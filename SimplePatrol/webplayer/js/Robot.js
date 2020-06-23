var Robot = function(id, img, visitList, graph, color) {
	this.target = 0;
	this.moving = false;
	this.graph = graph;
	this.speed = 1;
	this.finish = false;
	
	this.sitting = false;

	this.visitList = visitList;
	this.id = id;
	this.container = new PIXI.Container();
	//this.sprite = new PIXI.Sprite(PIXI.Texture.fromImage(img));
	//TODO
	
	this.color = color;
	
	this.walkTextures = [];
	this.sitTextures = [];
	
	for(var i = 0; i < 6; i++){
		var texture = PIXI.Texture.fromFrame(color + 'Walk' + i);
		texture.baseTexture.scaleMode = PIXI.SCALE_MODES.NEAREST;
		this.walkTextures.push(texture);
	}
	
	for(var i = 0; i < 10; i++){
		//var texture = PIXI.Texture.fromFrame(color + 'Sit' + i);
		var number = Math.floor((Math.random() * (3 - 0) + 0));
		var texture = PIXI.Texture.fromFrame(color + 'Sit' + number);
		texture.baseTexture.scaleMode = PIXI.SCALE_MODES.NEAREST;
		this.sitTextures.push(texture);
	}
	
	this.sprite = new PIXI.extras.AnimatedSprite(this.sitTextures);
	//this.sprite = new PIXI.extras.MovieClip(spriteTextures);
	//this.sprite.animationSpeed = 0.1;
	this.sprite.animationSpeed = 0.05;
	//this.sprite.tint = Math.random() * 0xFFFFFF;
	this.sprite.scale.set(5);
	//this.sprite.gotoAndPlay(0);
	this.sprite.gotoAndPlay(Math.random() * 27);
	//this.sprite.play();
	////
	
	this.container.addChild(this.sprite);
	this.sprite.anchor.x = 0.5;
	this.sprite.anchor.y = 0.5;
	this.sprite.position.x = this.graph.nodeList[this.visitList[this.target]].sprite.position.x;
	this.sprite.position.y = this.graph.nodeList[this.visitList[this.target]].sprite.position.y;
	//this.sprite.scale.set(0.25, 0.25);
	
	//this.textSprite = new PIXI.Text(this.id+"", {font: "10px sans-serif", fill: "black"});
	//this.textSprite.position.set(x,y);
	//this.container.addChild(this.textSprite);
	
	
};

Robot.prototype.visitNext = function()
{
	var next = this.target + 1;
	if(next < this.visitList.length){
		this.target = next;
		this.moving = true;
	}else{
		this.finish = true;
	}
};

Robot.prototype.update = function()
{	
	if(this.moving){
		this.goTo(this.speed);
	}else{
		this.visitNext();
	}
};

Robot.prototype.reset = function()
{	
	this.target = 0;
	this.moving = false;
	this.sitting = false;
	this.speed = 1;
	this.finish = false;
	this.sprite.position.x = this.graph.nodeList[this.visitList[this.target]].sprite.position.x;
	this.sprite.position.y = this.graph.nodeList[this.visitList[this.target]].sprite.position.y;
};

Robot.prototype.walkAnimation = function()
{	
	this.sitting = false;
	this.sprite.textures = this.walkTextures;
	this.sprite.animationSpeed = 0.1;
	this.sprite.gotoAndPlay(Math.random() * 27);
};

Robot.prototype.sitAnimation = function()
{	
	this.sitting = true;
	this.sprite.textures = this.sitTextures;
	this.sprite.animationSpeed = 0.05;
	this.sprite.gotoAndPlay(Math.random() * 27);
};

Robot.prototype.goTo = function(speed)
{
	var speed = speed;	
	var robotX = this.sprite.position.x;
	var robotY = this.sprite.position.y;
	
	var targetX = this.graph.nodeList[this.visitList[this.target]].sprite.position.x;
	var targetY = this.graph.nodeList[this.visitList[this.target]].sprite.position.y;
	
	if(robotX == targetX){
		
		if(robotY > targetY){
			speed*=-1;
			if(robotY + speed <= targetY){
				this.sprite.position.y = targetY;
				this.moving = false;
			}else{
				this.sprite.position.y = robotY + speed;
			}
			
		}else{
			if(robotY + speed >= targetY){
				this.sprite.position.y = targetY;
				this.moving = false;
			}else{
				this.sprite.position.y = robotY + speed;
			}
		}
		
		
	}
	
	if(robotY == targetY){		
		
		if(robotX > targetX){
			speed*=-1;
			if(robotX + speed <= targetX){
				this.sprite.position.x = targetX;
				this.moving.false;
			}else{
				this.sprite.position.x = robotX + speed;
			}	
		}else{
			if(robotX + speed >= targetX){
				this.sprite.position.x = targetX;
				this.moving.false;
			}else{
				this.sprite.position.x = robotX + speed;
			}	
		}
		
		
	}
};

