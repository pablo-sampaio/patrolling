var Edge = function(x0, y0, x, y) {
	this.lineWidth = 5;
	this.lineColor = "0x000000";
	this.container = new PIXI.Container();
	
	var line = new PIXI.Graphics();
	this.container.addChild(line);
	
	line.position.set(0, 0);
	line.lineStyle(this.lineWidth, this.lineColor);
	line.moveTo(x0, y0);
	line.lineTo(x, y);
	//line.position.set(x0, y0);
	
	
	//this.sprite = new PIXI.Sprite(PIXI.Texture.fromImage(img));
	//this.sprite.anchor.x = 0.5;
	//this.sprite.anchor.y = 0.5;
	//this.sprite.position.x = x;
	//this.sprite.position.y = y;
	//this.sprite.scale.set(0.25, 0.25);
	
};

