package com.codeking.rabbitmq.fiveExchange;

import com.codeking.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

/**
 * @author : codeking
 * @create : 2023/1/7 0:34
 */
public class ReceiveLogsDirect02 {
    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws Exception {
        // 获取信道
        Channel channel = RabbitMqUtils.getChannel();
        // 绑定Exchange
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        // 定义队列名称
        String queueName = "queue_log2";
        channel.queueDeclare(queueName, false, false, false, null);
        // 绑定队列
        channel.queueBind(queueName, EXCHANGE_NAME, "error");
        channel.queueBind(queueName, EXCHANGE_NAME, "debug");
        System.out.println("等待接收消息........... ");
        // 回调函数
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" 接收绑定键 :" + delivery.getEnvelope().getRoutingKey() + ", 消息:" + message);
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }
}
