/*******      测试websocket     *******/
//window.addEventListener("load",init,false);
var echo_websocket ;
var ws_url = "ws://127.0.0.1:8080/echo";
writeToCreen("Connecting to "+ ws_url);
if('WebSocket' in window){
    echo_websocket = new WebSocket(ws_url);
}else{
    console.log("当前浏览器不支持WebSocket");
}
$(function () {
    output = document.getElementById("output");
    testID = document.getElementById("testID");

});


/**
 * websocket  建立连接
 */
function connect(){
    var ws_url = "ws://127.0.0.1:8080/echo";
    writeToCreen("Connecting to "+ ws_url);
    if('WebSocket' in window){
        echo_websocket = new WebSocket(ws_url);
    }else{
        console.log("当前浏览器不支持WebSocket");
        return;
    }
}
//连接成功的回调方法
echo_websocket.onopen = function (ev) {
    writeToCreen("已经成功连接 !  连接成功后的回调!");
    //  doSend(testID.value);
};
//接收到消息的回调方法
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

/**
 * 发送数据
 * @param message
 */
function doSend() {
    var message = $("#testID").val();
    echo_websocket.send(message);
    writeToCreen("Sent message: "+ message);
}

/**
 * 关闭连接
 */
function close(){
    echo_websocket.close();
}

function writeToCreen(message) {
    var pre = document.createElement("P");
    pre.style.wordWrap = "break-word";
    pre.innerHTML = message;
    var output = document.getElementById("output");
    $("#output").append("<p>"+message+"</p>");
    //output.appendChild(pre);
}