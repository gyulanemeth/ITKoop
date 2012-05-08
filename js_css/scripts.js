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
		//var objects = new Array();
		var s = new state();
	
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
				newobj(json);
                break;
            case 3: //Objektum letrehozasa ...most akkor ilyen nem erkezik? ilyet csk kuldunk a szervernek? 
                break;
            case 4: //Objektum mozgatasa
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


	var ctx=document.getElementById("canvas").getContext("2d");
	
	/*function create(json){
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
	function move(json,save){
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
	}*/

	
	function newobj(json){
		if(!s.containId(json.message.objId)){
			s.addRectangle(new Rectangle(json,randomcolor()));
		}
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

	function Rectangle(json, c){
		
		this.x = parseInt(json.message.x);
		this.y = parseInt(json.message.y);
		this.z = parseInt(json.message.z);
		this.data = json.message.data;
		this.id = json.message.objId;
		this.color = c;
	}

	Rectangle.prototype.draw = function() {
		ctx.fillStyle = this.color;
		ctx.fillRect(this.x, this.y, this.data.length*10+30,30);
		ctx.fillStyle = "#000000";
		ctx.fillText(this.data,this.x+15,this.y+20);
	}

	Rectangle.prototype.contains = function(mx, my) {
		return  (this.x <= mx) && (this.x + this.data.length*10+30 >= mx) && (this.y <= my) && (this.y + 30 >= my);
	}

	Rectangle.prototype.getId = function(){
		return this.id;
	}

	function state(){
		this.redrawed  = false;
		this.objects = [];
		this.moving = false;
		this.selection = null;
		this.fromx = 0;
		this.fromy = 0;

		var state = this;
		var canvas = $("#canvas");
		this.width = canvas.width;
		this.height = canvas.height;

		canvas.bind('mousedown', function(e) {
			var mouse = state.getMouse(e);
			var mx = mouse.x;
			var my = mouse.y;
			var objects = state.objects;
			var l = objects.length;
			for (var i = l-1; i >= 0; i--) {
				if (objects[i].contains(mx, my)) {
					var selected = objects[i];
					state.fromx = mx - selected.x;
					state.fromy = my - selected.y;
					state.moving = true;
					state.selection = selected;
					state.redrawed = false;
					return;
				}
			}
			if(state.selected){
				state.selected = null;
				state.redrawed = false;
			}
		});

		 canvas.bind('mousemove', function(e) {
			if (state.moving){
				var mouse = state.getMouse(e);
      			state.selection.x = mouse.x - myState.dragoffx;
				state.selection.y = mouse.y - myState.dragoffy;   
				state.redrrawed = false;
			}
		});

		canvas.bind('mouseup', function(e) {
			state.moving = false;
		 });
		
		this.interval = 30;
		setInterval(function() { state.draw(); }, state.interval);
	}

	state.prototype.containId = function(id){
		var objects = this.objects;
		var le = objects.length;
		console.log(le);
		for (var i = le-1; i >= 0; i--) {
			if(objects[i] == undefined){
				return false;
			}
			if(objects[i].getId() == id){
				return true;
			}
		}
		return false;
	}

	state.prototype.addRectangle = function(Rectangle){
		this.objects.push(Rectangle);
		this.redrawed = false;
	}

	state.prototype.clear = function(){
		ctx.clearRect(0,0,this.width,this.height);
	}

	state.prototype.getMouse = function(e){
		var element = this.canvas, offsetX = 0, offsetY = 0, mx, my;
  
		if (element.offsetParent !== undefined) {
			do {
				offsetX += element.offsetLeft;
				offsetY += element.offsetTop;
			} while ((element = element.offsetParent));
		}

		mx = e.pageX - offsetX;
		my = e.pageY - offsetY;
  
		return {x: mx, y: my};
	}
	
	state.prototype.draw = function() {
		if (!this.redrawed) {
			var objects = this.objects;
			this.clear();
			var l = objects.length;
			for (var i = 0; i < l; i++) {
				//var obj = objects[i];
				//if (obj.x > this.width || obj.y > this.height || obj.x + obj.data.length*10+30 < 0 || shape.y + 30 < 0) continue;
				objects[i].draw(ctx);
			}
			if (this.selection != null) {
				ctx.strokeStyle = this.selectionColor;
				ctx.lineWidth = this.selectionWidth;
				var selected = this.selection;
				ctx.strokeRect(selected.x,selected.y,selected.data.length*10+30,30);
			}
    		this.redrawed = true;
		}
	}
	});
});

