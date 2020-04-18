package top.lexiang.common.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import top.lexiang.entity.system.Permission;
import top.lexiang.entity.system.User;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
public class ProfileResult implements Serializable {
    private String userId;
    private String mobile;
    private String nickName;
    private String roleId;
    private List permissions = new LinkedList();

    public ProfileResult(User user, List<Permission> list) {
        this.userId = user.getId();
        this.mobile = user.getMobile();
        this.nickName = user.getNickName();
        this.roleId = user.getRoleId();
        this.permissions = list;
    }

    public ProfileResult(User user) {
        this.userId = user.getId();
        this.mobile = user.getMobile();
        this.nickName = user.getNickName();
        this.roleId = user.getRoleId();
    }
}