package top.lexiang.notice.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import top.lexiang.common.entity.Result;
import top.lexiang.common.entity.ResultCode;
import top.lexiang.notice.config.ApplicationContextProvider;

import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通讯处理类，进行MQ和WebSocket的消息处理
 */
public class MyWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * 步骤：
     *  1.通过请求数据确定请求用户
     *  2.第一次请求建立WebSocket连接并存放在map容器中
     *  3.获取RabbitMQ消息内容
     *  4.使用WebSocket发送给用户
     *  5.通过监听器监听rabbitmq消息
     */

    //SpringMVC自带的json解析工具类
    private static ObjectMapper MAPPER = new ObjectMapper();

    private String SUBSCRIBE_USERKEY_PRI = "article_subscribe_";
    private String ARTICLE_THUMBUP_PRI = "thumbup_article_";

    //从Spring容器中获取消息监听器容器，处理订阅消息sysNotice
    SimpleMessageListenerContainer sysNoticeContainer = (SimpleMessageListenerContainer)
            ApplicationContextProvider.getApplicationContext().getBean("sysNoticeContainer");

    // 送Spring容器中获取消息监听器容器,处理点赞消息userNotice
    SimpleMessageListenerContainer userNoticeContainer = (SimpleMessageListenerContainer) ApplicationContextProvider.getApplicationContext()
            .getBean("userNoticeContainer");

    //从Spring容器中获取RabbitTemplate
    //TODO 视频中老师说是因为该类没有@Component等注解 不能使用@Autowired注入
    RabbitTemplate rabbitTemplate = ApplicationContextProvider
            .getApplicationContext().getBean(RabbitTemplate.class);

    //存放WebSocket连接Map，根据用户id存放 ConcurrentHashMap是一个线程安全的Map容器
    //key：用户id value：连接
    public static ConcurrentHashMap<String, Channel> userChannelMap = new ConcurrentHashMap<>();

    /**
     * 一、用户第一次连接时（登录）获取消息
     * 用户请求WebSocket服务端，执行的方法
     * @param ctx 可以获取socket连接
     * @param msg 封装请求数据
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        //约定用户第一次请求携带的数据：{"userId":"1"}
        //获取用户请求数据并解析
        String json = msg.text();
        //解析json数据，获取用户id
        String userId = MAPPER.readTree(json).get("userId").asText();

        //第一次请求的时候，需要建立WebSocket连接
        Channel channel = userChannelMap.get(userId);
        if (channel == null) {
            //获取webSocket的连接
            channel = ctx.channel();

            //把连接放到容器中 当用户断开连接时应该清楚其容器里的连接
            userChannelMap.put(userId, channel);
        }

        //只用完成新消息的提醒即可，只需要获取消息的数量
        //获取RabbitMQ的消息内容，并发送给用户
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitTemplate);
        //拼接获取队列名称
        String queueName = SUBSCRIBE_USERKEY_PRI + userId;
        //获取Rabbit的Properties容器 获取新消息的数量
        Properties queueProperties = rabbitAdmin.getQueueProperties(queueName);

        //获取消息数量
        int noticeCount = 0;
        //判断Properties是否不为空
        if (queueProperties != null) {
            //如果不为空，获取消息的数量
            noticeCount = (int) queueProperties.get("QUEUE_MESSAGE_COUNT");
        }

        //-----------------------------------------------------
        //拼接获取队列名称
        String userQueueName = ARTICLE_THUMBUP_PRI + userId;
        //创建队列
        Queue queue = new Queue(userQueueName, true);
        rabbitAdmin.declareQueue(queue);

        //获取Rabbit的Properties容器
        Properties userQueueProperties = rabbitAdmin.getQueueProperties(userQueueName);

        //获取消息数量
        int userNoticeCount = 0;
        //判断Properties是否不为空
        if (userQueueProperties != null) {
            // 如果不为空，获取消息的数量
            userNoticeCount = (int) userQueueProperties.get("QUEUE_MESSAGE_COUNT");
        }

        //封装返回的数据
        HashMap countMap = new HashMap();
        countMap.put("sysNoticeCount", noticeCount);
        countMap.put("userNoticeCount", userNoticeCount);
        Result result = new Result(ResultCode.SUCCESS, countMap);

        //使用WebSoket把数据发送给用户
        channel.writeAndFlush(new TextWebSocketFrame(MAPPER.writeValueAsString(result)));

        //主动获取消息后，把消息从队列里面清空，否则MQ消息监听器会再次消费一次
        if (noticeCount > 0) {
            rabbitAdmin.purgeQueue(queueName, true);
        }

        if (userNoticeCount > 0) {
            rabbitAdmin.purgeQueue(userQueueName, true);
        }

        /**
         * 二、推送消息
         * 为用户的消息通知队列注册监听器，便于用户在线的时候，
         *  一旦有消息，可以主动推送给用户，不需要用户请求服务器获取数据
         */
        //通过队列名称监听队列信息
        sysNoticeContainer.addQueueNames(queueName);
        userNoticeContainer.addQueueNames(userQueueName);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //添加连接
        System.out.println("客户端加入连接："+ctx.channel());
        //ChannelSupervise.addChannel(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //断开连接
        System.out.println("客户端断开连接："+ctx.channel());
        //ChannelSupervise.removeChannel(ctx.channel());
        ConcurrentHashMap.KeySetView<String, Channel> strings = userChannelMap.keySet();
        for (String string : strings) {
            if (userChannelMap.get(string).equals(ctx.channel())) {
                userChannelMap.remove(string);
            }
        }
    }

}
