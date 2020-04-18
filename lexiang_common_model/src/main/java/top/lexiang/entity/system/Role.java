package top.lexiang.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_role")
public class Role implements Serializable {

    private String id;//角色id

    private String name;//用户名称

    private String description;//描述

    private String columnId;//专栏id

    private String code;//用户标识

}
