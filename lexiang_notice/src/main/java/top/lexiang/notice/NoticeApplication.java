package top.lexiang.notice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import top.lexiang.common.utils.IdWorker;

@EnableEurekaClient
@EnableFeignClients
@EnableTransactionManagement
@EntityScan("top.lexiang.notice.entity")
@MapperScan("top.lexiang.notice.mapper")
@SpringBootApplication(scanBasePackages = "top.lexiang")
public class NoticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoticeApplication.class, args);
    }

    @Bean
    public IdWorker idWorker() {
        return new IdWorker();
    }

}
