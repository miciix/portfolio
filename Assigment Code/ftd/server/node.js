function randint(n){ return Math.round(Math.random()*n); }
function rand(n){ return Math.random()*n; }
function getRandomNumber(min, max) {return Math.round(Math.random() * (max - min) + min);}
function getPosition(angle, distance){
  var x = Math.cos(angle) * distance;
  var y = Math.sin(angle) * distance;
  return new Pair(x, y);
}


class Stage {
	constructor(mode){
		this.worldwidth = 1000;
		this.worldheight = 1000;
		// the logical width and height of the stage
		this.width=500;
		this.height=500;

		this.worldx = -(this.worldwidth-this.width)/2;
		this.worldy = -(this.worldwidth-this.width)/2;
		this.player_num = 0;
		this.actors=[]; // all actors on this stage (monsters, player, Obstaclees, ...)

		var enemy_health = 3;
		this.num_of_enemy = 3;
		this.multiplier = 2;
		
		this.current_num_of_enemy = this.num_of_enemy;

		switch (mode) {
			case "easy":
				enemy_health = 3;
				break;
		
			case "normal":
				player_health = 5;
				enemy_health = 3;
				num_of_enemy = 5;
				break;

			case "hard":
				player_health = 1;
				enemy_health = 5;
				num_of_enemy = 10;
				break;
		}	
		//	Add ammo to stage
		this.addActor(new Weapon(this,new Pair(500,500),"#b3d1ff",10,"shotgun"));
		this.addActor(new Weapon(this,new Pair(200,200),"#b3d1ff",10,"shotgun"));
		var num_of_ammo=5;
		this.generateAmmo(num_of_ammo)
		// Add in enemies
		this.generateEnemy(this.num_of_enemy);
		//add obstacles
		var num_obstacle=10;
		while(num_obstacle>0){
			var x=getRandomNumber(this.worldx,this.worldwidth+this.worldx);
			var y=getRandomNumber(this.worldx,this.worldwidth+this.worldx);
			while(this.withinSpawnRange(x,y)){
				x=getRandomNumber(this.worldx,this.worldwidth+this.worldx);
				y=getRandomNumber(this.worldx,this.worldwidth+this.worldx);
			}
			if(this.getActor(x,y)===null){
				var velocity = new Pair(1, 1);
				var radius = 10;
				var colour = 'rgba(218, 113, 113,0.5)';
				var position = new Pair(x,y);
				var b = new Obstacle(this,position,colour,50)
				this.addActor(b);
				num_obstacle--;
			}
		}
	}
	withinSpawnRange(x,y){
		var width = (this.width/3)*2
		return x< width && x > width/3 &&  y <width && y >width/3;
	}
	addPlayer(player){
		var player_health = 10;
		// Add the player to the center of the stage
		var velocity = new Pair(0,0);
		var radius = 20;
		var red=randint(255), green=randint(255), blue=randint(255);
		var colour= 'rgba('+red+','+green+','+blue+',1)';
		var degree = 0;
		var playerid = this.player_num + 1;
		this.player_num  += 1;
		var position = new Pair(Math.floor(this.width/2), Math.floor(this.height/2));
		var player = new Player(this, playerid, position, velocity, colour, radius,degree,10,player_health,"handgun")
		this.addActor(player);
		return playerid;
	}
	generateAmmo(num_of_ammo){
		while(num_of_ammo>0){
			var x=Math.floor((Math.random()*this.width)); 
			var y=Math.floor((Math.random()*this.height)); 
			if(this.getActor(x,y)===null){
				var radius = 10;
				var colour= "#ff66d9";
				var position = new Pair(x,y);
				var b = new Ammo(this,position,colour,10,radius);
				this.addActor(b);
				num_of_ammo--;
			}
		}
	}
	generateEnemy(num_of_enemy){
		while(num_of_enemy>0){
			var x=Math.floor((Math.random()*this.width)); 
			var y=Math.floor((Math.random()*this.height)); 
			if(this.getActor(x,y)===null){
				var velocity = new Pair(1, 1);
				var radius = 10;
				var colour= 'green';
				var position = new Pair(x,y);
				var b = new Enemy(this, position, velocity, colour, radius,0,3);
				b.headTo(new Pair(250,250));
				this.addActor(b);
				num_of_enemy--;
			}
		}
	}
	removePlayer(player){
		this.removeActor(player);
	}

