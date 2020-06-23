var Graph = function(nodeImg) {
	this.offset = 50;
	this.nodeList = [];
	this.nodeImg = nodeImg;
	this.container = new PIXI.Container();
	//this.sprite = new PIXI.Sprite(PIXI.Texture.fromImage(img));
	//this.sprite.anchor.x = 0.5;
	//this.sprite.anchor.y = 0.5;
	//this.sprite.position.x = x;
	//this.sprite.position.y = y;
	//this.sprite.scale.set(0.25, 0.25);
	
};

Graph.prototype.generateGrid = function(w, h)
{
	for(var i = 0; i < w; i++){
		for(var j = 0; j < h; j++){
			var y = 100*i + this.offset;
			var x = 100*j + this.offset;
			var id = i*w+j;
			
			this.nodeList.push(new Node(id, this.nodeImg, x, y));
			
			if(i + 1 < h){
				var edge = new Edge(x, y, x, y+100);
				this.container.addChild(edge.container);
			}
			if(j + 1 < w){
				var edge = new Edge(x, y, x+100, y);
				this.container.addChild(edge.container);
			}
		}
	}
	
	for(var i = 0; i < this.nodeList.length; i++){
		this.container.addChild(this.nodeList[i].container);
	}
	
};

