package com.codeking.rabbitmq.fiveExchange;

/**
 * @author : codeking
 * @create : 2023/1/6 23:45
 */

import com.codeking.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * 总共有以下类型：
 * 直接(direct), 主题(topic) ,标题(headers) , 扇出(fanout)
 */
public class FanoutProvider {
    // 在生产者方向定义Exchange就可以
    public static final String EXCHANGE_NAME="logs";

    public static void main(String[] args) throws Exception{
        // 获取信道
        Channel channel = RabbitMqUtils.getChannel();
        /**
         * 声明一个 exchange
         * 1.exchange 的名称
         * 2.exchange 的类型
         */
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
        // 获取键盘输入内容
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String message = scanner.next();
            channel.basicPublish(EXCHANGE_NAME,"",null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("发送信息成功，"+message);
        }
    }
}
