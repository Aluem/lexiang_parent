package top.lexiang.article;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import top.lexiang.common.utils.IdWorker;

@EnableFeignClients
@EnableEurekaClient
@EnableTransactionManagement
@EntityScan("top.lexiang.article.entity")
@MapperScan("top.lexiang.article.mapper")
@SpringBootApplication(scanBasePackages = "top.lexiang")
public class ArticleApprication {

    public static void main(String[] args) {
        SpringApplication.run(ArticleApprication.class, args);
    }

    @Bean
    public IdWorker idWorker() {
        return new IdWorker();
    }

}
