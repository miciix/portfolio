import React from 'react';
import { Nav } from './nav';
import { Login } from './login';
import { Leaderboard } from './leaderboard';
import { Profile } from './profile';
import $ from 'jquery';
//const socket = new WebSocket(`ws://${window.location.hostname}:8001`);
class Pair {
    constructor(x, y) {
        this.x = x; this.y = y;
    }

    toString() {
        return "(" + this.x + "," + this.y + ")";
    }

    normalize() {
        var magnitude = Math.sqrt(this.x * this.x + this.y * this.y);
        this.x = this.x / magnitude;
        this.y = this.y / magnitude;
    }
}
var keyPressed = "";
var keypressmoveMap = {
    'a': new Pair(-2, 0),
    's': new Pair(0, 2),
    'd': new Pair(2, 0),
    'w': new Pair(0, -2),
    'aw': new Pair(-2, -2),
    'wa': new Pair(-2, -2),
    'wd': new Pair(2, -2),
    'dw': new Pair(2, -2),
    'ds': new Pair(2, 2),
    'sd': new Pair(2, 2),
    'sa': new Pair(-2, 2),
    'as': new Pair(-2, 2),
};
var worldheight = null;
var worldwidth = null;
var worldx = null;
var worldy = null;
var playerid = null;
class EndGame extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            score: 0
        };
        this.updateStat = this.updateStat.bind(this);
    }
    updateStat(score) {
        this.setState({
            score: score
        });
    }
    render() {
        return (
            <div id="endgame">
                <h1>You Died</h1>
                <h2>Score: </h2>
                <h2 id="end_score">{this.state.score}</h2>
                <button className="newgame" onClick={this.props.newgame}>start again </button>
            </div>
        );
    }
}
class PlayerStat extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            hp: null,
            ammo: null,
            score: null
        };
        this.updateStat = this.updateStat.bind(this);
    }

    updateStat(stat) {
        this.setState({
            hp: stat.hp,
            ammo: stat.ammo,
            score: stat.score
        });
    }
    render() {
        return (
            <div id="text">
                <span >Health :</span><span id="health">{this.state.hp}</span><br />
                <span >Ammo :</span> <span id="ammo">{this.state.ammo}</span><br />
                <span >Score :</span><span id="score">{this.state.score}</span><br />
            </div>
        );
    }
}


