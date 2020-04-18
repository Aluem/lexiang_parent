package top.lexiang.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.lexiang.common.utils.IdWorker;
import top.lexiang.entity.system.Permission;
import top.lexiang.entity.system.Role;
import top.lexiang.system.mapper.RoleMapper;
import top.lexiang.system.service.RoleService;

import java.util.List;
import java.util.Map;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private IdWorker idWorker;

    /**
     * 通过角色获取到该角色所有权限
     */
    public List<Permission> findAllPermission(String roleId) {
        return roleMapper.findAllPermission(roleId);
    }

    @Override
    public void save(Role role) {
        String id = idWorker.nextId() + "";
        role.setId(id);
        roleMapper.insert(role);
    }

    @Override
    public Role selectById(String id) {
        return roleMapper.selectById(id);
    }

    @Override
    public int update(String id, Role role) {
        role.setId(id);
        return roleMapper.updateById(role);
    }

    @Override
    public int delete(String id) {
        return roleMapper.deleteById(id);
    }

    @Override
    public Page selectPage(Map map, Integer page, Integer size) {
        QueryWrapper<Role> wrapper = new QueryWrapper<>();

        if (!StringUtils.isEmpty(map.get("name"))) {
            wrapper.like("name", map.get("name"));
        }
        if (!StringUtils.isEmpty(map.get("column_id"))) {
            wrapper.eq("column_id", map.get("column_id"));
        }
        if (!StringUtils.isEmpty(map.get("code"))) {
            wrapper.eq("code", map.get("code"));
        }

        Page r_page = new Page(page, size);
        roleMapper.selectPage(r_page, wrapper);
        return r_page;
    }

    @Override
    public Role selectByNameAndColum(String name, String columnId) {
        QueryWrapper<Role> wrapper = new QueryWrapper();
        wrapper.eq("name", name);
        wrapper.eq("column_id", columnId);
        return roleMapper.selectOne(wrapper);
    }
}
