package top.lexiang.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.lexiang.entity.system.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
