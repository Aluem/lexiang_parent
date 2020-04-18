package top.lexiang.article.service;


import top.lexiang.entity.article.Comment;

import java.util.List;

public interface CommentService {

    /**
     * 根据id查询
     */
    Comment findById(String id);

    /**
     * 查询所有
     */
    List<Comment> findAll();

    /**
     * 保存
     */
    void save(Comment comment);

    /**
     * 修改
     */
    Long update(String commentid, String content);

    /**
     * 删除
     * @return
     */
    public int deleteByCommentid(String id);

    /**
     * 根据文章id查询
     */
    List<Comment> findByarticleId(String articleId);

    /**
     * 评论点赞
     * @return
     */
    boolean thumbup(String id, String userid);

}
