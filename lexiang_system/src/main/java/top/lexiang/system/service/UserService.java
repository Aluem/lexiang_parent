package top.lexiang.system.service;

import top.lexiang.system.entity.User;

public interface UserService {

    User login(String mobile, String password);

    User selectById(String id);
}
