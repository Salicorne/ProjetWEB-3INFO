
// Implémenter ici les 4 classes du modèle.
// N'oubliez pas l'héritage !

function Drawing() {
    this.shapes = new Array();
    
    this.paint = function(ctx){
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        ctx.fillStyle = '#F0F0F0';
        ctx.fillRect(0, 0, canvas.width, canvas.height);
        this.shapes.forEach(function(e){e.paint(ctx);});
    }.bind(this);
}

function Shape(col, th) {
    this.color = col;
    this.thickness = th;
}

function Rectangle(x, y, w, h, th, col) {
    Shape.call(this, col, th);
    this.xorig = x; 
    this.yorig = y;
    this.w = w;
    this.h = h;
    
    this.paint = function(ctx){
        ctx.strokeStyle = this.color;
        ctx.lineWidth = this.thickness;
        ctx.strokeRect(this.xorig, this.yorig, this.w, this.h);
    }.bind(this);
}

function Line(x1, y1, x2, y2, th, col) {
    Shape.call(this, col, th);
    this.x1 = x1; 
    this.y1 = y1;
    this.x2 = x2; 
    this.y2 = y2;
    
    this.paint = function(){
        ctx.strokeStyle = this.color;
        console.log(" --> Current color : "+this.color);
        ctx.lineWidth = this.thickness;
        ctx.beginPath();
        ctx.moveTo(this.x1, this.y1);
        ctx.lineTo(this.x2, this.y2);
        ctx.closePath();
        ctx.stroke();
    }.bind(this);
}
