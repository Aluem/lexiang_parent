package top.lexiang.entity.notice;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tb_notice")
@NoArgsConstructor
@AllArgsConstructor
public class Notice implements Serializable {

    @TableId(type = IdType.INPUT)
    private String id; //ID
    private String receiverId;//接收消息的用户ID
    private String operatorId;//进行操作的用户ID

    @TableField(exist = false)
    private String operatorName;//进行操作的用户昵称
    private String action;//操作类型（评论，点赞登）
    private String targetType;//对象类型（评论，点赞等）

    @TableField(exist = false)
    private String targetName; //对象名称或间接
    private String targetId;//对象id

    @TableField(fill = FieldFill.INSERT)
    private Date createtime;//创建日期

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatetime; //修改日期
    private String type;//消息类型
    private String state; //消息状态（0未读 1已读）
}
