package com.example.studywebsocket.kafka.server;


import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * ServerEndpoint 表示 该类 为websocket的一个端点
 *
 */
@ServerEndpoint("/echo")
@Component
public class WebSocketServer {
    private static int onlineCount = 0;//在线连接数

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    public static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 建立连接
     * @param session
     */
    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        webSocketSet.add(this);
        addOnlineCount();

    }

    /**
     * 接收 客户端 发送过来的消息  并且 返回 xxx  到客户端
     * @param incomingMessage
     * @return
     */
    @OnMessage
    public String onMessage(String incomingMessage){
        System.out.println("来自客户端的消息:"+incomingMessage);
        return "I got this ("+incomingMessage+") so I am sending it back!";
    }
    /**
     * 关闭连接
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);//将socket对象从集合中移除，以便广播时不发送次连接。如果不移除会报错(需要测试)
        subOnlineCount();
        System.out.println("A session insert,sessionId is " + session.getId());
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session,Throwable error){
        System.err.println("发生错误");
        error.printStackTrace();
    }

    /**
     * 该方法 用于从后台向前台推送数据
     * @param msg
     * @throws IOException
     */
    public void sendMessage(String msg) throws IOException {
        this.session.getBasicRemote().sendText(msg);
    }

    public static synchronized void addOnlineCount(){
        onlineCount++;
    }


    public static synchronized void subOnlineCount(){
        onlineCount--;
    }


}
