window.onload = function () {
    document.body.onresize = function () {
       var canvasNode = document.getElementById('canvas');
       canvasNode.width = canvasNode.parentNode.clientWidth;
       canvasNode.height = canvasNode.parentNode.clientHeight;
    }
    document.body.onresize();
};

function s_open(){
	console.log("succesfull connect");
}

function s_message(){
	console.log("tokom tudja, uzenet");
}

function s_close(){
	console.log("disconnected");
}

function s_error(){
	console.log("gondolom baj van");
}

$(document).ready(function() {

	$("#login_btn").click(function() {
		user = $("#login_table input:first-child").val();
		console.log(user);
		if ("WebSocket" in window)
		{
			ws = new WebSocket("ws://nemgy.itk.ppke.hu:61160");
			ws.onopen = s_open();
			ws.onmessage = s_message();
			ws.onclose = s_close();
			ws.onerror = s_error();
		}
		else
		{
			alert("WebSocket NOT supported by your Browser!");
		}
	});

});
