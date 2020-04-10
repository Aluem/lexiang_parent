package top.lexiang.article.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.lexiang.article.entity.Notice;
import top.lexiang.common.entity.Result;

@FeignClient(value = "lexiang-notice")
public interface NoticeClient {

    /**
     * 添加消息
     */
    @PostMapping("/notice")
    public Result save(@RequestBody Notice notice);
}
