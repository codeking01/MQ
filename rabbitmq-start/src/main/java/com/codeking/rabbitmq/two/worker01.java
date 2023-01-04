package com.codeking.rabbitmq.two;

import com.codeking.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import static com.codeking.rabbitmq.utils.RabbitMqUtils.getChannel;

/**
 * @author : codeking
 * @create : 2023/1/4 21:10
 */
public class worker01 {
    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 获取信道
        Channel channel = getChannel();
        // 定义信道
        /**
         * 生成一个队列
         * 1.队列名称
         * 2.队列里面的消息是否持久化 默认消息存储在内存中
         * 3.该队列是否只供一个消费者进行消费 是否进行共享 true 可以多个消费者消费
         * 4.是否自动删除 最后一个消费者端开连接以后 该队列是否自动删除 true 自动删除
         * 5.其他参数
         */
        // 队列持久化
        boolean durable = true;
        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
        //从控制台当中接受信息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            /**
             * 发送一个消息
             * 1.发送到那个交换机
             * 2.路由的 key 是哪个
             * 3.其他的参数信息
             * 4.发送消息的消息体
             */
            //不公平分发
            int prefetchCount = 1;
            channel.basicQos(prefetchCount);
            // 消息持久化 MessageProperties.PERSISTENT_TEXT_PLAIN
            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            System.out.println("消息发送完毕！");
        }
    }
}
