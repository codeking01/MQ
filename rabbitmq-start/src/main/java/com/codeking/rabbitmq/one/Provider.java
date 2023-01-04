package com.codeking.rabbitmq.one;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author : codeking
 * @create : 2023/1/4 20:32
 */
public class Provider {
    // 定义队列名称
    private static final String QUEUE_NAME="start_test";

    public static void main(String[] args) {
        //创建一个连接工厂
        ConnectionFactory factory= new ConnectionFactory();
        //设置参数
        factory.setHost("192.168.1.102");
        factory.setUsername("admin");
        factory.setPassword("admin");
        //channel 实现了自动 close 接口 自动关闭 不需要显示关闭
        try {
            Connection connection = factory.newConnection();
            // 创建信道
            Channel channel = connection.createChannel();
            /**
             * 生成一个队列
             * 1.队列名称
             * 2.队列里面的消息是否持久化 默认消息存储在内存中
             * 3.该队列是否只供一个消费者进行消费 是否进行共享 true 可以多个消费者消费
             * 4.是否自动删除 最后一个消费者端开连接以后 该队列是否自动删除 true 自动删除
             * 5.其他参数
             */
            channel.queueDeclare(QUEUE_NAME,false,false,false,null);
            String message = "hello world";
            /**
             * 发送一个消息
             * 1.发送到那个交换机
             * 2.路由的 key 是哪个
             * 3.其他的参数信息
             * 4.发送消息的消息体
             */
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes("UTF-8"));
            System.out.println("消息发送完毕！");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
