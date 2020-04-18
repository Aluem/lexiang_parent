package top.lexiang.article.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.lexiang.article.service.CommentService;
import top.lexiang.common.controller.BaseController;
import top.lexiang.common.entity.Result;
import top.lexiang.common.entity.ResultCode;
import top.lexiang.common.exception.CommonException;
import top.lexiang.entity.article.Comment;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/article/comment")
public class CommentController extends BaseController {

    @Autowired
    private CommentService commentService;

    //根据id查询
    @GetMapping("/get/{id}")
    public Result findById(@PathVariable(value = "id") String commentid) {
        Comment comment = commentService.findById(commentid);
        if (comment == null) {
            return new Result(ResultCode.VALUEISEMPTY);
        }
        return new Result(ResultCode.SUCCESS, comment);
    }

    //查询所有
    @GetMapping("/get")
    public Result findAll() {
        List<Comment> list = commentService.findAll();
        if (list.size() < 1) {
            return new Result(ResultCode.VALUEISEMPTY);
        }
        return new Result(ResultCode.SUCCESS, list);
    }

    //新增
    @PostMapping("")
    public Result save(@RequestBody Comment comment) {
        comment.setUserid(this.userId);
        commentService.save(comment);
        return new Result(ResultCode.SUCCESS);
    }

    //修改
    @PutMapping("/{id}")
    public Result update(@PathVariable("id") String commentid, @RequestBody Map<String ,String> map) throws CommonException {

        Long count = 0L;
        //判断修改的评论是否属于当前登录用户
        Comment comment = commentService.findById(commentid);
        if (!this.userId.equals(comment.getUserid())) {
            return new Result(ResultCode.UNAUTHORISE);
        }
        count = commentService.update(commentid, map.get("content"));
        if (count < 1) {
            return new Result(ResultCode.FAIL);
        }

        return new Result(ResultCode.SUCCESS);
    }

    //删除
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable("id") String commentid) throws CommonException {

        //判断删除的评论是否属于当前登录用户
        Comment comment = commentService.findById(commentid);
        if (!this.userId.equals(comment.getUserid())) {
            return new Result(ResultCode.UNAUTHORISE);
        }

        int count = commentService.deleteByCommentid(commentid);
        if (count < 1) {
            throw new CommonException(ResultCode.TARGETISEMPTY);
        }
        return new Result(ResultCode.SUCCESS);
    }

    //根据文章id查询评论
    @GetMapping("/top/{articleId}")
    public Result finByarticleId(@PathVariable("articleId") String articleId) {
        List<Comment> list = commentService.findByarticleId(articleId);
        if (list.size() < 1) {
            return new Result(ResultCode.VALUEISEMPTY);
        }
        return new Result(ResultCode.SUCCESS, list);
    }

    //评论点赞
    @PutMapping("/thumbup/{id}")
    public Result thumbup(@PathVariable("id") String commentid) {
        boolean flag = commentService.thumbup(commentid, this.userId);
        if (!flag) {
            return new Result(20000,"取消点赞成功", true);
        } else {
            return new Result(20000,"点赞成功", true);
        }
        }

}
