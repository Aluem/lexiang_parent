package top.lexiang.common.shiro.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import top.lexiang.common.response.ProfileResult;
import top.lexiang.entity.system.Permission;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 公共的realm，获取安全数据，构造权限信息
 */
public class LexiangRealm extends AuthorizingRealm {

    public void setName(String name) {
        super.setName("lexiangRealm");
    }

    //授权方法
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //1.获取安全数据
        ProfileResult result = (ProfileResult) principalCollection.getPrimaryPrincipal();
        //2.获取权限信息
        List<Permission> permissions = (List<Permission>) result.getPermissions();

        Set<String> pers = new HashSet<>();

        String codeAndCId = null;
        for (Permission permission : permissions) {
            codeAndCId = permission.getColumnId() + "_" + permission.getColumnId();
            pers.add(codeAndCId);
        }
        //3.构造权限数据，返回值
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(pers);
        return info;
    }

    //认证方法
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        return null;
    }
}
