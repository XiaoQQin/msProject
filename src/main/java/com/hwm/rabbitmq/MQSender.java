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

    /**
     * 发送消息到消息队列
     * @param msMessage
     */
    public void sendMsMessage(MSMessage msMessage) {
        String msg = RabbitUtils.beanToString(msMessage);
        logger.info("send message: "+msg);
        amqpTemplate.convertAndSend(MQConfig.MSQUEUE, msg);

    }
}
