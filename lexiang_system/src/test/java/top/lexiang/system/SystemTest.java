package top.lexiang.system;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import top.lexiang.entity.system.Permission;
import top.lexiang.entity.system.Role;
import top.lexiang.system.service.RoleService;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SystemApplication.class)
public class SystemTest {

    @Autowired
    private RoleService roleService;

    @Test
    public void test01() {
        List<Permission> allPermission = roleService.findAllPermission("1");
        System.out.println(allPermission);
    }

    @Test
    public void test02() {
        Role role = roleService.selectById("1");
        System.out.println(role);
    }

}