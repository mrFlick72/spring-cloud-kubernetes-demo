import React from "react"
import Jumbotron from "../component/Jumbotron";
import MessageRepository from "../repository/MessageRepository";

export default class MessageSiteApp extends React.Component {

    constructor(props) {
        super(props)

        this.state = {
            message: "initial message"
        };

        this.inputRef = React.createRef();
        this.messageRepository = new MessageRepository();
        this.sayHello = this.sayHello.bind(this);
    }

    sayHello() {
        console.log("this.inputRef")
        console.log("this.inputRef " + this.inputRef.current.value)
        this.messageRepository
            .sayHelloTo(this.inputRef.current.value)
            .then(message => this.setState({message: message}))
    }

    render() {
        console.log(this.inputRef)
        return <Jumbotron title="Hello, world!"
                          inputRef={this.inputRef}
                          message={this.state.message}
                          submitFn={this.sayHello}/>
    }
}