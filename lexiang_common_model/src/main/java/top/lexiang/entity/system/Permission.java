package top.lexiang.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_permission")
public class Permission {

    private String id;//权限id

    private String description;//描述

    private String name; //权限名称

    //1级：系统管理权限（管理管理员及专栏） 2级：管理用户文章的发布。。
    private Integer level; //权限等级

    private String code; //标识

    private String columnId;

}
