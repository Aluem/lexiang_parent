package top.lexiang.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.lexiang.common.utils.IdWorker;
import top.lexiang.entity.system.Permission;
import top.lexiang.system.mapper.PermissionMapper;
import top.lexiang.system.service.PermissionService;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public Permission selectById(String id) {
        return permissionMapper.selectById(id);
    }

    @Override
    public void save(Permission permission) {
        String id = idWorker.nextId() + "";
        permission.setId(id);
        permissionMapper.insert(permission);
    }

    @Override
    public int update(String id, Permission permission) {
        permission.setId(id);
        return permissionMapper.updateById(permission);
    }

    @Override
    public int delete(String id) {
        return permissionMapper.deleteById(id);
    }
}
