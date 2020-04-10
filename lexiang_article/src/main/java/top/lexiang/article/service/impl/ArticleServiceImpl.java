package top.lexiang.article.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.aspectj.weaver.ast.Not;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import top.lexiang.article.client.NoticeClient;
import top.lexiang.article.entity.Notice;
import top.lexiang.article.mapper.ArticleMapper;
import top.lexiang.article.service.ArticleService;
import top.lexiang.common.entity.ResultCode;
import top.lexiang.common.exception.CommonException;
import top.lexiang.common.query.QWrapper;
import top.lexiang.common.utils.IdWorker;
import top.lexiang.article.entity.Article;

import java.util.Map;
import java.util.Set;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private NoticeClient noticeClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //存于redis的订阅集合前缀、rabbit订阅队列的前缀
    private String SUBSCRIBE_USERKEY_PRI = "article_subscribe_";
    private String SUBSCRIBE_AUTHORKEY_PRI = "article_author_";
    //存于redis的文章点赞集合前缀
    private String ARTICLE_THUMBUP_PRI = "thumbup_article_";
    //rabbitmq订阅交换机
    private String ARTICLE_SUBSCRIBE_EXCHSNGE = "article_subscribe";

    @Override
    @Transactional
    public void save(Article article) throws CommonException {
        //TODO 获取当前登录用户id
        String authorId = "1";

        String id = idWorker.nextId() + "";
        article.setId(id);
        article.setUserid(authorId);

        if (!StringUtils.isEmpty(article.getState())){
            throw new CommonException(ResultCode.UNAUTHORISE);
        } else {
            articleMapper.insert(article);
        }

        //TODO 文章发布后通知对应文章类型的管理员

        //暂时先不用审核文章 通知订阅作者的读者发送
        String authorKey = SUBSCRIBE_AUTHORKEY_PRI + authorId;
        Set<String> set = redisTemplate.boundSetOps(authorKey).members();

        Notice notice = new Notice();
        for (String uid : set) {
            //消息通知
            notice.setReceiverId(uid);
            notice.setOperatorId(authorId);
            notice.setAction("publish"); //消息类型是文章发布
            notice.setTargetType("article");
            notice.setTargetId(id);
            notice.setType("system");

            noticeClient.save(notice);
        }
    }

    @Override
    public Article findById(String id) {
        return articleMapper.selectById(id);
    }

    @Override
    public Page searchLike(Map<String, Object> map, Integer page, Integer size) {
        QWrapper<Article> wrapper = new QWrapper<>();

        //TODO 管理员可以忽略该步骤
        //用户看到的必须是已经公开、已经发布的视频
        wrapper.eq("ispublic", "1");
        wrapper.eq("state", 1);

        if (map != null) {
            Set<String> fieldSet = map.keySet();
            for (String field : fieldSet) {
                //动态sql
                wrapper.like(null != map.get(field), field, map.get(field));
            }
        }

        Page r_page = new Page(page, size);
        articleMapper.selectPage(r_page, wrapper);
        return r_page;
    }

    @Override
    public int update(String id, Article article) throws CommonException {
        //TODO 判断用户角色 成员则判断修改的state（审核状态）、点赞数等 必须为空
        if (StringUtils.isEmpty(article.getState())) {
            article.setId(id);
            return articleMapper.updateById(article);
        } else {
            throw new CommonException(ResultCode.UNAUTHORISE);
        }
    }

    @Override
    public int delete(String id) {
        return articleMapper.deleteById(id);
    }

    @Override
    @Transactional
    public Boolean subscribe(String articleId) {
        //根据文章id查询文章作者id
        String authorId = articleMapper.selectById(articleId).getUserid();

        //TODO 用户id设置为登录的用户id
        String userId = "3";

        //创建Rabbit管理器
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitTemplate.getConnectionFactory());

        //声明exchange
        DirectExchange exchange = new DirectExchange(ARTICLE_SUBSCRIBE_EXCHSNGE);
        rabbitAdmin.declareExchange(exchange);

        //创建queue
        Queue queue = new Queue(ARTICLE_THUMBUP_PRI + userId, true);

        //声明exchange和queue的绑定关系，设置路由键为作者id
        Binding bingding = BindingBuilder.bind(queue).to(exchange).with(authorId);


        //用户自己订阅的对象集合
        String userKey = SUBSCRIBE_USERKEY_PRI + userId;
        //订阅该用户的对象集合
        String authorKey = SUBSCRIBE_AUTHORKEY_PRI + authorId;

        //查询该用户是否已经订阅作者
        Boolean flag = redisTemplate.boundSetOps(userKey).isMember(authorId);

        if (flag) {
            //如果flag未true，已经订阅，则取消订阅
            redisTemplate.boundSetOps(userKey).remove(authorId);
            redisTemplate.boundSetOps(authorKey).remove(userId);
            return false;
        } else {
            //如果flag为false，没有订阅，则进行订阅
            redisTemplate.boundSetOps(userKey).add(authorId);
            redisTemplate.boundSetOps(authorKey).add(userId);
            return true;
        }

    }

    //文章点赞+点赞通知
    @Override
    @Transactional
    public Boolean thumbup(String articleId) {
        //文章点赞
        //TODO 获取用户ID
        String userId = "3";
        String key = ARTICLE_THUMBUP_PRI + userId + "_" + articleId;

        //根据用户id和文章id查询用户点赞信息 是否点赞
        Object data = redisTemplate.opsForValue().get(key);
        //从数据库查出点赞数量
        Article article = articleMapper.selectById(articleId);

        boolean flag = true;
        //判断查询结果是否为空
        if (data == null) {
            //如果为空，表示用户未对该文章点赞，可以点赞
            article.setThumbup(article.getThumbup() + 1);
            articleMapper.updateById(article);

            //完成点赞，保存点赞信息
            redisTemplate.opsForValue().set(key, 1);
        } else {
            //查询结果不为空，表示用户已经对该文章点赞，取消点赞
            article.setThumbup(article.getThumbup() - 1);
            articleMapper.updateById(article);

            //取消点赞，保存消息
            redisTemplate.delete(key);
            flag = false;
        }

        //文章点赞消息通知
        Notice notice = new Notice();
        notice.setReceiverId(article.getUserid());
        notice.setOperatorId(userId);
        notice.setAction("thumbup");
        notice.setTargetType("article");
        notice.setTargetId(articleId);
        notice.setType("user");

        noticeClient.save(notice);

        return flag;
    }


}
