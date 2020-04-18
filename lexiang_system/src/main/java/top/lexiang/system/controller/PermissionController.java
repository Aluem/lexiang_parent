package top.lexiang.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.lexiang.common.entity.Result;
import top.lexiang.common.entity.ResultCode;
import top.lexiang.entity.system.Permission;
import top.lexiang.system.service.PermissionService;

@CrossOrigin
@RestController
@RequestMapping("/system/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 根据id查询
     */
    @GetMapping("/{id}")
    public Result selectById(@PathVariable("id") String id) {
        Permission permission = permissionService.selectById(id);
        return new Result(ResultCode.SUCCESS, permission);
    }

    /**
     * 保存
     */
    @PostMapping("")
    public Result save(@RequestBody Permission permission) {
        permissionService.save(permission);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 修改
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable("id") String id, @RequestBody Permission permission) {
        int count = permissionService.update(id, permission);
        if (count < 1) {
            return new Result(ResultCode.FAIL);
        }
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") String id) {
        int count = permissionService.delete(id);
        if (count < 1) {
            return new Result(ResultCode.FAIL);
        }
        return new Result(ResultCode.SUCCESS);
    }
}
