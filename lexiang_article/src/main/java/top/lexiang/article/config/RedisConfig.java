package top.lexiang.article.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * 设置 redisTemplate 的序列化设置
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        //1.创建 redisTemplate 模板
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        //2.关联 redisConnectionFactory
        template.setConnectionFactory(redisConnectionFactory);
        //3.创建序列化类 Generic:泛型 Serializer：序列化器
        GenericToStringSerializer genericToStringSerializer = new GenericToStringSerializer(Object.class);
        //4.序列化类，对象映射设置
        //5.设置 value 的转化格式和 key 的转化格式
        template.setValueSerializer(genericToStringSerializer);
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }

}
