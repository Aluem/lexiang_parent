package top.lexiang.article.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import top.lexiang.entity.article.Comment;

import java.util.List;

/**
 * 操作mongodb
 */
public interface CommentRepository extends MongoRepository<Comment, String> {

    /**
     * 根据自定义评论id查询
     */
    Comment findByCommentid(String commentid);

    /**
     * 根据文章id查询顶级评论
     */
    List<Comment> findByArticleidAndParentid(String articleId, String parentId);

    /**
     * 根据自定义id删除
     */
    int deleteByCommentid(String commentid);

}