	addActor(actor){
		this.actors.push(actor);
	}
	
	removeActor(actor){
		var index=this.actors.indexOf(actor);
		if(index!=-1){
			this.actors.splice(index,1);
		}
	}

	// Take one step in the animation of the game.  Do this by asking each of the actors to take a single step. 
	// NOTE: Careful if an actor died, this may break!
	step(){
		if (this.current_num_of_enemy < 1) {
			var num_of_enemy = this.multiplier * this.num_of_enemy
			this.generateEnemy(num_of_enemy);
			this.generateAmmo(2);
			this.current_num_of_enemy = num_of_enemy;
			this.num_of_enemy +=1;
		}
		for(var i=0;i<this.actors.length;i++){
			this.actors[i].step();
		}
	}

	//check if two actors collide
	isCollide(actor1,actor2,hitbox_correction){
		var dist = Math.hypot(actor1.position.x-actor2.position.x,
			actor1.position.y-actor2.position.y);
		if ((dist - actor2.radius - actor1.radius + hitbox_correction) < 0){
			return true;
		}
		return false;
	}
	// return the first actor at coordinates (x,y) return null if there is no such actor
	getActor(x, y){
		for(var i=0;i<this.actors.length;i++){
			if(this.actors[i].x==x && this.actors[i].y==y){
				return this.actors[i];
			}
		}
		return null;
	}
	getNearPlayer(spawn_position){
		var headto_position_x = 250;
		var headto_position_y = 250;
		var prev_dist = Infinity;
		var player_dist = {};
		for(var i=0;i<this.actors.length;i++){
			if (this.actors[i].constructor.name == "Player") {
				var player = this.actors[i];
				var dist = Math.hypot(spawn_position.x-player.position.x,
					spawn_position.y-player.position.y);
				player_dist[player.id] = dist;
				
				
				if (dist <= prev_dist) {
					headto_position_x = player.position.x;
					headto_position_y = player.position.y;
					prev_dist = dist;
				}
			}
		}
		return new Pair(headto_position_x, headto_position_y);
	}
	shoot(){
		
		for(var i=0;i<this.actors.length;i++){
			if (this.actors[i].constructor.name == "Enemy") {
				var spawn_position = new Pair(this.actors[i].x,this.actors[i].y);
				var player_location = this.getNearPlayer(spawn_position)
				this.actors[i].shoot(player_location);
			}
			
		};
	}
	
} // End Class Stage

class Pair {
	constructor(x,y){
		this.x=x; this.y=y;
	}

	toString(){
		return "("+this.x+","+this.y+")";
	}

	normalize(){
		var magnitude=Math.sqrt(this.x*this.x+this.y*this.y);
		this.x=this.x/magnitude;
		this.y=this.y/magnitude;
	}
}

class Enemy {
	constructor(stage, position, velocity, colour, radius,degree,hp){
		this.stage = stage;
		this.position=position;
		this.intPosition(); // this.x, this.y are int version of this.position
		this.degree = degree;
		this.velocity=velocity;
		this.colour = colour;
		this.radius = radius;
		this.healthpoint = hp;
	}
	
	headTo(position){
		this.velocity.x=(position.x-this.position.x);
		this.velocity.y=(position.y-this.position.y);
		this.velocity.normalize();
	}

	toString(){
		return this.position.toString() + " " + this.velocity.toString();
	}

	step(){
		
		this.position.x=this.position.x+this.velocity.x;
		this.position.y=this.position.y+this.velocity.y;
		
		for(var i=0;i<this.stage.actors.length;i++){
			if(this.stage.actors[i].constructor.name == "Obstacle" && this.stage.isCollide(this,this.stage.actors[i],0)){
				this.velocity.x=-this.velocity.x
				this.velocity.y=-this.velocity.y

			}
		}
		// bounce off the walls
		if(this.position.x<this.stage.worldx+this.radius){
			this.position.x=this.stage.worldx+this.radius;
			this.velocity.x=Math.abs(this.velocity.x);
			
		}
		if(this.position.x>this.stage.worldwidth+this.stage.worldx-this.radius){
			this.position.x=this.stage.worldwidth+this.stage.worldx-this.radius;
			this.velocity.x=-Math.abs(this.velocity.x);
		}
		if(this.position.y<this.stage.worldy+this.radius){
			this.position.y=this.stage.worldy+this.radius;
			this.velocity.y=Math.abs(this.velocity.y);
		}
		if(this.position.y>this.stage.worldheight+this.stage.worldx-this.radius){
			this.position.y=this.stage.worldheight+this.stage.worldx-this.radius;
			this.velocity.y=-Math.abs(this.velocity.y);
		}
		this.intPosition();

	}
	intPosition(){
		this.x = Math.round(this.position.x);
		this.y = Math.round(this.position.y);
	}
	shoot(player_location){
		var velocity = new Pair(5,5);
		var spawn_position = new Pair(this.position.x,this.position.y);
		var bullet = new Bullet(this.stage,spawn_position,velocity,'blue',5,0,"enemy",2);
		bullet.headTo(player_location);
		this.stage.addActor(bullet);
	}
	
}

