var Node = function(id, img, x, y) {
	this.adjList = [];
	this.id = id;
	this.container = new PIXI.Container();
	var texture = PIXI.Texture.fromImage(img);
	texture.baseTexture.scaleMode = PIXI.SCALE_MODES.NEAREST;
	this.sprite = new PIXI.Sprite(texture);
	this.container.addChild(this.sprite);
	this.sprite.anchor.x = 0.5;
	this.sprite.anchor.y = 0.5;
	this.sprite.position.x = x;
	this.sprite.position.y = y;
	//this.sprite.scale.set(0.25, 0.25);
	
	this.textSprite = new PIXI.Text(this.id+"", {font: "10px sans-serif", fill: "black"});
	this.textSprite.anchor.x = 0.5;
	this.textSprite.anchor.y = 0.5;
	this.textSprite.position.set(x,y);
	this.container.addChild(this.textSprite);
	
	
	
};

Node.prototype.addNeighbor = function(node)
{
	this.adjList.push(node);
};

