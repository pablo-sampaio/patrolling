var renderer;
var screen;
var simulator;
var started = false;

function remove(){
	//screen.destroy(true);
	//document.body.removeChild(renderer.view);
	//document.getElementById("sim").removeChild(renderer.view);
	//started = false;
	//renderer.destroy(true);
	//renderer = null;
}

function start(){
	console.log(started);
	if(started){
		document.body.appendChild(renderer.view);
		//document.getElementById("sim").appendChild(renderer.view);
	}else{
		started = true;

    	//renderer = PIXI.autoDetectRenderer(800, 500,{backgroundColor : 0x1099bb});
    	renderer = new PIXI.CanvasRenderer(800, 500,{backgroundColor : 0x9999ff});
    	//document.getElementById("go").innerHTML = JSON.parse(Android.getNumber())[0][1];
    	//renderer.preserveDrawingBuffer = true;
		document.body.appendChild(renderer.view);
		//document.getElementById("sim").appendChild(renderer.view);
		//scaleToWindow(renderer.view);

		//var data = JSON.parse(Android.getVisits());
		
		var data = JSON.parse(visits);

		simulator = new Simulator(data);
		PIXI.loader.add('spritesheet', 'Assets/sprites.json').load(setUp);
	}
}

function setUp(){
	//PIXI.Texture.SCALE_MODE.DEFAULT = PIXI.Texture.SCALE_MODE.NEAREST;
	simulator.setUp();
	screen = simulator.getScreen();
	gameLoop();
}

function gameLoop(){
	requestAnimationFrame(this.gameLoop);

	// update
	simulator.update();
	
	// check alerts
	renderer.render(screen);
}



