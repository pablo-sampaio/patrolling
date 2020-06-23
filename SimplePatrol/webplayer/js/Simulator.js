//todo element's positions

var Simulator = function(data) {

	this.data = data;
	this.graph;
	this.screen;
	this.renderer;
	this.visits = data;
	this.robotsNumber = this.visits.length;
	
	this.playButton;
	this.pauseButton;
	this.stopButton;
	this.speedUpButton;
	
	this.robots = [];
	this.pause = true;

	// scene sprites
	

	// containers

};

//ToSee
Simulator.prototype.update = function() {
	if(this.pause){
		return;
	}

	for(var robot of this.robots){
		if(!robot.finish){
			robot.update();
		}else{
			if(!robot.sitting){
				robot.sitAnimation();
			}
		}
	}
	
	for(var robot of this.robots){
		if(!robot.finish){
			return;
		}
	}
	
};

Simulator.prototype.setUp = function() {
	var assets = 'Assets/';
	
	//for(var i = 0; i < 5; i++){
	//	for(var j = 0; j < 5; j++){
	//		this.nodes.push(new Node(i+j, assets+"circle.png", 100*i, 100*j));	
	//	}
	//}
	
	this.graph = new Graph(assets+"n4.png");
	this.graph.generateGrid(5,5);
	
	var colors = ["green","teal","orange","pink","grey"];
	
	for(var i = 0; i < this.robotsNumber; i++){
		this.robots.push(new Robot(i, assets+"robot.png", this.visits[i], this.graph, colors[i]));
	}
	
	this.playButton = new Button('Play', assets + 'play.png', assets
			+ 'play2.png', assets + 's.png', 550, 400, this.onPlay
			.bind(this));
			
	this.pauseButton = new Button('Pause', assets + 'pause.png', assets
			+ 'pause2.png', assets + 's.png', 550, 400, this.onPause
			.bind(this));
			
	this.stopButton = new Button('Stop', assets + 'stop.png', assets
			+ 'stop2.png', assets + 's.png', 550, 300, this.onStop
			.bind(this));
			
	this.speedUpButton = new Button('SpeedUp', assets + 'speedUp.png', assets
			+ 'speedUp2.png', assets + 's.png', 550, 200, this.onSpeedUp
			.bind(this));
	
	//
	//this.getScreen();
	
	//this.renderer = PIXI.autoDetectRenderer(800, 600);
	//document.body.appendChild(this.renderer.view);
	
	//simulatorLoop();
};

Simulator.prototype.getScreen = function() {
	this.screen = new PIXI.Container();
	
	//for(var i = 0; i < this.nodes.length; i++){
	//	this.screen.addChild(this.nodes[i].sprite);
	//}
	
	this.screen.addChild(this.graph.container);
	for(var robot of this.robots){
		this.screen.addChild(robot.container);
	}
	
	//TODO
	this.screen.addChild(this.playButton.buttonContainer);
	this.screen.addChild(this.stopButton.buttonContainer);
	this.screen.addChild(this.speedUpButton.buttonContainer);
	
	//TODO
	return this.screen;
};

Simulator.prototype.onPlay = function() {
	this.pause = false;
	this.screen.removeChild(this.playButton.buttonContainer);
	this.screen.addChild(this.pauseButton.buttonContainer);
	for(var robot of this.robots){
		robot.walkAnimation();
	}

	//this.button.sprite.interactive = false;
};

Simulator.prototype.onPause = function() {
	this.pause = true;
	this.screen.removeChild(this.pauseButton.buttonContainer);
	this.screen.addChild(this.playButton.buttonContainer);
	//this.button.sprite.interactive = false;
	for(var robot of this.robots){
		robot.sitAnimation();
	}
};

Simulator.prototype.onSpeedUp = function() {
	for(var robot of this.robots){
		robot.speed += 1;
	}

};

Simulator.prototype.onStop = function() {
	this.onPause();
	for(var robot of this.robots){
		robot.reset();
	}
	

};

Simulator.prototype.teste = function() {
	console.log("HELLO");
	

};


