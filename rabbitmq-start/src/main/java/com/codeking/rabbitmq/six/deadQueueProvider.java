package com.codeking.rabbitmq.six;

import com.codeking.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.nio.charset.StandardCharsets;

/**
 * @author : codeking
 * @create : 2023/1/7 23:45
 */

public class deadQueueProvider {
    public static final String NORMAL_EXCHANGE = "normal_exchange";

    public static void main(String[] args) throws Exception {
        // 获取信道
        Channel channel = RabbitMqUtils.getChannel();
        // 声明 exchange交换机
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        //设置消息的 TTL 时间  这个可以在消费者设置（相当于多久没收到信息 就认定过期）
        // 去掉时间限制，并且去后面限制队列的长度
        //AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("1000").build();
        // 设置优先级,这个是现在 队列中代码添加优先级（params中添加 "x-max-priority"）
        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().priority(5).build();
        //AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().build();
        //该信息是用作演示队列个数限制
        for (int i = 0; i < 11; i++) {
            String message = "info" + i;
            channel.basicPublish(NORMAL_EXCHANGE, "first_one", properties, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("生产者发送消息:" + message);
        }
    }
}
