var Button = function(text, buttonImg1, buttonImg2, buttonImg3, x, y, callback){
	
	this.sprite = new PIXI.Sprite(PIXI.Texture.fromImage(buttonImg1));
	this.sprite.scale.set(0.5, 0.5);
	this.sprite.anchor.x = 0.5;
	this.sprite.anchor.y = 0.5;
	//this.sprite.position.x = x;
	//this.sprite.position.y = y;
	this.sprite.buttonMode = true;
	this.sprite.interactive = true;
	
	this.sprite.on("pointerdown", this.onButtonDown.bind(this));
	this.sprite.on("pointerupoutside", this.onButtonUpOutside.bind(this));
	this.sprite.on("pointerup", this.onButtonUp.bind(this));
	this.sprite.on("pointerover", this.onButtonOver.bind(this));
	this.sprite.on("pointerout", this.onButtonOut.bind(this));
	
	this.text = text;
	this.buttonImg1 = buttonImg1;
	this.buttonImg2 = buttonImg2;
	this.buttonImg3 = buttonImg3;
	this.x = x;
	this.y = y;
	this.callback = callback;
	
	//to improve
	this.hasLabel = false;
	this.textSprite;
	this.buttonContainer = new PIXI.Container();
	this.buttonContainer.position.x = x;
	this.buttonContainer.position.y = y;
	this.buttonContainer.addChild(this.sprite);
	//this.buttonContainer.scale.set(0.5, 0.5);
	
	this.isEnabled = true;

};

Button.prototype.addLabel = function()
{
	this.hasLabel = true;
	this.textSprite = new PIXI.Text(this.text, {font: "10px sans-serif", fill: "black"});
	this.textSprite.position.set(-20, -10);
	//this.buttonContainer = new PIXI.Container();
	//this.buttonContainer.addChild(this.sprite);
	this.buttonContainer.addChild(this.textSprite);
};

Button.prototype.disable = function()
{
	this.isEnabled = false;
	this.sprite.texture = PIXI.Texture.fromImage(this.buttonImg3);
}

Button.prototype.enable = function()
{
	this.isEnabled = true;
	this.sprite.texture = PIXI.Texture.fromImage(this.buttonImg1);
}

Button.prototype.onButtonDown = function()
{
	if(this.isEnabled)
	{
		this.sprite.isdown = true;
		//this.sprite.scale.set(0.5, 0.5);
		this.buttonContainer.scale.set(0.5, 0.5);
	}
};

Button.prototype.onButtonUp = function(){
	if(this.isEnabled){
		this.sprite.isdown = false;

		this.buttonContainer.scale.set(1, 1);
        this.sprite.texture = PIXI.Texture.fromImage(this.buttonImg1);
        this.callback(this, this.text);
	}
};

Button.prototype.onButtonUpOutside = function(){
	if(this.isEnabled){
		this.sprite.isdown = false;

		this.buttonContainer.scale.set(1, 1);
		this.sprite.texture = PIXI.Texture.fromImage(this.buttonImg1);

	}
};

Button.prototype.onButtonOver = function()
{
	if(this.isEnabled)
	{
		this.sprite.isOver = true;
		
		if (this.isdown)
		{
			return;
		}
		
		this.sprite.texture = PIXI.Texture.fromImage(this.buttonImg2);
	}
};

Button.prototype.onButtonOut = function()
{
	if(this.isEnabled)
	{	
		this.sprite.isOver = false;

		if (this.sprite.isdown)
		{
			return;
		}

		this.sprite.texture = PIXI.Texture.fromImage(this.buttonImg1);
	}
};

