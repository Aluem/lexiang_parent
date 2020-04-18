package top.lexiang.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.lexiang.entity.system.User;

import java.util.Map;


public interface UserService {

    User login(String mobile, String password);

    /**
     * 根据id查询
     */
    User selectById(String id);

    /**
     * 保存
     */
    void save(User user);

    /**
     * 修改
     */
    int update(String id, User user);

    /**
     * 删除
     */
    int delete(String id);

    /**
     * 分页查询
     */
    Page getPage(Map map, Integer page, Integer size);

    /**
     * 根据手机号查询
     */
    User findByMobile(String mobile);
}
