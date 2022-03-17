import React from 'react';
import { Button } from '@material-ui/core';
import Box from '@material-ui/core/Box';
import $ from 'jquery';
import { Register } from './register';
import { Game } from './game';

class Login extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: '',
            error: '',
            page: ''
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleRegister = this.handleRegister.bind(this);
        this.login = this.login.bind(this);
    }
    handleRegister() {
        this.setState({ page: 'register' });
    }

    handleChange(event) {
        this.setState((prevState, props) => {
            const name = event.target.name
            return { [name]: event.target.value };
        });
    }

    handleSubmit(event) {
        this.login();
        event.preventDefault();
    }
    login() {
        var credentials = {
            "username": this.state.username,
            "password": this.state.password
        };

        $.ajax({
            method: "POST",
            url: "/api/auth/login",
            data: JSON.stringify({}),
            headers: { "Authorization": "Basic " + btoa(credentials.username + ":" + credentials.password) },
            processData: false,
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }).done(function (data, text_status, jqXHR) {
            console.log(jqXHR.status + " " + text_status + JSON.stringify(data));
            this.setState({ page: 'game' });
        }.bind(this)).fail(function (err) {
            console.log("fail " + err.status + " " + JSON.stringify(err.responseJSON));
            this.setState({ error: 'password or username not correct' });
        }.bind(this));
    }
    render() {
        if (this.state.page === 'register') {
            return <Register/>;
        } else if (this.state.page === 'game') {
            return <Game username={this.state.username} password = {this.state.password}/>;
        } else {
            return (
                <div id="ui_login">
                    <h1>Welcome to f0rt9it32d</h1>
                    <input type="text" name="username" className="inputbox" placeholder="User Name" value={this.state.username} onChange={this.handleChange} /> <br />
                    <input type="password" name="password" className="inputbox" placeholder="Password" onChange={this.handleChange} /><br />
                    <Box >
                        <Button id="btn" variant="contained" color="primary" onClick={this.handleSubmit}>
                            Login
                        </Button>
                        <Button onClick={this.handleRegister} >Register</Button>
                    </Box>
                    <div className="error">{this.state.error}</div>
                </div>
            );
        }
    }
}
export { Login };