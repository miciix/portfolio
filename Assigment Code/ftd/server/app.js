const Stage = require('./node')
var port = 8000;
var webSocketPort = port + 1;
var express = require('express');
const WebSocket = require('ws');
const wss = new WebSocket.Server({ port: webSocketPort });
var app = express();

var interval = null;
var interval2 = null;

var stage = new Stage('easy');
function rep(key, value) {
	if (key == 'stage') {
		return undefined;
	}
	return value;
}
var stageState = null;
var player = null;
var clients = {};

function setStageState() {

	var players = [];
	var obstacles = [];
	var enemies = [];
	var ammos = [];
	var weapons = [];
	var bullets = [];

	for (i = 0; i < stage.actors.length; i++) {
		switch (stage.actors[i].constructor.name) {
			case "Player":
				players.push(stage.actors[i]);
				break;
			case "Obstacle":
				obstacles.push(stage.actors[i]);
				break;
			case "Enemy":
				enemies.push(stage.actors[i]);
				break;
			case "Ammo":
				ammos.push(stage.actors[i]);
				break;
			case "Weapon":
				weapons.push(stage.actors[i]);
				break;
			case "Bullet":
				bullets.push(stage.actors[i]);
				break;
		}
	}
	stageState = {
		'worldwidth': stage.worldwidth,
		'worldheight': stage.worldheight,
		'worldx': stage.worldx,
		'worldy': stage.worldy,
		'player': players,
		'obstacle': obstacles,
		'enemy': enemies,
		'ammo': ammos,
		'weapon': weapons,
		'bullet': bullets
	};
}

function getPlayer(playerid) {
	for (i = 0; i < stage.actors.length; i++) {
		if (stage.actors[i].constructor.name == 'Player') {
			if (stage.actors[i].id == playerid) {
				return stage.actors[i];
			}
		}
	}
}
function mouseMove(x, y) {
	var dx = x - Math.floor(stage.width / 2) - player.radius / 2;
	var dy = y - Math.floor(stage.height / 2) - player.radius / 2;
	var degree = Math.atan2(dy, dx)
	degree *= 180 / Math.PI;
	player.degree = degree;
}
function mouseClick(x, y) {
	if (player.ammo > 0) {
		player.shoot(x, y);
		player.ammo -= 1;
	}
}
function addPlayer(ws) {
	var playerid = stage.addPlayer();
	console.log('player' + playerid + ' added');
	ws.send(JSON.stringify({
		'playerid': playerid
	}));
	clients[playerid] = ws;
}



wss.on('close', function () {
	console.log('disconnected');
});

wss.broadcast = function (message) {
	for (let ws of this.clients) {
		ws.send(message);
	}
}

wss.on('connection', function (ws) {

	ws.on('message', function (message) {
		//hundle player actions
		var obj = null;
		var playerid = null;
		if(message) {
			try {
				obj = JSON.parse(message);
			} catch(e) {
				console.log('Error when parse message. Probabaly bad message')
				return;
			}
		}else{
			return;
		}
		
		if (obj.hasOwnProperty('playerid')) {
			playerid = obj.playerid;
			if (obj.hasOwnProperty('request') && obj.request == 'newgame') {
				addPlayer(ws);
			}
			player = getPlayer(playerid);
		}
		if (obj.hasOwnProperty('mouseMoveX') && player) {
			mouseMove(obj.mouseMoveX, obj.mouseMoveY);
		}
		if (obj.hasOwnProperty('keyPress') && player) {
			player.velocity = obj.keyPress;
		}
		if (obj.hasOwnProperty('keyUp') && player) {
			if (player.inBoundary()) {
				player.velocity.x -= obj.keyUp.x;
				player.velocity.y -= obj.keyUp.y;
			} else {
				player.velocity.x = 0;
				player.velocity.y = 0;
			}
		}
		if (obj.hasOwnProperty('touch') && player) {
			player.velocity.x = 0;
			player.velocity.y = 0;
		}

		if (obj.hasOwnProperty('mouseClickX') && player) {
			mouseClick(obj.mouseClickX, obj.mouseClickY);
		}
	});

	ws.on('close', function () {
		var i = 0;
		for (key in clients) {
			if (clients[key] == ws) {
				console.log('player' + key + ' disconnect');
				stage.removePlayer(getPlayer(key));
			}
			i += 1;
		}
	});

});

interval = setInterval(function () {
	setStageState();

	//step the world before broadcast to all player
	stage.step();
	wss.broadcast(JSON.stringify(stageState, rep));
}, 10);

