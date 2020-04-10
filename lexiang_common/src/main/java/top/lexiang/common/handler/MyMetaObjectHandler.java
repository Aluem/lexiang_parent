package top.lexiang.common.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

/**
 * 配置插入值时自动填充时间等
 */
public class MyMetaObjectHandler implements MetaObjectHandler {


    //在mp执行添加操作时运行
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createtime",new Date(), metaObject);
        this.setFieldValByName("updatetime",new Date(), metaObject);
    }

    //在mp执行修改操作时运行
    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updatetime", new Date(), metaObject);
    }
}
