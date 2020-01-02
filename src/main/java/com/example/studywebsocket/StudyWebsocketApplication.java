package com.example.studywebsocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * ServletComponentScan  负责扫描 WebListener 注解 WebFilter 注解 WebServlet注解
 */
@SpringBootApplication
@ServletComponentScan
public class StudyWebsocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyWebsocketApplication.class, args);
    }

}
