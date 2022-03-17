import React from 'react';
import { Nav } from './nav';
import { Login } from './login';
import { Leaderboard } from './leaderboard';
import { Game } from './game';
import $ from 'jquery';
import { Button } from '@material-ui/core';
class Profile extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: this.props.username,
            password: this.props.password,
            page: '',
            error: '',
            firstname: '',
            lastname: '',
            email: '',
            gender: '',
            ge: '',
            oldpassword: '',
            newpassword: '',
            repassword: ''
        };
        this.logout = this.logout.bind(this);
        this.game = this.game.bind(this);
        this.profile = this.profile.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleDelete = this.handleDelete.bind(this);
    }
    logout() {
        this.setState({
            page: 'logout'
        })
    }
    lb() {
        this.setState({
            page: 'lb'
        })
    }
    game() {
        this.setState({
            page: 'game'
        })
    }
    componentDidMount() {
        this.profile()
    }
    handleChange(event) {
        this.setState((prevState, props) => {
            const name = event.target.name
            return { [name]: event.target.value };
        });
    }
    handleSubmit() {
        var credentials = {
            "username": this.state.username,
            "password": this.state.password,
        };
        if (this.state.oldpassword === '') {
            this.setState({
                error: 'Please enter old password'
            })
            return;
        }
        if (this.state.newpassword !== this.state.repassword) {
            this.setState({
                error: 'Passwords Don\'t Match'
            })
            return;
        }
        if (this.state.oldpassword !== this.state.password) {
            this.setState({
                error: 'Wrong Password'
            })
            return;
        }
        var information = {
            "username": this.state.username,
            "password": this.state.newpassword,
            "repassword": this.state.repassword,
            "oldpassword": this.state.oldpassword,
            "email": this.state.email,
            "first_name": this.state.firstname,
            "last_name": this.state.lastname,
            "gender": this.state.gender,
            "experience": this.state.ge
        }
        $.ajax({
            method: "PUT",
            url: "/api/auth/profile/" + this.state.username,
            data: JSON.stringify(information),
            headers: { "Authorization": "Basic " + btoa(credentials.username + ":" + credentials.password) },
            processData: false,
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }).done(function (data, text_status, jqXHR) {
            console.log(jqXHR.status + " " + text_status + JSON.stringify(data));
            this.setState({
                error: data.message,
                username: information.username,
                password: information.password
            })
        }.bind(this)).fail(function (err) {
            console.log("fail " + err.status + " " + JSON.stringify(err.responseJSON));
            this.setState({
                error: err.responseJSON.error
            });
        }.bind(this));
    }

    profile() {
        var credentials = {
            "username": this.state.username,
            "password": this.state.password,
        };

        $.ajax({
            method: "get",
            url: "/api/auth/profile/" + credentials.username,
            headers: { "Authorization": "Basic " + btoa(credentials.username + ":" + credentials.password) },
            processData: false,
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }).done(function (data) {
            this.setState({
                firstname: data.firstname,
                lastname: data.lastname,
                ge: data.gameage,
                email: data.email,
                gender: data.gender
            });
        }.bind(this)).fail(function (err) {
            console.log("fail " + err.status + " " + JSON.stringify(err.responseJSON));
        });
    }
    handleDelete() {
        var credentials = {
            "username": this.state.username,
            "password": this.state.password,
        };
        if (this.state.oldpassword === '') {
            this.setState({
                error: 'Please enter old password'
            })
            return;
        }
        $.ajax({
            method: "DELETE",
            url: "/api/auth/profile/" + credentials.username,
            headers: { "Authorization": "Basic " + btoa(credentials.username + ":" + credentials.password) },
            processData: false,
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }).done(function (data, text_status, jqXHR) {
            console.log(jqXHR.status + " " + text_status + JSON.stringify(data));
            this.setState({
                page: 'logout'
            });
        }.bind(this)).fail(function (err) {
            console.log("fail " + err.status + " " + JSON.stringify(err.responseJSON));
            this.setState({
                error: err.responseJSON.error
            });
        }.bind(this));
    }
    render() {
        if (this.state.page === 'logout') {
            return <Login />;
        } else if (this.state.page === 'lb') {
            return <Leaderboard username={this.state.username} password={this.state.password} />;
        } else if (this.state.page === 'game') {
            return <Game username={this.state.username} password={this.state.password} />;
        } else {
            return (
                <div>
                    <Nav logout={this.logout} lb={this.lb} game={this.game} />
                    <div id="ui_profile">
                        <h1>Profile</h1>
                        <div className="error">{this.state.error}</div>
                        <form id="profile_form">
                            First Name:<br />
                            <input name="firstname" onChange={this.handleChange} placeholder="First Name " value={this.state.firstname} /><br />
                        Last Name:<br />
                            <input name="lastname" onChange={this.handleChange} placeholder="Last Name" value={this.state.lastname} /><br />
                        Email address:<br />
                            <input name="email" onChange={this.handleChange} type="email" placeholder="email@address.com" value={this.state.email} /><br />
                        Username:<br />
                            <input name="username" onChange={this.handleChange} type="text" required minLength="1" maxLength="15" value={this.state.username} /><br />
                        Old Password:<br />
                            <input name="oldpassword" onChange={this.handleChange} type="password" minLength="6" maxLength="12" /><br />
                        New Password:<br />
                            <input name="newpassword" onChange={this.handleChange} type="password" minLength="6" maxLength="12" /><br />
                        Re-enter Password:<br />
                            <input name="repassword" onChange={this.handleChange} type="password" minLength="6" maxLength="12" /><br />

                            <fieldset>
                                <legend>Gender:</legend>
                                <label><input type="radio" name="gender" onChange={this.handleChange} value="Male" checked={this.state.gender === 'Male'} /> Male </label>
                                <label><input type="radio" name="gender" onChange={this.handleChange} value="Female" checked={this.state.gender === 'Female'} /> Female </label>
                                <label><input type="radio" name="gender" onChange={this.handleChange} value="Other" checked={this.state.gender === 'Other'} /> Other </label><br />
                            </fieldset>

                            <fieldset>
                                <legend>Gaming Experience:</legend>
                                <input onChange={this.handleChange} type="number" name='ge' min="0" max="100" value={this.state.ge} /> Year(s)
                        </fieldset>
                        </form>
                        <Button id="btn" variant="contained" color="primary" onClick={this.handleSubmit}>
                            Submit
                        </Button>
                        <Button id="btn" variant="contained" color="secondary" onClick={this.handleDelete}>
                            Delete Account
                        </Button>
                    </div>
                </div>
            );
        }
    }
}
export { Profile }