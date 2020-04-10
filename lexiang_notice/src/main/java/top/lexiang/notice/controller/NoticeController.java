package top.lexiang.notice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.lexiang.common.entity.PageResult;
import top.lexiang.common.entity.Result;
import top.lexiang.common.entity.ResultCode;
import top.lexiang.notice.entity.Notice;
import top.lexiang.notice.entity.NoticeFresh;
import top.lexiang.notice.service.NoticeService;

@CrossOrigin
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    //1.根据id查询消息通知
    @GetMapping("/{id}")
    public Result selectById(@PathVariable("id") String id) {
        Notice notice = noticeService.selectById(id);
        if (notice == null) {
            return new Result(ResultCode.VALUEISEMPTY);
        }
        return new Result(ResultCode.SUCCESS, notice);
    }

    //2.根据条件分页查询消息通知
    @PostMapping("/search/{page}/{size}")
    public Result selectByList(@PathVariable("page") Integer page,
                               @PathVariable("size") Integer size,
                               @RequestBody Notice notice) {
        Page<Notice> pageData = noticeService.selectByPage(notice, page, size);

        if (pageData.getRecords().size() == 0) {
            return new Result(ResultCode.VALUEISEMPTY);
        }
        PageResult<Notice> pageResult = new PageResult<>(pageData.getTotal(), pageData.getRecords());
        return new Result(ResultCode.SUCCESS, pageResult);
    }

    //3.新增通知
    @PostMapping()
    public Result save(@RequestBody Notice notice) {
        noticeService.save(notice);
        return new Result(ResultCode.SUCCESS);
    }

    //4.修改通知
    @PutMapping()
    public Result updateById(@RequestBody Notice notice) {
        int count = noticeService.updateById(notice);
        if (count < 1) {
            return new Result(ResultCode.TARGETISEMPTY);
        }
        return new Result(ResultCode.SUCCESS);
    }

    //5.根据用户id查询该用户的待推送消息
    @GetMapping("/fresh/{userId}/{page}/{size}")
    public Result freshPage(@PathVariable("userId") String userId,
                            @PathVariable("page") Integer page,
                            @PathVariable("size") Integer size)  {
        Page<NoticeFresh> pageData = noticeService.freshPage(userId, page, size);

        if (pageData == null) {
            return new Result(ResultCode.VALUEISEMPTY);
        }
        PageResult<NoticeFresh> pageResult = new PageResult<>(pageData.getTotal(), pageData.getRecords());
        return new Result(ResultCode.SUCCESS, pageResult);
    }

    //6.删除待推送消息
    @DeleteMapping("/fresh")
    public Result freshDelete(@RequestBody NoticeFresh noticeFresh) {
        int count = noticeService.freshDelete(noticeFresh);
        if (count < 1) {
            return new Result(ResultCode.TARGETISEMPTY);
        }
        return new Result(ResultCode.SUCCESS);
    }

    //7.删除消息
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") String id) {
        int count = noticeService.delete(id);
        if (count < 1) {
            return new Result(ResultCode.TARGETISEMPTY);
        }
        return new Result(ResultCode.SUCCESS);
    }


}
