package top.lexiang.article.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.lexiang.common.exception.CommonException;
import top.lexiang.article.entity.Article;

import java.util.Map;

public interface ArticleService {

    //保存
    void save(Article article) throws CommonException;
    //id查询
    Article findById(String id);
    //条件分页查询
    Page searchLike(Map<String, Object> map, Integer page, Integer size);
    //修改
    int update(String id, Article article) throws CommonException;
    //删除
    int delete(String id);

    //订阅文章作者
    Boolean subscribe(String articleId);

    //文章点赞
    Boolean thumbup(String articleId);

}
