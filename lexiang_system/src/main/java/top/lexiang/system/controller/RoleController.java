package top.lexiang.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.lexiang.common.entity.PageResult;
import top.lexiang.common.entity.Result;
import top.lexiang.common.entity.ResultCode;
import top.lexiang.entity.system.Role;
import top.lexiang.system.service.RoleService;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/system/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 根据id查询
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable("id") String id) {
        Role role = roleService.selectById(id);
        return new Result(ResultCode.SUCCESS, role);
    }

    /**
     * 保存 TODO 需要对应权限
     */
    @PostMapping("")
    public Result save(@RequestBody Role role) {
        roleService.save(role);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 修改 TODO 需要对应权限
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable("id") String id, @RequestBody Role role) {
        int count = roleService.update(id, role);
        if (count < 1) {
            return new Result(ResultCode.FAIL);
        }
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 删除 TODO 需要对应权限
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") String id) {
        int count = roleService.delete(id);
        if (count < 1) {
            return new Result(ResultCode.FAIL);
        }
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 分页查询
     */
    @PostMapping("/search/{page}/{size}")
    public Result selectPage(@PathVariable("page") Integer page,
                             @PathVariable("size") Integer size,
                             @RequestBody Map map) {
        Page r_page = roleService.selectPage(map, page, size);
        return new Result(ResultCode.SUCCESS, new PageResult(r_page.getTotal(), r_page.getRecords()));
    }
}
