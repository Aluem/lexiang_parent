package top.lexiang.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.lexiang.common.controller.BaseController;
import top.lexiang.common.entity.PageResult;
import top.lexiang.common.entity.Result;
import top.lexiang.common.entity.ResultCode;
import top.lexiang.common.response.ProfileResult;
import top.lexiang.entity.system.User;
import top.lexiang.system.service.UserService;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/system/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    //模拟登录
    //@PostMapping("/login")
    public Result login() {
        Map map = new HashMap();
        map.put("token","admin");
        return new Result(ResultCode.SUCCESS, map);
    }

    @GetMapping("/info")
    public Result info() {
        Map map = new HashMap();
        map.put("roles","[admin]");
        map.put("name","admin");
        map.put("avatar", "https:wping.wallstcn.com/f7788738c-e4f8-48f8-4870-b634-56703b4acafe.gif");
        return new Result(ResultCode.SUCCESS, map);
    }

    /**
     * 用户登录，shiro验证
     */
    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> map) {
        try {
            String mobile = map.get("username");
            String password = map.get("password");
            //1.构造登录令牌UsernamePasswordToken
            //加密密码 1.密码 2.盐  3.加密次数
            password = new Md5Hash(password, mobile, 3).toString();
            System.out.println(password);
            UsernamePasswordToken upToken = new UsernamePasswordToken(mobile, password);

            //2.获取subject
            Subject subject = SecurityUtils.getSubject();

            //3.调用login方法，进入realm完成认证
            subject.login(upToken);

            //4.获取sessionid ?sessionid从哪来 猜测：调用subject.login(upToken)后会根据upToken返回唯一的sessionid
            String sessionId = (String) subject.getSession().getId();

            //5.构造返回结果
            map.put("token",sessionId);
            return new Result(ResultCode.SUCCESS, map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(ResultCode.PASSWORDERROR);
        }
    }

    /**
     * 用户登录成功后，获取用户信息
     *      1.获取用户id
     *      2.根据用户id查询用户
     *      3.构建返回值对象
     *      4.响应
     */
    @PostMapping("/profile")
    public Result profile() {
        //获取session中的安全数据
        Subject subject = SecurityUtils.getSubject();
        //1.subject获取所有的安全数据集合
        PrincipalCollection principals = subject.getPrincipals();
        //2.获取安全数据
        ProfileResult result = (ProfileResult) principals.getPrimaryPrincipal();
        return new Result(ResultCode.SUCCESS, result);
    }

    @GetMapping("/get/{id}")
    public Result selectById(@PathVariable("id") String id) {
        User user = userService.selectById(id);
        return new Result(ResultCode.SUCCESS, user);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public Result save(@RequestBody User user) {

        if (user.getCreatetime() != null ||
                user.getOnline() != null ||
                user.getFansCount() != null ||
                user.getFollowCount() != null) {
            return new Result(ResultCode.INSERTVALUEISEMPTY);
        }

        userService.save(user);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 修改
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable("id") String id, @RequestBody User user){

        if (user.getCreatetime() != null ||
                user.getOnline() != null ||
                user.getFansCount() != null ||
                user.getFollowCount() != null) {
            return new Result(ResultCode.INSERTVALUEISEMPTY);
        }

        int count = userService.update(id, user);
        if (count < 1) {
            return new Result(ResultCode.TARGETISEMPTY);
        }
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") String id){
        int count = userService.delete(id);
        if (count < 1) {
            return new Result(ResultCode.TARGETISEMPTY);
        }
        return new Result(ResultCode.SUCCESS);
    }

    @PostMapping("/search/{page}/{size}")
    public Result selectPage(@PathVariable("page") Integer page,
                             @PathVariable("size") Integer size,
                             @RequestBody Map map) {
        Page r_page = userService.getPage(map, page, size);
        return new Result(ResultCode.SUCCESS, new PageResult(r_page.getTotal(), r_page.getRecords()));
    }

}
