import React from 'react';

class Nav extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: '',
            error:'',
            page:''
        };
    }
    render(){
        return(  
            <nav id= "nav">
                <ul>
                <li id = "lb" onClick = {this.props.lb}> Leader Board </li>
                <li id = "game" onClick = {this.props.game}> Game</li>
                <li id = "profile" onClick = {this.props.profile}> Profile</li>
                <li onClick = {this.props.logout}>  Logout</li> 
                </ul>
            </nav>
        )
    }
}
export {Nav};