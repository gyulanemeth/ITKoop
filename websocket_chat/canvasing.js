/**
	A canvast kezelő függvények.
*/

var stage=null;  ///< a Kinetic.Stage magában foglalja a canvast is.
var board=null;  ///< erre a Kinetic.Layerre fogunk rajzolni.

var colors=['#ff0000', '#00ff00', '#0000ff']; ///< ebből a listából választunk színt az objektumoknak: id % colors.length

/**
	Inicializálás
*/
function canvasingInit() {
	if(!document.getElementById('canvas_cont')) return false;
	
	stage=new Kinetic.Stage("canvas_cont", 800,600);	//ez létrehozza a canvast is.
	board=new Kinetic.Layer();

	stage.add(board);

	createObject(0, 50, 50, "ITK");
	createObject(1, 100, 100, "Kooperatív");
	createObject(2, 200, 200, "Izé");	

	stage.draw();

	moveObject(2, 100,200);
	return true;
}

var objmap = {};	// ebben a mapben tároljuk az objektumainkat {id: <Kinetic.Text object>} formában

/**
	Lokálisan új objektum létrehozása. Wrapper a new Kinetic.Text-hez.
*/
function createObject(id, x, y, label) {
	objmap[id] = new Kinetic.Text({
		x: x,
		y: y,
		text: label,
		stroke: "black",
		strokeWidth: 1,
		fill: colors[id % colors.length],
		fontSize: 12,
		fontFamily: "Arial",
		textFill: "white",
		padding: 5,
		align: "center",
		verticalAlign: "middle",
		draggable: true
	});


	objmap[id].on("dragstart", function(){
		objmap[id].moveToTop();
	});


	//notify server about movement
	objmap[id].on("dragend", function(){
				sendMovementMessage(id);
            });

	board.add(objmap[id]);
	return true;
}

/**
	Objektum lokális, programatikus mozgatása.
*/
function moveObject(id, x, y) {
	if(!objmap[id]) {
		logToConsole("Wanted to move object #" +id+", no such object.", "error");
		return false;
	}

	if(objmap[id].x==x || objmap[id].y==y) return true;

	objmap[id].x=x;
	objmap[id].y=y;

	stage.draw();

	return true;
}

/**
	Üzenet küldése a szervernek id azonosítójú objektum mozgatásáról.
*/
function sendMovementMessage(id) {
	var m={
		type: '2',
		message: {
			objId: id,
			x: objmap[id].x,
			y: objmap[id].y,
			savePos: true
		}
	};

	message(m);
}
