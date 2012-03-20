/**
	A mozgatott objektumaink reprezentációja. A Kinetic.Text altípusa.
*/
function ITKoobject(id, x, y, label, color) {
	this.ITKoobjId=id;
	this.lastMessage = 0; ///< Ekkor küldtünk ki utoljára üzenetet az objektum savePos=false jellegű mozgatásáról.
	

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
//a Kinetic beépített extends öröklődést támogató eszköze.
Kinetic.GlobalObject.extend(ITKoobject, Kinetic.Text);

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

ITKoobject.prototype.msgPerSecond=30; ///< maximum ennyi savePos=false üzenetet küldünk ki másodpercenként. FPS.
/**
	A szerver értesítése ideiglenes mozgatásról.
*/
ITKoobject.prototype.sendTempMoveMessage=function(){
	var milliDelay=1000/this.msgPerSecond;

	// ha már eltelt 1000/MPS idő a legutóbbi üzenet óta, akkor kiküldünk egy üzenetet.
	if ( Number(new Date()) > (this.lastMessage+milliDelay) ) {
		message({
			type: '2',
			message: {
				objId: this.ITKoobjId,
				x: this.x,
				y: this.y,
				savePos: false
			}
		});
		this.lastMessage=Number(new Date());
	}
}

/**
	Canvast kezelő objektum. Konstruktor.

	@param canvas_container  az a html node, ahová a canvast be szeretnénk szúrni.
*/
function canvasing(canvas_container) {
	this.stage=null;  ///< a Kinetic.Stage magában foglalja a canvast is.
	this.board=null;  ///< erre a Kinetic.Layerre fogunk rajzolni.
	this.objmap = {};  ///< ebben a mapben tároljuk az objektumainkat {id: <ITKoobject>} formában

	this.stage=new Kinetic.Stage(canvas_container, 800,600);	//ez létrehozza a 800*600-as canvast és appendolja a canvas_containerhez
	this.board=new Kinetic.Layer();

	this.stage.add(this.board);

	/*
	this.createObject(0, 50, 50, "ITK");
	this.createObject(1, 100, 100, "Kooperatív");
	this.createObject(2, 200, 200, "Izé");
	*/

	this.stage.draw();
}

canvasing.prototype.colors=['#ff0000', '#00ff00', '#0000ff', "#ff00ff", "#00ffff", "#ffff00", "#000000"]; ///< ebből a listából választunk színt az objektumoknak: <sorszám> % colors.length


/**
	Objektum lokális létrehozása a vásznon.
*/
canvasing.prototype.createObject=function(id, x, y, label) {
	this.objmap[id] = new ITKoobject(id, x, y, label, this.colors[ this.board.getChildren().length % this.colors.length ] );

	// ha húzzni kezdjük, kerüljön legfelülre
	this.objmap[id].on("dragstart", this.objmap[id].moveToTop);

	//notify server about movement on end of dragging
	this.objmap[id].on("dragend", this.objmap[id].sendMovementMessage);

	//akkor is küldjünk savePos=false üzenetet, ha csak húzzuk
	this.objmap[id].on("dragmove", this.objmap[id].sendTempMoveMessage);

	this.board.add(this.objmap[id]);
	return true;
}

/**
	Objektum lokális, programatikus mozgatása a vásznon.
*/
canvasing.prototype.moveObject=function(id, x, y, label) {
	if(!this.objmap[id]) {	// ha még nem létezik, készítsük el.
		this.createObject(id, x, y, label);
		this.stage.draw();
		return true;
	}
	else if(label && this.objmap[id].getText() != label) {
		this.objmap[id].setText(label);
	}

	if(this.objmap[id].x==x || this.objmap[id].y==y) return true;

	this.objmap[id].setPosition(x, y);

	this.stage.draw();
}

/**
	Az összes objektum törlése.
*/
canvasing.prototype.clear=function() {
	this.board.removeChildren();
	this.objmap={};
	this.stage.draw();
}
