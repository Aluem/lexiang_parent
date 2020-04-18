package top.lexiang.system.shiro.realm;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import top.lexiang.common.response.ProfileResult;
import top.lexiang.common.shiro.realm.LexiangRealm;
import top.lexiang.entity.system.Permission;
import top.lexiang.entity.system.User;
import top.lexiang.system.service.RoleService;
import top.lexiang.system.service.UserService;

import java.util.List;

public class UserRealm extends LexiangRealm {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    //认证方法
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) {
        //1.获取用户的手机号和密码
        UsernamePasswordToken upToken = (UsernamePasswordToken) authenticationToken;
        String mobile = upToken.getUsername();
        String password = new String(upToken.getPassword());

        //2.根据手机号查询用户
        User user = userService.findByMobile(mobile);

        //3.判断用户是否存在，用户密码是否和输入密码一致
        if (user != null && user.getPassword().equals(password)) {

            //4.构造安全数据并返回（安全数据：用户基本数据，权限信息 profileResult）
            ProfileResult result = null;

            if (StringUtils.isEmpty(user.getRoleId())) {
                //普通用户，无特殊权限
                result = new ProfileResult(user);
            } else {
                //管理员 查询响应权限返回
                List<Permission> list = roleService.findAllPermission(user.getRoleId());
                result = new ProfileResult(user, list);
            }

            //构造方法：安全数据，密码，realm域名
            SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(result, user.getPassword(), this.getName());
            return info;
        }

        //用户名密码不匹配，返回null
        return null;
    }

}
