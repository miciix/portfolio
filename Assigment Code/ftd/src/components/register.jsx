import React from 'react';
import { Button } from '@material-ui/core';
import $ from 'jquery';
import { Login } from './login';

class Register extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: '',
            error: '',
            page: '',
            firstname: '',
            lastname: '',
            email: '',
            gender: '',
            ge: '',
            newpassword: '',
            repassword: ''
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleBack = this.handleBack.bind(this);
    }
    handleBack() { this.setState({ page: 'login' }); }
    handleChange(event) {
        this.setState((prevState, props) => {
            const name = event.target.name
            return { [name]: event.target.value };
        });
    }
    handleSubmit(event) {
        this.register();
        event.preventDefault();
    }
    register() {
        if (this.state.newpassword !== this.state.repassword) {
            this.setState({
                error: 'Passwords Don\'t Match'
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
            method: "POST",
            url: "/api/register",
            data: JSON.stringify(information),
            contentType: "application/json; charset=utf-8",
            processData: false,
            dataType: "json"
        }).done(function (data, text_status, jqXHR) {
            console.log(jqXHR.status + " " + text_status + JSON.stringify(data));
            this.setState({
                error: 'Registration sucess'
            });
        }.bind(this)).fail(function (err) {
            console.log("fail " + err.status + " " + JSON.stringify(err.responseJSON));
            this.setState({
                error: err.responseJSON.error
            });
        }.bind(this));
    }
    render() {
        if (this.state.page === 'login') {
            return <Login />;
        } else {
            return (
                <div id="ui_register">
                    <form>
                        <h1>Sign up</h1>
                        <div className="error">{this.state.error}</div>
                    First Name:<br />
                        <input name="firstname" onChange={this.handleChange}  placeholder="First Name " /><br />
                    Last Name:<br />
                        <input name="lastname" onChange={this.handleChange}  placeholder="Last Name" /><br />
                    Email address:<br />
                        <input name="email" onChange={this.handleChange}  type="email" placeholder="email@address.com" /><br />
                    Username:<br />
                        <input name="username" onChange={this.handleChange} type="text" required minLength="1" maxLength="15" /><span className="star">*</span><br />
                    Password:<br />
                        <input name="newpassword" onChange={this.handleChange} type="password" required minLength="6" maxLength="12" /><span className="star">*</span><br />
                    Re-enter Password:<br />
                        <input name="repassword" onChange={this.handleChange} type="password" required minLength="6" maxLength="12" /><span className="star">*</span><br />

                        <fieldset >
                            <legend>Gender:</legend>
                            <label><input type="radio" name="gender" value="Male" onChange={this.handleChange}/> Male </label>
                            <label><input type="radio" name="gender" value="Female" onChange={this.handleChange}/> Female </label>
                            <label><input type="radio" name="gender" value="Other" onChange={this.handleChange}/> Other </label><br />
                        </fieldset>

                        <fieldset >
                            <legend>Gaming Experience:</legend>
                            <input type="number" name="ge" min="0" max="100" onChange={this.handleChange}/> Year(s)
                    </fieldset>
                    </form>
                    <br />

                    <Button id="btn" variant="contained" color="primary" onClick={this.handleSubmit}>
                        Create an Account
                    </Button>
                    <Button onClick={this.handleBack} variant="contained" color="secondary" >Back to Login</Button>
                </div>
            );

        }
    }
}
export { Register };