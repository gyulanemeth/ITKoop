/**
	A mozgatott objektumaink reprezentációja. A Kinetic.Text altípusa.
*/
function ITKoobject(id, x, y, label, color) {
	this.ITKoobjId=id;

	var config = {
		x: x,
		y: y,
		text: label,
		fill: color,
		padding: 5,
		draggable: true,

		align: "center",
		verticalAlign: "middle",
		fontFamily: "Arial",
		fontSize: 12,
		stroke: "black",
		strokeWidth: 1,
		textFill: "white"
	};

	Kinetic.Text.apply(this, [config]);		// a Kinetic.Text konstruktorának hívása
}

ITKoobject.prototype=new Kinetic.Text({
	x: 0,
	y: 0,
	text: ">_>"
});

/**
	Üzenet a szervernek az objektum mozgatásáról.
*/
ITKoobject.prototype.sendMovementMessage = function() {
	var m={
		type: '2',
		message: {
			objId: this.ITKoobjId,
			x: this.x,
			y: this.y,
			savePos: true
		}
	}
	message(m);
};
Kinetic.GlobalObject.extend(ITKoobject, Kinetic.Text);


/**
	Canvast kezelő objektum. Konstruktor.

	@param canvas_container  az a html node, ahová a canvast be szeretnénk szúrni.
*/
function canvasing(canvas_container) {

	this.stage=null;  ///< a Kinetic.Stage magában foglalja a canvast is.
	this.board=null;  ///< erre a Kinetic.Layerre fogunk rajzolni.
	this.objmap = {};  ///< ebben a mapben tároljuk az objektumainkat {id: <Kinetic.Text object>} formában

	this.stage=new Kinetic.Stage(canvas_container, 800,600);	//ez létrehozza a canvast is.
	this.board=new Kinetic.Layer();

	this.stage.add(this.board);

	this.createObject(0, 50, 50, "ITK");
	this.createObject(1, 100, 100, "Kooperatív");
	this.createObject(2, 200, 200, "Izé");	

	this.stage.draw();

	this.moveObject(2, 200,200);
}

canvasing.prototype.colors=['#ff0000', '#00ff00', '#0000ff']; ///< ebből a listából választunk színt az objektumoknak: id % colors.length


/**
	Objektum lokális létrehozása a vásznon.
*/
canvasing.prototype.createObject=function(id, x, y, label) {
	this.objmap[id] = new ITKoobject(id, x, y, label, this.colors[id % this.colors.length] );

	this.objmap[id].on("dragstart", this.objmap[id].moveToTop);

	//notify server about movement
	this.objmap[id].on("dragend", this.objmap[id].sendMovementMessage);

	this.board.add(this.objmap[id]);
	return true;
}

/**
	Objektum lokális, programatikus mozgatása a vásznon.
*/
canvasing.prototype.moveObject=function(id, x, y) {
	if(!this.objmap[id]) throw ("Wanted to move object #" +id+", no such object.");

	if(this.objmap[id].x==x || this.objmap[id].y==y) return true;

	this.objmap[id].x=x;
	this.objmap[id].y=y;

	this.stage.draw();
}
