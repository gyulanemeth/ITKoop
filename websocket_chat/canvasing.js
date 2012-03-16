/**
	A canvast kezelő függvények.
*/

var canvas=null;
var context=null;

var colors=['#ff0000', '#00ff00', '#0000ff'];

function canvasingInit() {
	if(!document.getElementById('canvas')) return false;
	
	canvas=document.getElementById('canvas');
	context=canvas.getContext('2d');

	createObject(0, 50, 50, "Nulla");
	createObject(1, 300, 300, "Egy");
	createObject(2, 300, 300, "Kettő");

	draw();

	return true;
}

var objmap = {};	// ebben a mapben tároljuk az objektumainkat { <identifier> : {x: <xval>, y: <yval>, message: <msg>} } formában. Kezdetben üres.

function createObject(id, x, y, label) {
	objmap[id] = { x: x, y: y, message: label };
	return true;
}

function moveObject(id, x, y) {
	if(!objmap[id]) {
		logToConsole("Wanted to move object #" +id+", no such object.");
		return false;
	}

	objmap[id].x=x;
	objmap[id].y=y;
	return true;
}

function draw() {
	context.clearRect(0, 0, canvas.width, canvas.height);

	for (obj in objmap) {
		context.beginPath();
		context.rect(objmap[obj].x, objmap[obj].y, 20, 20);
			context.fillStyle=colors[ obj % colors.length ];
		context.fill();
			context.lineWidth=1;
			context.strokestyle="#000";
		context.stroke();
	}
}