interval2 = setInterval(function () {
	stage.shoot();
}, 2000);

const { Pool } = require('pg')
const pool = new Pool({
	user: 'webdbuser',
	host: 'localhost',
	database: 'webdb',
	password: 'password',
	port: 5432
});

const bodyParser = require('body-parser'); // we used this middleware to parse POST bodies

function isObject(o) { return typeof o === 'object' && o !== null; }
function isNaturalNumber(value) { return /^\d+$/.test(value); }
function requestisValid(req, res) {

	var args = ['username', 'password', 'repassword', 'first_name', 'last_name',
		'email', 'gender', 'experience'];

	for (let i = 0; i < args.length; i++) {
		if (!(args[i] in req.body)) {
			res.status(400);
			res.json({ "error": 'Invalid Request' });
			return;
		}
	}
	//verify password match
	if (req.body.password != req.body.repassword) {
		res.status(400);
		res.json({ "error": 'Passwords Don\'t Match' });
		return;
	}
	return true;
}
// app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
// app.use(bodyParser.raw()); // support raw bodies

// Non authenticated route. Can visit this without credentials
app.post('/api/test', function (req, res) {
	res.status(200);
	res.json({ "message": "got here" });
});

/** 
 * This is middleware to restrict access to subroutes of /api/auth/ 
 * To get past this middleware, all requests should be sent with appropriate
 * credentials. Now this is not secure, but this is a first step.
 *
 * Authorization: Basic YXJub2xkOnNwaWRlcm1hbg==
 * Authorization: Basic " + btoa("arnold:spiderman"); in javascript
**/
app.use('/api/auth', function (req, res, next) {
	if (!req.headers.authorization) {
		return res.status(403).json({ error: 'No credentials sent!' });
	}
	try {
		// var credentialsString = Buffer.from(req.headers.authorization.split(" ")[1], 'base64').toString();
		var m = /^Basic\s+(.*)$/.exec(req.headers.authorization);

		var user_pass = Buffer.from(m[1], 'base64').toString()
		m = /^(.*):(.*)$/.exec(user_pass); // probably should do better than this

		var username = m[1];
		var password = m[2];

		let sql = 'SELECT * FROM ftduser WHERE username=$1 and password=sha512($2);';
		pool.query(sql, [username, password], (err, pgRes) => {
			if (err) {
				res.status(403).json({ error: 'Not authorized' });
			} else if (pgRes.rowCount == 1) {
				next();
			} else {
				res.status(403).json({ error: 'Not authorized' });
			}
		});
	} catch (err) {
		res.status(403).json({ error: 'Not authorized' });
	}
});

// All routes below /api/auth require credentials 
app.post('/api/auth/login', function (req, res) {
	res.status(200);
	res.json({ "message": "authentication success" });
});
app.get('/api/auth/leaderboard/:mode/', function (req, res) {
	var mode = req.params.mode;
	if (mode == "easy") {
		sql = 'SELECT username,easy from score order by easy desc limit 10;';
	} else if (mode == "normal") {
		sql = 'SELECT username,normal from score order by normal desc limit 10;';
	} else {
		sql = 'SELECT username,hard from score order by hard desc limit 10;';
	}
	pool.query(sql, [], (err, pgRes) => {
		if (err) {
			res.status(500);
			res.json({ "error": err.message });
			return;
		} else if (pgRes.rowCount > 0) {

			res.status(200);
			res.json(pgRes.rows);
			return;
		} else {
			res.status(404);
			res.json({ "error": `No such user` });
			return;
		}
	});
});
//fetch userprofile 
app.get('/api/auth/profile/:username/', function (req, res) {
	var username = req.params.username

	let sql = 'SELECT * FROM ftduser WHERE username=$1;';

	pool.query(sql, [username], (err, pgRes) => {

		if (err) {
			res.status(500);
			res.json({ "error": err.message });
			return;
		} else if (pgRes.rows.length == 1) {
			res.status(200);
			res.json(pgRes.rows[0]);
			return;
		} else {
			res.status(404);
			res.json({ "error": `No such user` });
			return;
		}
	});
});

