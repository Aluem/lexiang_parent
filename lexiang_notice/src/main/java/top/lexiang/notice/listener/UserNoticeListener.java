package top.lexiang.notice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import top.lexiang.common.entity.Result;
import top.lexiang.common.entity.ResultCode;
import top.lexiang.notice.netty.MyWebSocketHandler;

import java.util.HashMap;

/**
 * 处理点赞消息
 * Rabbit监听器，用来获取MQ消息并进行处理
 */
public class UserNoticeListener implements ChannelAwareMessageListener {

    private static ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 该方法会将队列中的消息消费掉 Message message
     */
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        //获取用户id，可以通过队列名称获取
        String queueName = message.getMessageProperties().getConsumerQueue();
        String userId = queueName.substring(queueName.lastIndexOf("_") + 1);

        //从自己定义的map容器中获取该用户的连接
        io.netty.channel.Channel wsChannel = MyWebSocketHandler.userChannelMap.get(userId);

        //判断用户是否在线
        if (wsChannel != null) {
            //如果连接不为空，表示用户在线
            //封装返回数据
            HashMap countMap = new HashMap();
            countMap.put("userNoticeCount", 1);
            Result result = new Result(ResultCode.SUCCESS, countMap);

            //把数据通过WebSocket连接主动推送用户
            wsChannel.writeAndFlush(new TextWebSocketFrame(MAPPER.writeValueAsString(result)));
        }
    }
}
