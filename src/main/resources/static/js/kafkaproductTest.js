function sendKafka(){
    var url = "/sendMsg";
    var param = {msg:$("#testID").val()};
    $.post(url,param,function (data) {
        alert(data);
    });
}