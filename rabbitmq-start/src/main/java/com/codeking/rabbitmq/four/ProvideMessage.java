package com.codeking.rabbitmq.four;

import com.codeking.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;

/**
 * @author : codeking
 * @create : 2023/1/4 22:41
 */
public class ProvideMessage {
    //private static final String TASK_QUEUE_NAME = "ack_queue_confirm";
    private static final int MESSAGE_COUNT = 1000;

    public static void main(String[] args) throws Exception {
        // 单个信息
        //publishMessageIndividually();
        // 多个处理
        //publishMessageBatch();
        // 异步处理
        publishMessageAsync();
    }

    public static void publishMessageIndividually() throws IOException, TimeoutException, InterruptedException {
        // 获取信道
        Channel channel = RabbitMqUtils.getChannel();
        String TASK_QUEUE_NAME = UUID.randomUUID().toString();
        // 定义参数
        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        //开启发布确认
        channel.confirmSelect();
        long begin = System.currentTimeMillis();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", TASK_QUEUE_NAME, null, message.getBytes());
            //服务端返回 false 或超时时间内未返回，生产者可以消息重发
            boolean flag = channel.waitForConfirms();
            if (flag) {
                System.out.println("消息发送成功");
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个单独确认消息,耗时" + (end - begin) + "ms");
    }

    public static void publishMessageBatch() throws IOException, TimeoutException, InterruptedException {
        // 获取信道
        Channel channel = RabbitMqUtils.getChannel();
        String TASK_QUEUE_NAME = UUID.randomUUID().toString();
        // 定义参数
        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        //开启发布确认
        channel.confirmSelect();
        //批量确认消息大小
        int batchSize = 100;
        //未确认消息个数
        int outstandingMessageCount = 0;
        long begin = System.currentTimeMillis();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", TASK_QUEUE_NAME, null, message.getBytes());
            outstandingMessageCount++;
            if (outstandingMessageCount == batchSize) {
                //服务端返回 false 或超时时间内未返回，生产者可以消息重发
                boolean flag = channel.waitForConfirms();
                if (flag) {
                    System.out.println("消息发送成功");
                    outstandingMessageCount = 0;
                }
            }
        }
        //为了确保还有剩余没有确认消息 再次确认
        if (outstandingMessageCount > 0) {
            channel.waitForConfirms();
        }
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个单独确认消息,耗时" + (end - begin) + "ms");
    }

    public static void publishMessageAsync() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        /**
         * 线程安全有序的一个哈希表，适用于高并发的情况
         * 1.轻松的将序号与消息进行关联
         * 2.轻松批量删除条目 只要给到序列号
         * 3.支持并发访问
         */
        // 相当于每一个信息都加了回调，也是加了标记
        ConcurrentSkipListMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();
        /**
         * 确认收到消息的一个回调
         * 1.消息序列号
         * 2.true 可以确认小于等于当前序列号的消息
         * false 确认当前序列号消息
         */

        ConfirmCallback ackCallback = (sequenceNumber, multiple) -> {
            // 如果是批处理
            if (multiple) {
                //返回的是小于等于当前序列号的未确认消息 是一个 map
                ConcurrentNavigableMap<Long, String> confirmMap = outstandingConfirms.headMap(sequenceNumber, true);
                confirmMap.clear();
            } else {
                //只清除当前序列号的消息
                outstandingConfirms.remove(sequenceNumber);
            }
        };
        ConfirmCallback nackCallback = (sequenceNumber, multiple) -> {
            String message = outstandingConfirms.get(sequenceNumber);
            System.out.println("发布的消息" + message + "未被确认，序列号" + sequenceNumber);
        };
        /**
         * 添加一个异步确认的监听器
         * 1.确认收到消息的回调
         * 2.未收到消息的回调
         */
        channel.addConfirmListener(ackCallback, nackCallback);
        long begin = System.currentTimeMillis();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = "消息" + i;
            /**
             * channel.getNextPublishSeqNo()获取下一个消息的序列号
             * 通过序列号与消息体进行一个关联
             * 全部都是未确认的消息体
             */
            outstandingConfirms.put(channel.getNextPublishSeqNo(), message);
            channel.basicPublish("", queueName, null, message.getBytes(StandardCharsets.UTF_8));
        }
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个异步确认消息,耗时" + (end - begin) + "ms");
    }
}
