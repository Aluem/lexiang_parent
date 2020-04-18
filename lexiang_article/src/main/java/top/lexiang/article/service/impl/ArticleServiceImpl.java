package top.lexiang.article.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import top.lexiang.article.mapper.ArticleMapper;
import top.lexiang.article.service.ArticleService;
import top.lexiang.common.entity.ResultCode;
import top.lexiang.common.exception.CommonException;
import top.lexiang.common.query.QWrapper;
import top.lexiang.common.utils.IdWorker;
import top.lexiang.entity.article.Article;
import top.lexiang.entity.notice.Notice;

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

    /**
     * 新增文章
     */
    @Override
    @Transactional
    public void save(Article article) throws CommonException {

        String authorId = article.getUserid(); //作者id

        String id = idWorker.nextId() + "";
        article.setId(id);

        if (!StringUtils.isEmpty(article.getState())){
            throw new CommonException(ResultCode.UNAUTHORISE);
        } else {
            articleMapper.insert(article);
        }

        //TODO 文章发布后通知对应文章类型的管理员

        //入库成功后，发送mq消息，内容是消息通知id
        //第一个参数：使用 订阅者名称的 交互机  第二个参数：作者id作为路由键 第三个参数：存放新文章的id
        rabbitTemplate.convertAndSend(ARTICLE_SUBSCRIBE_EXCHSNGE, authorId, id);

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
    public Page searchLike(Map<String, Object> map, Integer page, Integer size, String roleId) {
        QWrapper<Article> wrapper = new QWrapper<>();

        // 管理员可以忽略该步骤
        if(StringUtils.isEmpty(roleId)){
            //用户看到的必须是已经公开、已经发布的视频
            wrapper.eq("ispublic", "1");
            wrapper.eq("state", 1);
        }


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
            article.setId(id);
            return articleMapper.updateById(article);
    }

    @Override
    public int delete(String id) {
        return articleMapper.deleteById(id);
    }

    /**
     * 订阅文章作者
     */
    @Override
    @Transactional
    public Boolean subscribe(String articleId, String userId) {
        //根据文章id查询文章作者id
        String authorId = articleMapper.selectById(articleId).getUserid();

        //1.创建Rabbit管理器
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitTemplate.getConnectionFactory());

        //2.声明Direct类型交换机，处理新增文章消息
        DirectExchange exchange = new DirectExchange(ARTICLE_SUBSCRIBE_EXCHSNGE);
        rabbitAdmin.declareExchange(exchange);

        //3.创建队列，每个用户都有自己的队列，通过用户id进行区分
        Queue queue = new Queue(SUBSCRIBE_USERKEY_PRI + userId, true);

        //4.声明exchange和queue的绑定关系，
        // 通过路由键进行绑定作者，队列只收到绑定作者的文章消息
        //第一个是队列，第二个是交换机，第三个是路由键作者id
        Binding bingding = BindingBuilder.bind(queue).to(exchange).with(authorId);

        //存放用户订阅作者
        String userKey = SUBSCRIBE_USERKEY_PRI + userId;

        //存放作者的订阅者
        String authorKey = SUBSCRIBE_AUTHORKEY_PRI + authorId;

        //查询该用户是否已经订阅作者
        Boolean flag = redisTemplate.boundSetOps(userKey).isMember(authorId);

        if (flag) {
            //如果flag未true，已经订阅，则取消订阅
            redisTemplate.boundSetOps(userKey).remove(authorId);
            redisTemplate.boundSetOps(authorKey).remove(userId);

            //删除绑定的队列
            rabbitAdmin.removeBinding(bingding);

            return false;
        } else {
            //如果flag为false，没有订阅，则进行订阅
            redisTemplate.boundSetOps(userKey).add(authorId);
            redisTemplate.boundSetOps(authorKey).add(userId);

            //声明队列和绑定队列
            rabbitAdmin.declareQueue(queue);
            rabbitAdmin.declareBinding(bingding);

            return true;
        }

    }

    //文章点赞+点赞通知
    @Override
    @Transactional
    public Boolean thumbup(String articleId, String userId) {
        //文章点赞

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

        //1.创建Rabbit管理器
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitTemplate.getConnectionFactory());

        //2.创建队列，每个用户都有自己的队列，通过作者id进行区分
        Queue queue = new Queue(ARTICLE_THUMBUP_PRI + article.getUserid(), true);
        rabbitAdmin.declareQueue(queue);
        //3.发消息到队列中
        rabbitTemplate.convertAndSend(ARTICLE_THUMBUP_PRI + article.getUserid(), articleId);

        return flag;
    }


}
