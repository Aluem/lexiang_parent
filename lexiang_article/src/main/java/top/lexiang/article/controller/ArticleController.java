package top.lexiang.article.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.lexiang.article.service.ArticleService;
import top.lexiang.common.controller.BaseController;
import top.lexiang.common.entity.PageResult;
import top.lexiang.common.entity.Result;
import top.lexiang.common.entity.ResultCode;
import top.lexiang.common.exception.CommonException;
import top.lexiang.entity.article.Article;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/article")
public class ArticleController extends BaseController {

    @Autowired
    private ArticleService articleService;


    /**
     * 根据id查询
     */
    @GetMapping("/search/{id}")
    public Result getArticle(@PathVariable("id") String id) {
        Article article = articleService.findById(id);
        if (article == null) {
            return new Result(ResultCode.VALUEISEMPTY);
        }
        return new Result(ResultCode.SUCCESS, article);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public Result save(@RequestBody Article article) throws CommonException {
        article.setUserid(this.userId);
        articleService.save(article);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 根据条件分页查询
     */
    @PostMapping("/search/{page}/{size}")
    public Result getPage(@PathVariable("page") Integer page,
                          @PathVariable("size") Integer size,
                          @RequestBody(required = false) Map<String, Object> map) {

        Page r_page = articleService.searchLike(map, page, size, this.roleId);
        if (r_page.getTotal() < 1) {
            return new Result(ResultCode.VALUEISEMPTY);
        }
        return new Result(ResultCode.SUCCESS, new PageResult((Long)r_page.getTotal(), r_page.getRecords()));
    }

    /**
     * 修改
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable("id") String id, @RequestBody Article article) throws CommonException {

        int count = 0;
        if (StringUtils.isEmpty(this.roleId)){ //普通用户
            if (StringUtils.isEmpty(article.getState())) { //改的state（审核状态）、点赞数等 必须为空
                count = articleService.update(id, article);

            } else {
                throw new CommonException(ResultCode.UNAUTHORISE);
            }
        } else {//管理员

            count = articleService.update(id, article);
        }

        if (count < 1) {
            return new Result(ResultCode.TARGETISEMPTY);
        }
        return new Result(ResultCode.SUCCESS);

    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") String id) {
        //只能删除自己的文章
        Article article = articleService.findById(id);
        if (!this.userId.equals(article.getUserid())) { //删除的文章不属于本人
            return new Result(ResultCode.UNAUTHORISE);
        }
        int count = articleService.delete(id);
        if (count < 1) {
            return new Result(ResultCode.TARGETISEMPTY);
        }
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 订阅或者取消订阅文章作者
     */
    @PostMapping("/subscribe/{articleId}")
    public Result subscribe(@PathVariable("articleId") String articleId) {
        //根据文章id，订阅文章作者，返回订阅状态，true表示订阅成功，false取消订阅成功
        Boolean flag = articleService.subscribe(articleId, this.userId);

        if (flag) {
            return new Result(20000, "订阅成功", true);
        } else {
            return new Result(20000, "订阅取消", true);
        }
    }

    /**
     * 文章点赞
     */
    @PutMapping("/thumbup/{articleId}")
    public Result thumbup(@PathVariable("articleId") String articleId) {
        Boolean flag = articleService.thumbup(articleId, this.userId);
        if (flag) {
            return new Result(20000, "点赞成功", true);
        } else {
            return new Result(20000, "取消点赞", true);
        }
    }

}
