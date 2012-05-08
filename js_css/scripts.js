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
	var objects = new Array();
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
        switch(json.type){ //mivel nincs sehol ertelmesen osszefoglalva, igy ki kellett lesnem a desktop kliensbol..
			case 1: break; //objektum modositasa
            case 2: //Objektum mozgatasa +mentese 
				if(objects[json.message.objId] == undefined){ //mert az a kocsog szerver valamiert 2es type-u uzenettel kuldi ki az elejen az objektumokat -.-
					create(json);
				}
				else{
					move(json,true); //meg nem teszteltem FOS!
				}
                break;
            case 3: //Objektum letrehozasa
				create(json);
				//elvileg akkor ilyen nem johet -.-
                break;
            case 4: //Objektum mozgatasa
				move(json,false); //meg nem teszteltem
                break;
			case 5: //Objektum torlese
				break;
            case 1000: //CHAT
                handleChatMessage(json);
                break;
            default: //hiba
                console.log("Valami nagyon nem jo.. szivas..");
        }
        
        //DEBUG
        //console.log(json);
    }

	$("#canvas").click(function() {
		console.log("canvas click");
	});

	var ctx=document.getElementById("canvas").getContext("2d");
	
	function create(json){
		if(objects[json.message.objId] == undefined ){
			var color = randomcolor();
			var x = parseInt(json.message.x);
			var y = parseInt(json.message.y);

			ctx.fillStyle = color;
			ctx.fillRect(x,y,json.message.data.length*10+30,30);
			ctx.fillStyle = "#000000";
			ctx.font="10px Arial";
			ctx.fillText(json.message.data,x+15,y+20);
			var obj = {"x":x,"y":y,"z":json.message.z,"size": json.message.data.length*10+30,"img":ctx.getImageData(x,y,json.message.data.length*10+30,30)};
			objects[json.message.objId]= obj;
		}
	}

	var movearray = new Array();
	function move(json,save){ //nem szar
		var temp = objects[json.message.objId];
		if(temp != undefined){
			var tmp = movearray[json.message.objId];
			if(tmp == undefined){
				ctx.clearRect(temp["x"],temp["y"],temp["size"],30);
			}
			else{
				ctx.clearRect(tmp["x"],tmp["y"],temp["size"],30);
			}
			ctx.putImageData(temp["img"],json.message.x,json.message.y);
			if(save){
				if(tmp != undefined){
					delete movearray[json.message.objId];
				}
				objects[json.message.objId]["x"]=json.message.x;
				objects[json.message.objId]["y"]=json.message.y;
			}
			else{
				movearray[json.message.objId]={"x":json.message.x,"y":json.message.y};
			}
		}
	}



	function modify(json){
		var temp = objects[json.messages.objId];
		if(temp != undefined){
			
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

