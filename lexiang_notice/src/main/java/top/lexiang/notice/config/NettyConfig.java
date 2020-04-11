package top.lexiang.notice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.lexiang.notice.netty.NettyServer;

/**
 * netty服务配置类
 */
@Configuration
public class NettyConfig {

    @Bean
    public NettyServer createNettyServer() {
        NettyServer nettyServer = new NettyServer();

        //启动Netty服务，使用新的线程启动
        new Thread(() -> nettyServer.start(1234)).start();

        //上面是下面这个写法的新写法
        /*
        new Thread(){
            @Override
            public void run() {
               nettyServer.start(1234);
            }
        }.start();
        */
        return nettyServer;
    }

}
