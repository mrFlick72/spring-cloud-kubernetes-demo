import React from "react"
import Jumbotron from "../component/Jumbotron";
import MessageRepository from "../repository/MessageRepository";

export default class MainSiteApp extends React.Component {

    constructor(props) {
        super(props)

        this.state = {
            message: ""
        }

        this.inputRef = React.createRef()
        this.messageRepository = new MessageRepository();
    }

    sayHello() {
        console.log(this.inputRef)
        console.log(this.inputRef.current)
        this.messageRepository
            .sayHelloTo(this.inputRef.current)
            .then(message => this.setState({message: message}))
    }

    render() {
        return <Jumbotron title="Hello, world!"
                          inputRef={this.inputRef}
                          message={this.state.message}
                          submitFn={this.sayHello.bind()}/>
    }
}