class Player extends Enemy {
	constructor(stage,id, position, velocity, colour, radius,degree,ammo,hp,weapon) {
		super(stage, position, velocity, colour, radius,degree,hp)
		this.ammo = ammo;
		this.center = new Pair(this.position.x+this.radius/2,this.position.y+this.radius/2)
		this.weapon = weapon;
		this.id = id;
		this.score = 0;
	}
	step(){
		this.position.x=this.position.x+this.velocity.x;
		this.position.y=this.position.y+this.velocity.y;
		this.center.x  = this.position.x + this.radius/2;
		this.center.y = this.position.y + this.radius/2;

		for(var i=0;i<this.stage.actors.length;i++){
			switch (this.stage.actors[i].constructor.name) {
				case "Obstacle":
					if (this.isCollide(this.stage.actors[i])) {
						this.position.x=this.position.x-this.velocity.x;
						this.position.y=this.position.y-this.velocity.y;
						this.velocity.x=0;
						this.velocity.y=0;
					}
					break;
				case "Ammo":
					var ammo = this.stage.actors[i];
					if (this.stage.isCollide(ammo,this,ammo.radius/2)) {
						this.ammo+=ammo.quantity;
						this.stage.removeActor(ammo);
					}
					break;
				case "Weapon":
					var weapon = this.stage.actors[i]
					if (this.stage.isCollide(weapon,this,weapon.radius/2)) {
						this.weapon = weapon.name;
						this.stage.removeActor(weapon);
					}
			}
		}
		// bounce off the walls
		if(this.position.x<this.stage.worldx){
			this.position.x=this.stage.worldx;
			this.velocity.x=0;
		}
		if(this.position.x>this.stage.worldwidth+this.stage.worldx-this.radius){
			this.position.x=this.stage.worldwidth+this.stage.worldx-this.radius;
			this.velocity.x=0;
		}
		if(this.position.y<this.stage.worldy){
			this.position.y=this.stage.worldy;
			this.velocity.y=0;
		}
		if(this.position.y>this.stage.worldheight+this.stage.worldx-this.radius){
			this.position.y=this.stage.worldheight+this.stage.worldx-this.radius;
			this.velocity.y=0;
		}
		this.intPosition();
		
	}
	isCollide(actor){
		var dist = Math.hypot(actor.position.x-this.center.x,
			actor.position.y-this.center.y);
		if ((dist - actor.radius - this.radius/2) < 0){
			return true;
		}
		return false;
	}
	inBoundary(){
		if(this.position.x<this.stage.worldx+this.radius || this.position.x>=this.stage.worldwidth+this.stage.worldx-this.radius || 
			this.position.y<this.stage.worldy+this.radius ||this.position.y>=this.stage.worldheight+this.stage.worldx-this.radius){
			return false;
		}else{
			for(var i=0;i<this.stage.actors.length;i++){
				if(this.stage.actors[i].constructor.name == "Obstacle" && this.stage.isCollide(this,this.stage.actors[i],-this.radius)){
					return false;
				}
			}
		}
		return true;
	}
	shoot(headtoX,headtoY){
		var radius = this.radius/2;
		var x = headtoX-(Math.floor(this.stage.width/2) - this.position.x) ;
		var y = headtoY-(Math.floor(this.stage.height/2) - this.position.y);
		if (this.weapon == "handgun") {
			let velocity = new Pair(10,10);
			let spawn_position = new Pair(this.position.x +radius,this.position.y+radius);
			let headto_position = new Pair(x,y);
			let bullet = new Bullet(this.stage,spawn_position,velocity,'red',5,0,this,10);
			bullet.headTo(headto_position);
			this.stage.addActor(bullet);
			
		}
		if (this.weapon == "shotgun") {
			var headx = x;
			var heady = y;
			for (let i = 0; i < 5; i++) {
				let velocity = new Pair(10,10);
				let spawn_position = new Pair(this.position.x +radius,this.position.y+radius);

				if ((this.degree > 0 && this.degree<90) || (this.degree > -180 && this.degree <-90))  {
					var tempx = x-20;
					var tempy = y+20;
					headx = tempx+10*i;
					heady = tempy-10*i;
				}else{
					var tempx = x-20;
					var tempy = y-20;
					headx =tempx+10*i;
					heady =tempy+10*i;
				}
				let headto_position = new Pair(headx,heady);
				let bullet = new Bullet(this.stage,spawn_position,velocity,'red',5,0,this,10);
				bullet.headTo(headto_position);
				this.stage.addActor(bullet);		
			}
		}
	}
}
class Obstacle {
	constructor(stage, position, colour, radius){
		this.stage = stage;
		this.position=position;
		this.colour = colour;
		this.radius = radius;
		this.healthpoint = 3;
	}
	step(){
		if (this.healthpoint == 0) {
			this.stage.removeActor(this);
		}
	}
}
class Bullet extends Enemy{
	constructor(stage, position, velocity, colour, radius,degree,owner,rof){
		super(stage, position, velocity, colour, radius,degree);
		this.owner = owner;
		this.rof=rof;
		this.travaled_distance = 0;
	}
	headTo(position){
		this.velocity.x=(position.x-this.position.x);
		this.velocity.y=(position.y-this.position.y);
		this.velocity.normalize();
		this.velocity.x = this.velocity.x * this.rof;
		this.velocity.y = this.velocity.y * this.rof;
	}
	
