package com.codeking.rabbitmq.one;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author : codeking
 * @create : 2023/1/4 20:44
 */
public class Consumer {
    // 定义队列名称
    private static final String QUEUE_NAME = "start_test";
    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.102");
        factory.setUsername("admin");
        factory.setPassword("admin");
        // 新建连接，创建信道
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        System.out.println("等待接收信息...");
        //推送的消息如何进行消费的接口回调
        //consumerTag 代表哪个信息标记 ,delivery 代表传递的信息
        DeliverCallback deliverCallback=(consumerTag,delivery)->{
            String message = new String(delivery.getBody());
            System.out.println("当前的consumerTag:"+consumerTag);
            System.out.println("接受的message："+message);
        };
        //取消消费的一个回调接口 如在消费的时候队列被删除掉了
        CancelCallback cancelCallback =(consumerTag)->{
            System.out.println("消费消息被打断！");
            System.out.println("当前的consumerTag:"+consumerTag);
        };
        /**
         * 消费者消费消息
         * 1.消费哪个队列
         * 2.消费成功之后是否要自动应答 true 代表自动应答 false 手动应答
         * 3.消费者未成功消费的回调
         */
        channel.basicConsume(QUEUE_NAME,false, deliverCallback,cancelCallback);
    }
}
