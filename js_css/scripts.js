window.onload = function () {
    document.body.onresize = function () {
       var canvasNode = document.getElementById('canvas');
       canvasNode.width = canvasNode.parentNode.clientWidth;
       canvasNode.height = canvasNode.parentNode.clientHeight;
       var scrollbarNode = document.getElementById('viewport');
       $("#viewport").css({"height" : scrollbarNode.parentNode.parentNode.clientHeight});
       
    }
    document.body.onresize();
};


$(document).ready(function() {
    $("#login_btn").click(function() {
        user = $('#username').val();
	if(user == ""){
            user = "anonymus" + Math.ceil(Math.random()*100000);
	}
            console.log("Username: " + user);
        
    window.WebSocket = window.WebSocket || window.MozWebSocket;
    if (!window.WebSocket) {
        console.log("Szar a bongeszod!");
        return;
    }

    var chat_messages=$('#chat_messages_ul');

    var ws = new WebSocket("ws://nemgy.itk.ppke.hu:61160");
    //ONOPEN
    ws.onopen = function(){
        $('#login_container').hide();
        $('#scrollbar1').tinyscrollbar();

        console.log("succesfull connect");
        console.log("Sending username");
        var user_msg={
            "type":"0",
            "sender":user
        }
        ws.send(JSON.stringify(user_msg));
    }
    
    //ONCLOSE
    ws.onclose = function(){
        console.log("connection closed...");
    }
    
    //ONERROR
    ws.onerror = function(error) {
        console.log("gondolom baj van");     
    }
    
    //ONMESSAGE
    ws.onmessage = function (message) {
        console.log("Got message");
        try {
            var json = JSON.parse(message.data);
        } catch (e) {
            console.log('This doesn\'t look like a valid JSON: ', message.data);
            return;
        }
        
        switch(json.type){
            case 2: //Objektum modositasa
                break;
            case 3: //Objektum letrehozasa
                break;
            case 4: //Objektum torlese
                break;
            case 1000: //CHAT
                handleChatMessage(json);
                break;
            default: //hiba
                console.log("Valami nagyon nem jo.. szivas..");
        }
        
        //DEBUG
        console.log(json);
    }
    function handleChatMessage(json){
        chat_messages.append("<li>"+json.sender + ": " + json.message+"</li>")
        $('#scrollbar1').tinyscrollbar_update('bottom');
    }

$('#chat_input_field').keydown(function(e) {
        if (e.keyCode === 13) {
            var msg = $(this).val();
            var chat_msg = {
                "sender":user,
                "message":msg,
                "type":1000
            }
            
            ws.send(JSON.stringify(chat_msg));
            handleChatMessage(chat_msg);
            $(this).val('');
        }
    });
  
})
})

