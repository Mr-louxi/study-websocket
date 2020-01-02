## 1. websocket原理

 

![1577956076663](C:\Users\louxi\AppData\Roaming\Typora\typora-user-images\1577956076663.png)

## 2.websocket入门

websocket 是基于http请求之上的 双工通信协议;

### js端代码:

```javascript
var echo_websocket ;
var ws_url = "ws://127.0.0.1:8080/echo";
writeToCreen("Connecting to "+ ws_url);
if('WebSocket' in window){
    echo_websocket = new WebSocket(ws_url);
}else{
    console.log("当前浏览器不支持WebSocket");
}
//连接成功的回调方法
echo_websocket.onopen = function (ev) {
    writeToCreen("已经成功连接 !  连接成功后的回调!");
    //  doSend(testID.value);
};
//接收到消息的回调方法 -- 用于后台消息推送
echo_websocket.onmessage = function (ev) {
    writeToCreen("收到消息后的回调!" + ev.data);
    //echo_websocket.close();
}
//连接失败的回调方法
echo_websocket.onerror = function (ev) {
    writeToCreen("<span style='color: red;'> ERROR: 连接失败的回调!</span> "+ ev.data);
    echo_websocket.close();
}
echo_websocket.onclose = function (ev) {
    writeToCreen("关闭连接后的回调!");
}
```

### java端：

#### 1.引入 websocket

```xml
<!-- 引入websocket -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

#### 2.编写websocket服务端  WebSocketServer.java

创建一个java类文件，并加上注解 

```java
@ServerEndpoint("/echo")
@Component
```

例如:

```java
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
```

#### 3.编写WebSocketConfig 用于扫描所有的 WebSocketServer

```java
/**
 * 扫描 并注册 所有的ServerEndpoint
 */
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}
```

<Font color="red">注意：WebSocket 后台向前端推送数据 是通过自定义sendMessage方法</Font>

```java
/**
 * 该方法 用于从后台向前台推送数据
 * @param msg
 * @throws IOException
 */
public void sendMessage(String msg) throws IOException {
    this.session.getBasicRemote().sendText(msg);
}
```

## 3.websocket消费kafka的数据 并推送到前台

```java
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
```

