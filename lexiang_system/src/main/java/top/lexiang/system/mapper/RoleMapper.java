package top.lexiang.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.lexiang.entity.system.Permission;
import top.lexiang.entity.system.Role;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 通过角色获取到该角色所有权限
     */
    List<Permission> findAllPermission(@Param("roleId") String roleId);

}
