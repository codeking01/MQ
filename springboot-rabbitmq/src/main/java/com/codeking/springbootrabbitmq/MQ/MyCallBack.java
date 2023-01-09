package com.codeking.springbootrabbitmq.MQ;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author : codeking
 * @create : 2023/1/9 18:27
 */
@Slf4j
@Component
public class MyCallBack implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {
    // 这个位置注入
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /*@Autowired
    private MyCallBack myCallBack;*/

    //rabbitTemplate 注入之后就设置该值
    //依赖注入 rabbitTemplate 之后再设置它的回调对象 这个地方需要使用注解 @PostConstruct
    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        /**
         * true：
         * 交换机无法将消息进行路由时，会将该消息返回给生产者
         * false：
         * 如果发现消息无法进行路由，则直接丢弃
         */
        rabbitTemplate.setMandatory(true);
        //设置回退消息交给谁处理  这个用法被弃用了...
        rabbitTemplate.setReturnCallback(this);
    }

    /**
     * 交换机不管是否收到消息的一个回调方法
     * CorrelationData
     * 消息相关数据
     * ack
     * 交换机是否收到消息
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = correlationData != null ? correlationData.getId() : "";
        if (ack) {
            log.info("交换机已经收到 id 为:{}的消息", id);
        } else {
            log.info("交换机还未收到 id 为:{}消息,由于原因:{}", id, cause);
        }
    }

    @Override
    public void returnedMessage(Message message, int replyCode,
                                String replyText, String exchange, String routingKey) {
        log.info("消息:{}被服务器退回，退回原因:{}, 交换机是:{}, 路由 key:{}",
                new String(message.getBody()), replyText, exchange, routingKey);
    }
}
