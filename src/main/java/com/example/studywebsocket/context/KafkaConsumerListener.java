package com.example.studywebsocket.context;

import com.example.studywebsocket.kafka.KafkaConsumerObj;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class KafkaConsumerListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("========================容器初始化，启动Kafka消费者线程。========================");
        KafkaConsumerObj kafkaConsumerObj = new KafkaConsumerObj();
        kafkaConsumerObj.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("========================容器销毁========================");
    }
}
