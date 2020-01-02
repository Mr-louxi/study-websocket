package com.example.studywebsocket.controller;

import com.example.studywebsocket.kafka.KafkaProducerObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class WebSocketTestController {
    private static Logger logger = LoggerFactory.getLogger(WebSocketTestController.class);

    @Autowired
    private KafkaProducerObj kafkaProducerObj;

    @RequestMapping("/index")
    public String testWebSocket(){
        return "/page/websocketTest.html";
    }

    /**
     * 打开写kafka的 页面
     * @return
     */
    @RequestMapping("/productKafka")
    public String productKafka(){
        return "/page/kafkaproductTest.html";
    }

    /**
     * 将请求数据写入kafka
     * @param msg
     * @return
     */
    @RequestMapping("sendMsg")
    @ResponseBody
    public String sendMsg(String msg){
        String reslut = "";
        logger.info("传入的消息:"+msg);
        try {
            kafkaProducerObj.send("studytest",msg);
            reslut = "发送成功";
            logger.info(reslut);
        }catch (Exception e){
            e.printStackTrace();
            reslut = "发送失败";
            logger.info(reslut);
        }
        return reslut;
    }
}
