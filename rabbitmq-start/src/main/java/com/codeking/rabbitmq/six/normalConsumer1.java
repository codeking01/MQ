package com.codeking.rabbitmq.six;

import com.codeking.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : codeking
 * @create : 2023/1/8 0:05
 */
public class normalConsumer1 {
    //普通交换机名称
    private static final String NORMAL_EXCHANGE = "normal_exchange";
    //死信交换机名称
    private static final String DEAD_EXCHANGE = "dead_exchange";

    public static void main(String[] args) throws Exception {
        // 获取信道
        Channel channel = RabbitMqUtils.getChannel();
        //声明死信和普通交换机 类型为 direct
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);
        //声明死信队列和普通的队列
        String dead_queue = "dead_queue";
        String normalQueue = "normal_queue";
        channel.queueDeclare(dead_queue, false, false, false, null);
        //死信队列绑定死信交换机与 routingkey
        channel.queueBind(dead_queue, DEAD_EXCHANGE, "dead_key");
        // 绑定正常的key,需要声明一些参数
        Map<String, Object> params = new HashMap<>();
        params.put("x-dead-letter-exchange", DEAD_EXCHANGE);
        //正常队列设置死信 routing-key 参数 key 是固定值
        params.put("x-dead-letter-routing-key", "dead_key");
        // 记得删除原先的队列
        //params.put("x-max-length", 6);
        channel.queueDeclare(normalQueue, false, false, false, params);
        // 绑定普通发送的队列
        channel.queueBind(normalQueue, NORMAL_EXCHANGE, "first_one");
        System.out.println("等待接收信息...");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            if (message.equals("info5")) {
                System.out.println("Consumer01 接收到消息" + message + "并拒绝签收该消息");
                //requeue 设置为 false 代表拒绝重新入队 该队列如果配置了死信交换机将发送到死信队列中
                channel.basicReject(delivery.getEnvelope().getDeliveryTag(), false);
            }
            System.out.println("Consumer01 接收到消息" + message);
        };
        channel.basicConsume(normalQueue, deliverCallback, consumerTag -> {
        });
    }
}
