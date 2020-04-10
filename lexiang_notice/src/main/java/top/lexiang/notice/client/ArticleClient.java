package top.lexiang.notice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.lexiang.common.entity.Result;

@FeignClient(value = "lexiang-article")
public interface ArticleClient {

    /**
     * 根据ID查询文章
     */
    @GetMapping("/article/{articleId}")
    public Result findByProblemId(@PathVariable("articleId") String articleId);


}
