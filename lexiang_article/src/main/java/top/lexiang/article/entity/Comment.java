package top.lexiang.article.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

@Data
public class Comment implements Serializable {

    @Id
    private String _id; //mongo自动生成id

    private String commentid; //自定义评论id

    private String articleid; //文章id

    private String content; //评论内容

    private String userid; //用户id

    private String parentid; //父id 若为0则为顶级评论

    private Date publishdate; //评论日期

    private Integer thumbup; //点赞数


}
