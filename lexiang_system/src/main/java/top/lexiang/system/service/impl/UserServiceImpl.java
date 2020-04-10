package top.lexiang.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.lexiang.system.entity.User;
import top.lexiang.system.mapper.UserMapper;
import top.lexiang.system.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String mobile, String password) {
        QueryWrapper<User> wrapper = new QueryWrapper();
        wrapper.eq("mobile", mobile);
        wrapper.eq("password", password);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public User selectById(String id) {
        return userMapper.selectById(id);
    }
}
