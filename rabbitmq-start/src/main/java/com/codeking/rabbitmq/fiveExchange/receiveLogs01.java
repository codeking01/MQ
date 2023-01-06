package com.codeking.rabbitmq.fiveExchange;

import com.codeking.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

/**
 * @author : codeking
 * @create : 2023/1/7 0:02
 */
public class receiveLogs01 {
    public static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        /**
         * 生成一个临时的队列 队列的名称是随机的
         * 当消费者断开和该队列的连接时 队列自动删除
         */
        String queueName = channel.queueDeclare().getQueue();
        //把该临时队列绑定我们的 exchange 其中 routingkey(也称之为 binding key)为空字符串
        channel.queueBind(queueName, EXCHANGE_NAME, "");
        System.out.println("等待接收消息,把接收到的消息打印在屏幕........... ");

        // 接受和取消的回调函数
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("consumerTag 是" + consumerTag);
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);
            System.out.println("接受的信息为：" + msg);
        };
        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println("消息取消....  " + consumerTag);
        };

        System.out.println("等待接受信息");
        channel.basicConsume(queueName, deliverCallback, cancelCallback);
    }
}
