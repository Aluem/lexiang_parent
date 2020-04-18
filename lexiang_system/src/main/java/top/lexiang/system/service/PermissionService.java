package top.lexiang.system.service;


import top.lexiang.entity.system.Permission;

public interface PermissionService {

    /**
     * 根据id查询
     */
    Permission selectById(String id);

    /**
     * 保存
     */
    void save(Permission permission);

    /**
     * 修改
     */
    int update(String id, Permission permission);

    /**
     * 删除
     */
    int delete(String id);

}
