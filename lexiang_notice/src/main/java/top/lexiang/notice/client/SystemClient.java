package top.lexiang.notice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.lexiang.common.entity.Result;

@FeignClient(value = "lexiang-system")
public interface SystemClient {

    /**
     * 根据id查询用户
     */
    @GetMapping("system/user/{id}")
    public Result findById(@PathVariable("id") String id);

}
