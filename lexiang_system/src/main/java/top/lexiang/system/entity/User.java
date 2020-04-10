package top.lexiang.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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

    private Date regDate; //注册时间

    private Date updateDate; //修改时间

    private Date lastDate; //最后登录时间

    private Long online; //在线时长

    private String interest; //兴趣爱好

    private String personality; //个性签名

    private Integer fansCount; //粉丝数量

    private Integer followCount; //关注数量
}