//update user profile
app.put('/api/auth/profile/:username/', function (req, res) {
	//verify request has needed information
	if (requestisValid(req, res) != true) {
		return;
	}
	var username = req.params.username
	var info = [];
	for (let [key, value] of Object.entries(req.body)) {

		if (key == 'repassword') continue;
		if (key == 'oldpassword') continue;
		if (key == 'password' && value == '') {
			info.push(req.body.oldpassword)

			continue;
		}
		if (key == 'experience' && value == '') {
			info.push(0);
			continue;
		}
		info.push(value);

	}
	info.push(username);

	let sql = 'UPDATE ftduser\
				SET username=$1, password=sha512($2), \
					email=$3,firstname=$4,\
					lastname=$5,gender=$6,\
					gameage=$7\
				WHERE username=$8;';
	pool.query(sql, info, (err, pgRes) => {

		if (err) {
			res.status(500);
			res.json({ "error": err.message });
			return;
		} else if (pgRes.rowCount == 1) {
			res.status(200);
			res.json({ "message": "Update success" });
			return;
		} else {
			res.status(404);
			res.json({ "error": `No such user` });
			return;
		}
	});
});

//register user
app.post('/api/register', function (req, res) {
	//verify request has needed information
	if (requestisValid(req, res) != true) {
		return;
	}
	//verify request has password
	if (req.body.password == '' || req.body.repassword == '') {
		res.status(400);
		res.json({ "error": 'Please enter password' });
		return;
	}
	var info = [];
	for (let [key, value] of Object.entries(req.body)) {
		if (key == 'repassword') continue;
		if (key == 'experience' && value == '') {
			info.push(0);
			continue;
		}
		info.push(value);
	}
	let sql = 'INSERT INTO ftduser values($1,sha512($2),$3,$4,$5,$6,$7);';
	pool.query(sql, info, (err, pgRes) => {
		if (err && err.code == 23505) { // pg duplicate key error
			res.status(409);
			res.json({ "error": 'username is already in database' });
			return;
		} else if (err) {
			res.status(500);
			res.json({ "error": err.message });
			return;
		} else if (pgRes.rowCount == 1) {
			sql = 'INSERT INTO score values($1,0,0,0);';
			pool.query(sql, [req.body.username], (err, pgRes) => {
				if (pgRes.rowCount == 1) {
					res.status(200);
					res.json({ "message": "Registration success" });
					return;
				} else {
					res.status(500);
					res.json({ "error": `couldn't register at this time` });
					return;
				}
			});
		} else {
			res.status(500);
			res.json({ "error": `couldn't register at this time` });
			return;
		}
	});

});
app.delete('/api/auth/profile/:username/', function (req, res) {
	var username = req.params.username
	let sql = 'DELETE FROM score WHERE username=$1;';

	pool.query(sql, [username], (err, pgRes) => {
		if (err) {
			res.status(500);
			res.json({ "error": err.message });
			return;
		} else if (pgRes.rowCount == 1) {
			let sql = 'DELETE FROM ftduser WHERE username=$1;';
			pool.query(sql, [username], (err, pgRes) => {
				if (err) {
					res.status(500);
					res.json({ "error": err.message });
				} else {
					res.status(200);
					res.json({ "message": "Your account has been successfully deleted" });
					return;
				}
			});
		} else {
			res.status(404);
			res.json({ "error": "No such user" });
			return;
		}
	});

});

app.put('/api/auth/score/:username/:score/:mode/', function (req, res) {
	var username = req.params.username;
	var score = req.params.score;
	var mode = req.params.mode;

	if (score == 0) {
		res.status(200);
		res.json({ "message": "score updated" });
		return;
	}
	var sql = "";
	if (mode == "easy") {
		sql = 'SELECT easy from score where username =$1;';
	} else if (mode == "normal") {
		sql = 'SELECT normal from score where username =$1;';
	} else {
		sql = 'SELECT hard from score where username =$1;';
	}

	pool.query(sql, [username], (err, pgRes) => {
		if (err) {
			res.status(500);
			res.json({ "error": err.message });
			return;
		} else if (pgRes.rowCount == 1) {
			if (score > Object.values(pgRes.rows[0])[0]) {
				let sql = "";
				if (mode == "easy") {
					sql = 'UPDATE score SET easy=$1 WHERE username=$2;';
				} else if (mode == "normal") {
					sql = 'UPDATE score SET normal=$1 WHERE username=$2;';
				} else {
					sql = 'UPDATE score SET hard=$1 WHERE username=$2;';
				}
				pool.query(sql, [score, username], (err, pgRes) => {
					if (err) {
						res.status(500);
						res.json({ "error": "some thing wrong with database" })
					} else {
						res.status(200);
						res.json({ "message": "update score successful" });
						return
					}
				});
			}
			return;
		} else {
			res.status(500);
			res.json({ "error": "some thing wrong with database" });
			return;
		}
	});

});

app.use('/', express.static('static_files'));

app.listen(port, function () {
	console.log('Server listening on port ' + port);
});
