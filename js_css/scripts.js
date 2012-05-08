window.onload = function () {
    document.body.onresize = function () {
//       var canvasNode = document.getElementById('canvas');
//       canvasNode.width = canvasNode.parentNode.clientWidth;
//       canvasNode.height = canvasNode.parentNode.clientHeight;
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

	//KINETICJS
    var stage = new Kinetic.Stage({
          container: "canvas_container",
		  width:$('#canvas_container')[0].clientWidth,
		  height:$('#canvas_container')[0].clientHeight
    });
	var layer = new Kinetic.Layer();
//	stage.add(layer);


	//End: KINETICJS


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
                console.log("Valami nagyon nem jo.. szivas..");
        }
        
        //DEBUG
        //console.log(json);
    }

	function draw(json){
		if(objects[json.message.objId] == undefined ){
		var color = randomcolor();
		var x = json.message.x;
		var y = json.message.y;
		/*
		var c=document.getElementById("canvas");
		var ctx=c.getContext("2d");


		ctx.fillStyle = color;
		ctx.fillRect(x,y,json.message.data.length*10+30,30);
		ctx.fillStyle = 'black';
		console.log((json.message.data.length)*10+30);
		ctx.font="10pt Calibri";
		ctx.fillText(json.message.data,parseInt(x)+15,parseInt(y)+20);
		*/
		var rect=new Kinetic.Rect({
				"x":json.message.x,
				"y":json.message.y,
				"width":json.message.data.length*10+30,
				"height":30,
				"fill":color
				});
		var simpleText = new Kinetic.Text({
				"x":parseInt(json.message.x)+15,
				"y":parseInt(json.message.y)+10,
				text: json.message.data,
				fontSize:11,
				fontFamily: "Calibri",
				textFill: "black"
				})
		layer.add(rect);
		layer.add(simpleText);
		stage.add(layer);
//		var obj = {"x":x,"y":y,"z":json.message.z,"img":ctx.getImageData(x,y,json.message.data.length+30,30)};
//		objects[json.message.objId]= obj;
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

