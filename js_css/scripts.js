window.onload = function () {
    document.body.onresize = function () {
       var canvasNode = document.getElementById('canvas');
       canvasNode.width = canvasNode.parentNode.clientWidth;
       canvasNode.height = canvasNode.parentNode.clientHeight;
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

    var chat_messages=$('#chat_messages');

    var ws = new WebSocket("ws://nemgy.itk.ppke.hu:61160");
	var objects = new Array();
    //ONOPEN
    ws.onopen = function(){
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
				draw(json);
                break;
            case 3: //Objektum letrehozasa
				//draw(json);
                break;
            case 4: //Objektum torlese
                break;
            case 1000: //CHAT
                handleChatMessage(json);
                break;
            default: //hiba
                console.log("Valami nagyon nem jo.. szivas..")
        }
        
        //DEBUG
        //console.log(json);
    }

	function draw(json){
		if(objects[json.message.objId] == undefined ){
		var color = randomcolor();
		var c=document.getElementById("canvas");
		var ctx=c.getContext("2d");
		var x = json.message.x;
		var y = json.message.y;

		ctx.fillStyle = color;
		ctx.fillRect(x,y,json.message.data.length+30,30);
		ctx.fillStyle = "#000000";
		console.log((json.message.data.length)*10+30);
		ctx.font="10px Arial";
		ctx.fillText(json.message.data,x+15,y+20);
		var obj = {"x":x,"y":y,"z":json.message.z,"img":ctx.getImageData(x,y,json.message.data.length+30,30)};
		objects[json.message.objId]= obj;
		}
	}

	function modify(json){
		var temp = objects[json.messages.objId];
		if(temp != "undefine"){
			
		}
		else{
			console.log("aaaaAAaaaAAaAAaAaAaa");
		}
	}

	//random color generalo
	function randomcolor(){
		var color = "#";
		for(var i=0;i<6;i += 1){
			var temp = Math.floor((Math.random() * 15)+1);
			if(temp>9){
				switch(temp){
					case 10: color = color + 'a';break;
					case 11: color = color + 'b';break;
					case 12: color = color + 'c';break;
					case 13: color = color + 'd';break;
					case 14: color = color + 'e';break;
					case 15: color = color + 'f';break;
				}
			}
			else{
				color = color + temp;
			}
		}
		return color;
	}

    function handleChatMessage(json){
        chat_messages.append(json.userName + ":" + json.msg)        //ezmeg szar
    }
})
})

