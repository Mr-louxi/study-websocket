package com.example.studywebsocket.kafka;

import com.example.studywebsocket.kafka.server.WebSocketServer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

import static com.example.studywebsocket.kafka.server.WebSocketServer.webSocketSet;
/**
 * Kafka消费者
 */
public class KafkaConsumerObj extends Thread {


    private KafkaConsumer<String, String> consumer;

 /*   @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrap_server;

    @Value("${spring.kafka.consumer.group-id}")
    private String group_id;

    @Value("${spring.kafka.consumer.auto-commit-interval}")
    private String interval_ms;

    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private String auto_commit;*/

    private String topic = "studytest";


    @Override
    public void run() {
        //加载kafka消费者参数
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.31.55:9092");
        props.put("group.id", "testGroup");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "15000");

        props.put("auto.offset.reset","earliest");

        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        //创建消费者对象
        consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Arrays.asList(this.topic));
        //死循环，持续消费kafka
        while (true) {
           try{
               //消费数据，并设置超时时间
               ConsumerRecords<String, String> records = consumer.poll(100);
               //Consumer message
               for (ConsumerRecord<String, String> record : records) {
                   //Send message to every client
                   for (WebSocketServer webSocket :webSocketSet) {
                       webSocket.sendMessage(record.value());
                   }
               }
           }catch (Exception e){
               e.printStackTrace();
           }
        }
    }
}
