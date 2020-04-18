package top.lexiang.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.lexiang.entity.system.Permission;
import top.lexiang.entity.system.Role;

import java.util.List;
import java.util.Map;

public interface RoleService {

    /**
     * 通过角色获取到该角色所有权限
     */
    List<Permission> findAllPermission(String roleId);

    /**
     * 保存
     */
    void save(Role role);

    /**
     * 根据id查询
     */
    Role selectById(String id);

    /**
     * 根据id修改
     */
    int update(String id, Role role);

    /**
     * 根据id删除
     */
    int delete(String id);

    /**
     * 分页查询
     */
    Page selectPage(Map map, Integer page, Integer size);

    /**
     * 根据角色名称及专栏获取角色
     */
    Role selectByNameAndColum(String name, String columnId);


}
