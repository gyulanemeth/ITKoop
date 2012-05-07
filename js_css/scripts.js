window.onload = function () {
    document.body.onresize = function () {
       var canvasNode = document.getElementById('canvas');
       canvasNode.width = canvasNode.parentNode.clientWidth;
       canvasNode.height = canvasNode.parentNode.clientHeight;
    }
    document.body.onresize();
};

function s_open(ev){
	console.log("succesfull connect");
}

function s_message(ev){
	console.log("tokom tudja, uzenet");
}

function s_close(ev){
	console.log("disconnected");
}

function s_error(ev){
	console.log("gondolom baj van");
}

$(document).ready(function() {

	$("#login_btn").click(function() {
		user = $("#login_table input:first-child").val();
		if(user == ""){
			user = "anonymus" + Math.ceil(Math.random()*100000);
		}
		console.log(user);
		if ("WebSocket" in window)
		{
			ws = new WebSocket("ws://nemgy.itk.ppke.hu:61160");
			ws.onopen = function(ev){ s_open(ev);}
			ws.onmessage = function(ev){s_message(ev);}
			ws.onclose = function(ev){s_close(ev);}
			ws.onerror = function(ev){s_error(ev);}
		}
		else
		{
			alert("WebSocket NOT supported by your Browser!");
		}
	});

	$("#chat_input button").click( function() {
		
	});

});
