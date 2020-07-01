package com.hwm.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    private static Logger logger = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    AmqpTemplate amqpTemplate;

//    public void send(Object message){
//        String msg = RabbitUtils.beanToString(message);
//        logger.info("send message: "+msg);
//        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
//    }
//
//
//
//    public void sendTopic(Object message){
//        String msg = RabbitUtils.beanToString(message);
//        logger.info("send topic message:"+msg);
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", msg+"1");
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", msg+"2");
//    }

    public void sendMsMessage(MSMessage msMessage) {
        String msg = RabbitUtils.beanToString(msMessage);
        logger.info("send message: "+msg);
        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);

    }
}
