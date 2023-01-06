package com.codeking.rabbitmq.fiveExchange;

import com.codeking.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : codeking
 * @create : 2023/1/7 0:16
 */
public class DirectExchangeProvider {
    public static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws Exception {
        // 获取信道
        Channel channel = RabbitMqUtils.getChannel();
        // 设置Exchange类型
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //创建多个 bindingKey
        Map<String, String> bindingKeyMap = new HashMap<>();
        bindingKeyMap.put("info", "普通 info 信息");
        bindingKeyMap.put("warning", "警告 warning 信息");
        bindingKeyMap.put("error", "错误 error 信息");
        //debug 没有消费这接收这个消息 所有就丢失了
        bindingKeyMap.put("debug","调试 debug 信息");
        // 挨个发
        for (Map.Entry<String, String> entry : bindingKeyMap.entrySet()){
            String bindKey = entry.getKey();
            String bindValue = entry.getValue();
            // 开始发信息
            channel.basicPublish(EXCHANGE_NAME,bindKey,null,bindValue.getBytes(StandardCharsets.UTF_8));
            System.out.println("生产者发出消息:" + bindValue);
        }
    }
}
