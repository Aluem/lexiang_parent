package top.lexiang.common.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.lexiang.common.entity.Result;
import top.lexiang.common.entity.ResultCode;

@RestController
public class ErrorController {

    //公共错误跳转
    @RequestMapping(value = "/autherror")
    public Result autherror(int code) {
        return code ==1?new Result(ResultCode.UNAUTHENTICATED):new Result(ResultCode.UNAUTHORISE);
    }
}