class Game extends React.Component {
    socket = new WebSocket(`ws://${window.location.hostname}:8001`);
    context = null;
    startpoint = null;
    endpoint = null;
    constructor(props) {
        super(props);
        this.canvas = React.createRef();
        this.movementContol = React.createRef();
        this.stat = React.createRef();
        this.end = React.createRef();
        this.state = {
            username: this.props.username,
            password: this.props.password,
            error: '',
            page: '',
            endgame: false
        };
        this.keyDown = this.keyDown.bind(this);
        this.keyUp = this.keyUp.bind(this);
        this.mouseDown = this.mouseDown.bind(this);
        this.mouseMove = this.mouseMove.bind(this);
        this.logout = this.logout.bind(this);
        this.profile = this.profile.bind(this);
        this.lb = this.lb.bind(this);
        this.newgame = this.newgame.bind(this);
        this.updateScore = this.updateScore.bind(this);
        this.touchMove = this.touchMove.bind(this)

    }
    componentDidMount() {
        this.context = this.canvas.current.getContext("2d");
        var movementPanel = this.movementContol.current;
        var movementCtx = this.movementContol.current.getContext("2d");
        document.addEventListener("keydown", this.keyDown);
        document.addEventListener("keyup", this.keyUp);
        movementPanel.addEventListener('touchend', function (event) { this.touchMove("touchend", event); }.bind(this));
        movementPanel.addEventListener('touchmove', function (event) { this.touchMove("touchmove",event); }.bind(this));
        movementPanel.addEventListener('touchstart', function (event) { this.touchMove("touchstart", event); }.bind(this));
        movementCtx.font = '15px Arial';
        movementCtx.fillText("Use this canvas for", 0, 20);
        movementCtx.fillText("mobile movement control", 0, 40);
        this.socket.onopen = function (event) {
            // on connecting, do nothing but log it to the console
            this.socket.send(JSON.stringify({
                'playerid': playerid,
                'request': 'newgame'
            }));
        }.bind(this);

        this.socket.onclose = function (event) {
            console.log("server is closed\n closed code:" + event.code + " reason:" + event.reason + " wasClean:" + event.wasClean);
            this.socket.send(JSON.stringify({
                'close': 'closing',
                'playerid': playerid
            }));
        }.bind(this);

        this.socket.onmessage = function (event) {
            const stage = JSON.parse(event.data)
            worldheight = stage.worldheight;
            worldwidth = stage.worldwidth;
            worldx = stage.worldx;
            worldy = stage.worldy;
            if (stage.playerid) {
                playerid = stage.playerid;
            }

            this.context = this.canvas.current.getContext("2d");
            this.context.save();
            this.context.translate(worldx-500, worldy-500)
            this.context.clearRect(0, 0, worldwidth+500, worldheight+500);
            this.context.restore();

            Object.keys(stage).forEach(function (key) {
                if (key === "obstacle") {
                    for (let i = 0; i < stage[key].length; i++) {
                        this.drawObstacle(stage[key][i]);
                    }
                }
                if (key === "ammo") {
                    for (let i = 0; i < stage[key].length; i++) {
                        this.drawAmmo(stage[key][i]);
                    }
                }
                if (key === "bullet") {
                    for (let i = 0; i < stage[key].length; i++) {
                        this.drawBullet(stage[key][i]);
                    }
                }
                if (key === "enemy") {
                    for (let i = 0; i < stage[key].length; i++) {
                        this.drawEnemy(stage[key][i]);
                    }
                }
                if (key === "weapon") {
                    for (let i = 0; i < stage[key].length; i++) {
                        this.drawWeapon(stage[key][i]);
                    }
                }
                if (key === "player") {
                    for (let i = 0; i < stage[key].length; i++) {
                        this.drawPlayer(stage[key][i]);
                        if (stage[key][i].id === playerid) {
                            //health = stage[key][i].healthpoint;
                            var stat = {
                                hp: stage[key][i].healthpoint,
                                ammo: stage[key][i].ammo,
                                score: stage[key][i].score
                            }
                            this.stat.current.updateStat(stat);
                            if (stage[key][i].healthpoint === 0) {
                                this.updateScore(stat.score, 'easy');
                                this.setState(prevState => ({ endgame: !prevState.endgame }));
                                this.end.current.updateStat(stat.score);
                                this.context = this.canvas.current.getContext("2d");
                            }
                        }
                    }
                }
            }.bind(this));
            this.drawBoundary();
        }.bind(this);

    }
    componentWillUnmount() {
        document.removeEventListener("keydown", this.keyDown);
        document.removeEventListener("keyup", this.keyUp);
        this.socket.close();
    }
    updateScore(score, mode) {
        var credentials = {
            "username": this.state.username,
            "password": this.state.password,
        };
        $.ajax({
            method: "PUT",
            url: "/api/auth/score/" + credentials.username + "/" + score + "/" + mode,
            headers: { "Authorization": "Basic " + btoa(credentials.username + ":" + credentials.password) },
            processData: false,
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }).done(function (data, text_status, jqXHR) {
            console.log(jqXHR.status + " " + text_status + JSON.stringify(data));

        }).fail(function (err) {
            console.log("fail " + err.status + " " + JSON.stringify(err.responseJSON));
        });
    }
    newgame() {
        this.socket.send(JSON.stringify({
            'playerid': playerid,
            'request': 'newgame'
        }));
        this.setState(prevState => ({ endgame: !prevState.endgame }));
        this.context = this.canvas.current.getContext("2d");
        this.context.setTransform(1, 0, 0, 1, 0, 0);
    }
    drawObstacle(actor) {
        var position = actor.position;
        var colour = actor.colour;
        var radius = actor.radius;
        this.context.beginPath();
        this.context.arc(position.x, position.y, radius, 0, 2 * Math.PI, false);
        this.context.fillStyle = colour;
        this.context.fill();
    }
    drawWeapon(actor) {
        var position = actor.position;
        var colour = actor.colour;
        var radius = actor.radius;
        this.context.fillStyle = colour;
        this.context.fillRect(position.x, position.y, radius, radius);
        this.context.fillText("Weapon", position.x, position.y);
    }
    drawEnemy(actor) {
        var position = actor.position;
        var colour = actor.colour;
        var radius = actor.radius;
        this.context.fillStyle = colour;
        this.context.beginPath();
        this.context.arc(position.x, position.y, radius, 0, 2 * Math.PI, false);
        this.context.fill();
    }
    drawAmmo(actor) {
        var position = actor.position;
        var colour = actor.colour;
        var radius = actor.radius;
        this.context.fillStyle = colour;
        this.context.fillRect(position.x, position.y, radius, radius);
        this.context.fillText("Ammo", position.x, position.y);
    }
    drawPlayer(actor) {
        var velocity = actor.velocity;
        var position = actor.position;
        var colour = actor.colour;
        var degree = actor.degree;
        var center = actor.center;
        var radius = actor.radius;

        var camx = -velocity.x;
        var camy = -velocity.y;

        //only move canvas that who control it
        if (playerid === actor.id) {
            this.context.translate(camx, camy);
        }

        //rotate via crosshair
        this.context.save();
        this.context.translate(position.x + radius / 2, position.y + radius / 2);
        this.context.rotate(degree * Math.PI / 180);
        this.context.translate(-(position.x + radius / 2), -(position.y + radius / 2));
        this.context.fillStyle = colour;
        this.context.fillRect(position.x, position.y, radius, radius);
        this.context.fillStyle = "red";
        this.context.fillRect(position.x + radius, position.y + radius / 4, radius / 2, radius / 2);
        this.context.fillStyle = "red";
        this.context.fillRect(center.x, center.y, 5, 5);
        this.context.restore();
    }
    drawBoundary() {
        this.context.moveTo(worldx, worldy);
        this.context.lineTo(worldx, worldy + worldheight);
        this.context.lineTo(worldx + worldwidth, worldy + worldheight);
        this.context.lineTo(worldx + worldwidth, worldy);
        this.context.lineTo(worldx, worldy);
        this.context.stroke();

    }
    drawBullet(actor) {
        var position = actor.position;
        var colour = actor.colour;
        var radius = actor.radius;

        this.context.beginPath();
        this.context.arc(position.x, position.y, radius, 0, 2 * Math.PI, false);
        this.context.fillStyle = colour;
        this.context.fill();
    }
    mouseMove(e) {
        this.socket.send(JSON.stringify({
            'playerid': playerid,
            'mouseMoveX': e.nativeEvent.offsetX,
            'mouseMoveY': e.nativeEvent.offsetY
        }));
    }
    mouseDown(e) {
        this.socket.send(JSON.stringify({
            'playerid': playerid,
            'mouseClickX': e.nativeEvent.offsetX,
            'mouseClickY': e.nativeEvent.offsetY
        }));
    }
    keyUp(e) {
        keyPressed = keyPressed.replace(e.key, "");
        var key = e.key;
        //avoid user press three key together
        if (keyPressed.length === 2) {
            return;
        }
        var moveMap = {
            'a': new Pair(-2, 0),
            's': new Pair(0, 2),
            'd': new Pair(2, 0),
            'w': new Pair(0, -2),
        };

        if (key in moveMap) {
            this.socket.send(JSON.stringify({
                'playerid': playerid,
                'keyUp': moveMap[key]
            }));
        }
    }
    keyDown(e) {
        if (!keyPressed.includes(e.key)) {
            keyPressed = keyPressed.concat(e.key);
        }
        //avoid user press three key together
        if (keyPressed.length > 2) {
            return;
        }

        if (keyPressed in keypressmoveMap) {
            this.socket.send(JSON.stringify({
                'playerid': playerid,
                'keyPress': keypressmoveMap[keyPressed]
            }));
        }
    }
    logout() {
        this.socket.close();
        this.setState({
            page: 'logout'
        })
    }
    lb() {
        this.socket.close();
        this.setState({
            page: 'lb'
        })
    }
    profile() {
        this.socket.close();
        this.setState({
            page: 'profile'
        })
    }
    touchMove(etype,e){
        e.preventDefault();
        var pressedkey = null
        if (etype === 'touchstart') {
            this.startpoint = new Pair(e.touches[0].pageX,e.touches[0].pageY);
        }
        if (etype === 'touchmove') {
            this.endpoint = new Pair(e.touches[0].pageX,e.touches[0].pageY);
            if(this.endpoint.x < this.startpoint.x && this.startpoint.y === this.endpoint.y){
                pressedkey= 'a';
            }else if(this.endpoint.x < this.startpoint.x && this.endpoint.y < this.startpoint.y){
                pressedkey = 'aw';
            }else if(this.endpoint.x < this.startpoint.x && this.endpoint.y > this.startpoint.y){
                pressedkey = 'as';
            }else if(this.endpoint.x === this.startpoint.x && this.endpoint.y > this.startpoint.y){
                pressedkey = 's';
            }else if(this.endpoint.x > this.startpoint.x && this.endpoint.y > this.startpoint.y){
                pressedkey = 'sd';
            }else if(this.endpoint.x > this.startpoint.x && this.endpoint.y === this.startpoint.y){
                pressedkey = 'd';
            }else if(this.endpoint.x > this.startpoint.x && this.endpoint.y < this.startpoint.y){
                pressedkey = 'wd';
            }else{
                pressedkey = 'w';
            }
            
        }
        if (etype === "touchend") {

            this.socket.send(JSON.stringify({
                'playerid': playerid,
                'touch':'stop'
            }));
            return;
        }
        if (pressedkey && !keyPressed.includes(pressedkey)) {
            keyPressed = pressedkey;
        }
        //avoid user press three key together
        if (keyPressed.length > 2) {
            return;
        }

        if (keyPressed in keypressmoveMap) {
            this.socket.send(JSON.stringify({
                'playerid': playerid,
                'keyPress': keypressmoveMap[keyPressed]
            }));
        }
    }

    render() {
        if (this.state.page === 'logout') {
            return <Login />;
        } else if (this.state.page === 'lb') {
            return <Leaderboard username={this.state.username} password={this.state.password} />;
        } else if (this.state.page === 'profile') {
            return <Profile username={this.state.username} password={this.state.password} />;
        } else {
            return (
                <div>
                    <Nav logout={this.logout} lb={this.lb} profile={this.profile} />
                    <div id="ui_play">
                        <center>
                        <canvas id = "movement" ref={this.movementContol} width="200" height="500" style={{ "border": "1px solid black" }}> </canvas>
                            <div id="stat">
                                <PlayerStat ref={this.stat} />
                                {this.state.endgame
                                    ? <EndGame newgame={this.newgame} ref={this.end} />
                                    : null}
                                <canvas id="stage"
                                    tabIndex="0"
                                    onMouseMove={this.mouseMove}
                                    onMouseDown={this.mouseDown}
                                    onTouchStart= {this.mouseDown}
                                    ref={this.canvas} width="500" height="500" style={{ "border": "1px solid black" }}> </canvas>
                                
                            </div>
                            <div></div>
                        </center>
                    </div>
                </div>
            );
        }
    }
}

export { Game };