	step(){
		this.position.x=this.position.x+this.velocity.x;
		this.position.y=this.position.y+this.velocity.y;

		this.travaled_distance = this.travaled_distance+ Math.abs(this.velocity.x)+Math.abs(this.velocity.y);

		if (this.travaled_distance >300) {
			this.stage.removeActor(this);
		}
			
		// remove bullet while hitting the wall
		if(this.position.x<this.stage.worldx+this.radius || this.position.x>this.stage.worldwidth+this.stage.worldx-this.radius || 
			this.position.y<this.stage.worldy+this.radius ||this.position.y>this.stage.worldheight+this.stage.worldx-this.radius){
			this.stage.removeActor(this);
		}

		//when bullet hit something
		for(var i=0;i<this.stage.actors.length;i++){
			switch (this.stage.actors[i].constructor.name) {
				case "Obstacle":
					if (this.stage.isCollide(this,this.stage.actors[i],0)) {
						this.stage.removeActor(this);
						if (this.owner != "enemy") {
							this.stage.actors[i].healthpoint -= 1;
							if (this.stage.actors[i].healthpoint == 0) {
								this.stage.removeActor(this.stage.actors[i]);
							}
						}
					}
					break;
				case "Player":
					var player = this.stage.actors[i];
					if (this.stage.isCollide(player,this,player.radius/2) && (this.owner == "enemy" || this.owner.id != player.id)) {
						this.stage.removeActor(this);
						player.healthpoint -= 1;
						if (player.healthpoint == 0) {
							this.stage.removePlayer(player);
							if (this.owner != "enemy") {
								this.owner.score += 50;
							}
						}
					}
					break;
				case "Enemy":
					var enemy = this.stage.actors[i];
					if (this.stage.isCollide(enemy,this,0) && this.owner != "enemy") {
						this.stage.removeActor(this);
						enemy.healthpoint -= 1;
						if (enemy.healthpoint == 0) {
							this.stage.removeActor(enemy);
							this.owner.score += 20;
							this.stage.current_num_of_enemy -=1;
						}
					}
			}
		}
		this.intPosition();
	}
}
class Ammo extends Obstacle{
	constructor(stage, position, colour, radius,quantity){
		super(stage, position, colour, radius)
		this.quantity=quantity;
	}
}

class Weapon extends Obstacle{
	constructor(stage, position, colour, radius,name){
		super(stage, position, colour, radius)
		this.name=name;
	}
}
module.exports = Stage;