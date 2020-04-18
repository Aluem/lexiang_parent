package top.lexiang.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_user")
public class User implements Serializable {

    @TableId(type = IdType.INPUT)
    private String id;

    private String mobile;//手机号码

    private String password;//密码

    private String nickName; //昵称

    private String sex; //性别

    private Date birthday; //生日

    private String avatar; //头像

    private String email;

    @TableField(fill = FieldFill.INSERT)
    private Date createtime; //注册时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatetime; //修改时间

    private Date lastDate; //最后登录时间

    private Long online; //在线时长

    private String interest; //兴趣爱好

    private String personality; //个性签名

    private Integer fansCount; //粉丝数量

    private Integer followCount; //关注数量

    private String roleId;//普通用户(null) 管理员(对应id)
}
