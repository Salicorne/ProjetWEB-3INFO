
var editingMode = { rect: 0, line: 1 };

function Pencil(ctx, drawing, canvas) {
	this.currEditingMode = editingMode.line;
	this.currLineWidth = 5;
	this.currColour = '#000000';
	this.currentShape = 0;

	// Liez ici les widgets à la classe pour modifier les attributs présents ci-dessus.
	
    this.updateColor = function() {
        this.currColour = document.getElementById('colour').value;
    }.bind(this);
    
	picker.addEventListener('change', this.updateColor, false);

	new DnD(canvas, this);

	// Implémentez ici les 3 fonctions onInteractionStart, onInteractionUpdate et onInteractionEnd
    
	
	this.onInteractionStart = function(dnd){
	    console.log("Interaction start");
	    switch(this.currEditingMode) {
	        case editingMode.rect: {
	            this.currentShape = new Rectangle(dnd.x1, dnd.y1, 0, 0, this.currLineWidth, this.currColour);
	            break;
	        }
	        case editingMode.line: {
	            console.log(this.currColour);
    	        this.currentShape = new Line(dnd.x1, dnd.y1, dnd.x1, dnd.y1, this.currLineWidth, this.currColour);
	            break;
	        }
	    }
	}.bind(this);
	
	this.onInteractionUpdate = function(dnd){
	    console.log("Interaction update");
	    switch(this.currEditingMode) {
	        case editingMode.rect: {
	            this.currentShape.w = dnd.x2 - dnd.x1;
	            this.currentShape.h = dnd.y2 - dnd.y1;
	            break;
	        }
	        case editingMode.line: {
    	        this.currentShape.x2 = dnd.x2;
    	        this.currentShape.y2 = dnd.y2;
	            break;
	        }
	    }
	    drawing.paint(ctx);
	    this.currentShape.paint(ctx);
	}.bind(this);
	
	this.onInteractionEnd = function(dnd){
	    console.log("Interaction end");
//	    this.currentShape.paint(ctx);
	    drawing.shapes.push(this.currentShape);
	    drawing.paint(ctx);
	}.bind(this);
};


