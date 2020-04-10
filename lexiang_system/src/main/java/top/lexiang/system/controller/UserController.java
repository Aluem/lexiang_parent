package top.lexiang.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.lexiang.common.entity.Result;
import top.lexiang.common.entity.ResultCode;
import top.lexiang.system.entity.User;
import top.lexiang.system.service.UserService;

@RestController
@RequestMapping("/system/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login/{mobile}/{password}")
    public Result login(@PathVariable("mobile") String mobile,
                        @PathVariable("password") String password) {
        User user = userService.login(mobile, password);

        if(user != null) {
            return new Result(ResultCode.LOGINSUCCESS);
        }

        return new Result(ResultCode.PASSWORDERROR);
    }

    @GetMapping("/{id}")
    public Result selectById(@PathVariable("id") String id) {
        User user = userService.selectById(id);
        return new Result(ResultCode.SUCCESS, user);
    }

}
