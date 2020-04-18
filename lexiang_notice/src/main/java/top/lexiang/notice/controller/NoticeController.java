package top.lexiang.notice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.lexiang.common.controller.BaseController;
import top.lexiang.common.entity.PageResult;
import top.lexiang.common.entity.Result;
import top.lexiang.common.entity.ResultCode;
import top.lexiang.entity.notice.Notice;
import top.lexiang.entity.notice.NoticeFresh;
import top.lexiang.notice.service.NoticeService;

@CrossOrigin
@RestController
@RequestMapping("/notice")
public class NoticeController extends BaseController {

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
        //设置进行操作用户的id  获取用户的角色 设置消息是系统消息（system）还是用户消息（uesr)
        notice.setOperatorId(this.userId); //操作者 A对B进行点赞 该参数是设置A的值
        noticeService.save(notice);
        return new Result(ResultCode.SUCCESS);
    }

    //4.修改通知
    @PutMapping()
    public Result updateById(@RequestBody Notice notice) {
        //判断该通知是否为本人所发送
        Notice notice1 = noticeService.selectById(notice.getId());
        if (!this.userId.equals(notice1.getOperatorId()))
        {
            return new Result(ResultCode.UNAUTHORISE);
        }
        int count = noticeService.updateById(notice);
        if (count < 1) {
            return new Result(ResultCode.TARGETISEMPTY);
        }
        return new Result(ResultCode.SUCCESS);
    }

    //5.根据用户id查询该用户的待推送消息
    @GetMapping("/fresh/{page}/{size}")
    public Result freshPage(@PathVariable("page") Integer page,
                            @PathVariable("size") Integer size)  {

        Page<NoticeFresh> pageData = noticeService.freshPage(this.userId, page, size);

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
        Notice notice = noticeService.selectById(id);
        //删除的消息是否属于该用户 且该用户不为管理员
        if (!this.userId.equals(notice.getOperatorId()) && StringUtils.isEmpty(this.roleId)) {
            return new Result(ResultCode.UNAUTHORISE);
        }
        int count = noticeService.delete(id);
        if (count < 1) {
            return new Result(ResultCode.TARGETISEMPTY);
        }
        return new Result(ResultCode.SUCCESS);
    }


}
