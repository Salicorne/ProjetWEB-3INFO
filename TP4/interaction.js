
// La création d'un Dnd requière un canvas et un interacteur.
// L'interacteur viendra dans un second temps donc ne vous en souciez pas au départ.
function DnD(canvas, interactor) {
	// Définir ici les attributs de la 'classe'
	this.x1=0;
	this.y1=0;
	this.x2=0;
	this.y2=0;
	this.isDragging = 0;
	// Developper les 3 fonctions gérant les événements
	this.onMouseDown = function(evt){
	    var pos = getMousePosition(canvas, evt);
	    this.x1 = pos.x;
	    this.y1 = pos.y;
	    this.isDragging = 1;
	    interactor.onInteractionStart(this);
	    console.log("Begin DnD : "+this.x1+":"+this.y1+"\n");
	}.bind(this);
	
	this.onMouseUp = function(evt){
	    if (this.isDragging == 1){
	        var pos = getMousePosition(canvas, evt);
	        this.x2 = pos.x;
	        this.y2 = pos.y;
	        this.isDragging = 0;
	        interactor.onInteractionEnd(this);
	        console.log("End DnD : "+this.x2+":"+this.y2+"\n");
	    }
	}.bind(this);
	
	this.onMouseMove = function(evt){
	    if (this.isDragging == 1){
	        var pos = getMousePosition(canvas, evt);
	        this.x2 = pos.x;
	        this.y2 = pos.y;        
    	    interactor.onInteractionUpdate(this);
    	    //console.log("DnD : "+this.x2+":"+this.y2+"\n");
	    }
	}.bind(this);
	
	

	// Associer les fonctions précédentes aux évènements du canvas.
	canvas.addEventListener('mousedown', this.onMouseDown, false);
	canvas.addEventListener('mousemove', this.onMouseMove, false);
	canvas.addEventListener('mouseup', this.onMouseUp, false);
};


// Place le point de l'événement evt relativement à la position du canvas.
function getMousePosition(canvas, evt) {
  var rect = canvas.getBoundingClientRect();
  return {
    x: evt.clientX - rect.left,
    y: evt.clientY - rect.top
  };
};



