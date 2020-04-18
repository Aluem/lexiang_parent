package top.lexiang.article.service.impl;

import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.lexiang.article.repository.CommentRepository;
import top.lexiang.article.service.CommentService;
import top.lexiang.common.utils.IdWorker;
import top.lexiang.entity.article.Comment;

import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Comment findById(String commontid) {

        return commentRepository.findByCommentid(commontid);

    }

    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Override
    public void save(Comment comment) {
        String id = idWorker.nextId() + "";
        //设置自定义id
        comment.setCommentid(id);

        //初始化数据
        comment.setPublishdate(new Date());
        comment.setThumbup(0);

        //若是顶级评论 parentid设置为0
        if (comment.getParentid() == null) {
            comment.setParentid("0");
        }
        commentRepository.save(comment);
    }

    @Override
    public Long update(String commentid, String content) {

        Query query = new Query();
        query.addCriteria(Criteria.where("commentid").is(commentid));
        Update update = Update.update("content",content);
        UpdateResult result = mongoTemplate.upsert(query, update, "comment");
        return result.getModifiedCount();
    }

    @Override
    public int deleteByCommentid(String commentid) {
        int count = commentRepository.deleteByCommentid(commentid);
        return count;
    }

    @Override
    public List<Comment> findByarticleId(String articleId) {
        //先只获取顶级评论
        return commentRepository.findByArticleidAndParentid(articleId, "0");
    }

    /**
     * return: false 取消点赞成功
     *          true 点赞成功
     */
    @Override
    public boolean thumbup(String commentid, String userid) {
        //不能重复点赞

        //在redis中查询用户是否已经点赞
        String redisKey = "thumbup_" + userid + "_" + commentid;
        Object result = redisTemplate.opsForValue().get(redisKey);

        //修改条件
        Query query = new Query();
        query.addCriteria(Criteria.where("commentid").is(commentid));
        //修改的数据
        Update update = new Update();
        //如果已点赞，取消点赞
        if (result != null) {
            //1.删除redis的点赞数据
            redisTemplate.delete(redisKey);
            //2.对应评论点赞量在原基础上减一
            update.inc("thumbup", -1);
            mongoTemplate.updateFirst(query, update, "comment");
            return false;
        } else {
            //未点赞进行点赞操作
            //在原来基础上加一
            update.inc("thumbup", 1);
            //参数一：修改条件  参数二：修改的数据 参数三：mongoDB的集合名称
            mongoTemplate.updateFirst(query, update, "comment");
            //保存点赞记录
            redisTemplate.opsForValue().set(redisKey, 1);

            return true;
        }
    }
}
