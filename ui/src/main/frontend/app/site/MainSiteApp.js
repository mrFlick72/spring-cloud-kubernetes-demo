import React from "react"
import Jumbotron from "../component/Jumbotron";
import MessageRepository from "../repository/MessageRepository";
import TextInputForm from "../component/TextInputForm";
import NavBar from "../component/NavBar";
import Container from "../component/Container";

export default class MainSiteApp extends React.Component {

    constructor(props) {
        super(props)

        this.state = {
            message: "No message right now"
        };

        this.inputRef = React.createRef();
        this.messageRepository = new MessageRepository();
        this.sayHello = this.sayHello.bind(this);
    }

    sayHello() {
        this.messageRepository
            .sayHelloTo(this.inputRef.current.value)
            .then(message => this.setState({message: message}))
    }

    render() {
        let leadSection = <form>
            <TextInputForm inputRef={this.inputRef}
                           componentId="name"
                           componentLabel="Say Hello to "
                           componentPlaceholder="Say Hello to "/>
            <button type="button" onClick={this.sayHello} className="btn btn-primary">Submit</button>
        </form>
        let bottomSection = <p>{this.state.message}</p>

        return <React.Fragment>
            <NavBar title="Message Managment"/>
            <Container>
                <Jumbotron title="Hello, world!"
                           inputRef={this.inputRef}
                           leadSection={leadSection}
                           bottomSection={bottomSection}
                           message={this.state.message}/>
            </Container>
        </React.Fragment>
    }
}