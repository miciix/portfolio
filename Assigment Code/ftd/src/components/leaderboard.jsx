import React from 'react';
import { Nav } from './nav';
import { Login } from './login';
import { Profile } from './profile';
import { Game } from './game';
import $ from 'jquery';
class Leaderboard extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: this.props.username,
            password: this.props.password,
            page: '',
            boardlist: []
        };
        this.logout = this.logout.bind(this);
        this.profile = this.profile.bind(this);
        this.game = this.game.bind(this);
        this.retrieveLB = this.retrieveLB.bind(this);
    }
    logout() {
        this.setState({
            page: 'logout'
        })
    }
    game() {
        this.setState({
            page: 'game'
        })
    }
    profile() {
        this.setState({
            page: 'profile'
        })
    }
    componentDidMount() {
        this.retrieveLB('easy')
    }
    retrieveLB(mode) {
        var credentials = {
            "username": this.state.username,
            "password": this.state.password,
        };
        $.ajax({
            method: "GET",
            url: "/api/auth/leaderboard/" + mode,
            headers: { "Authorization": "Basic " + btoa(credentials.username + ":" + credentials.password) },
            processData: false,
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }).done(function (data, text_status, jqXHR) {
            console.log(jqXHR.status + " " + text_status);
            var lb = "";
            if (mode === "easy") {
                for (let i = 0; i < data.length; i++) {
                    //lb += "<br/>" + data[i].username + "  " + data[i].easy;

                    this.setState((prevState, props) => {
                        var username = data[i].username;
                        var score = data[i].easy;
                        prevState.boardlist.push({'username': username, 'score':score})
                        return { boardlist:prevState.boardlist }
                    });
                }
                $("#easy_lb").html(lb);
            } else if (mode === "normal") {
                for (let i = 0; i < data.length; i++) {
                    lb += "<br/>" + data[i].username + "  " + data[i].normal;
                }
                $("#normal_lb").html(lb);
            } else {
                for (let i = 0; i < data.length; i++) {
                    lb += "<br/>" + data[i].username + "  " + data[i].hard;
                }
                $("#hard_lb").html(lb);
            }
        }.bind(this)).fail(function (err) {
            console.log("fail " + err.status + " " + JSON.stringify(err.responseJSON));
        });
    }
    render() {
        if (this.state.page === 'logout') {
            return <Login />;
        } else if (this.state.page === 'profile') {
            return <Profile username={this.state.username} password = {this.state.password}/>;
        } else if (this.state.page === 'game') {
            return <Game username={this.state.username} password = {this.state.password}/>;
        } else {
            return (
                <div >
                    <Nav logout={this.logout} game={this.game} profile={this.profile} />
                    <div id= "ui_leaderboard">
                        <div className = "item">
                                <ul>
                                { this.state.boardlist.map((i)=><li key={i.username}>{"id: "+i.username+" score: "+i.score}</li> ) }
                                </ul>
                        </div>
                    </div>
                </div>
            );
        }
    }
}
export { Leaderboard }