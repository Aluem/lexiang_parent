package top.lexiang.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.lexiang.common.utils.IdWorker;
import top.lexiang.entity.system.User;
import top.lexiang.system.mapper.UserMapper;
import top.lexiang.system.service.UserService;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private IdWorker idWorker;

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

    @Override
    public void save(User user) {
        String id = idWorker.nextId() + "";
        user.setId(id);
        //保存的密码进行加密
        String password = new Md5Hash(user.getPassword(), user.getMobile(), 3).toString();
        user.setPassword(password);
        userMapper.insert(user);
    }

    @Override
    public int update(String id, User user) {
        user.setId(id);
        return userMapper.updateById(user);
    }

    @Override
    public int delete(String id) {
        return userMapper.deleteById(id);
    }

    @Override
    public Page getPage(Map map, Integer page, Integer size) {
        QueryWrapper<User> wrapper = new QueryWrapper();

        if (!StringUtils.isEmpty(map.get("createtime"))) {
            wrapper.ge("createtime", map.get("createtime"));
        }

        if (!StringUtils.isEmpty(map.get("mobile"))) {
            wrapper.like("mobile", map.get("mobile"));
        }

        Page r_page = new Page(page, size);
        userMapper.selectPage(r_page, wrapper);
        return r_page;

    }

    @Override
    public User findByMobile(String mobile) {
        QueryWrapper<User> wrapper = new QueryWrapper();
        wrapper.eq("mobile", mobile);
        return userMapper.selectOne(wrapper);
    }
}
