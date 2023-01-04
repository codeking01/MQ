package com.codeking.rabbitmq.two;

import com.codeking.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author : codeking
 * @create : 2023/1/4 21:20
 */
// 更改配置，启动两个进程
public class consumer01 {
    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 获取信道
        Channel channel = RabbitMqUtils.getChannel();
        // 定义接受成功 和失败取消的回调
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("接受的消息为：" + message);
            // 收到消息，手动应答 multiple 设置为false 设置批量应答为否
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };
        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println("消费取消，consumerTag:" + consumerTag);
        };
        /**
         * 消费者消费消息
         * 1.消费哪个队列
         * 2.消费成功之后是否要自动应答 true 代表自动应答 false 手动应答
         * 3.消费者未成功消费的回调
         */
        System.out.println("C1 消费者启动等待消费.................. ");
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, cancelCallback);
    }
}
