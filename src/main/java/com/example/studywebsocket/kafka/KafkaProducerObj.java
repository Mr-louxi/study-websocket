package com.example.studywebsocket.kafka;

import com.example.studywebsocket.entity.Message;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * kafka生成者
 */
@Component
public class KafkaProducerObj {
    private static Logger logger = LoggerFactory.getLogger(KafkaProducerObj.class);

    @Autowired
    private KafkaTemplate kafkaTemplate;

    private Gson gson = new Gson();

    /**
     * 生产数据到kafka
     */
    public void send(String topic,String msg){
        Message message = new Message();
        message.setId(System.currentTimeMillis());
        message.setMsg(msg);
        message.setSendDate(new Date());
        logger.info("发送消息 ----->>>>>  message = {}", gson.toJson(message));
        kafkaTemplate.send(topic,gson.toJson(message));
    }


}
