import React from "react"
import Jumbotron from "../component/Jumbotron";
import MessageRepository from "../repository/MessageRepository";
import TextInputForm from "../component/TextInputForm";

export default class MessageSiteApp extends React.Component {

    constructor(props) {
        super(props)

        this.state = {
            messages: []
        };

        this.inputRef = React.createRef();
        this.messageRepository = new MessageRepository();
        this.saveMessage = this.saveMessage.bind(this);
        this.displayMessages = this.displayMessages.bind(this);
    }

    saveMessage() {
        this.messageRepository
            .saveMessage({message: this.inputRef.current.value})
            .then(response => this.displayMessages())
    }

    componentDidMount() {
        this.displayMessages();
    }

    displayMessages() {
        this.messageRepository.findMessages()
            .then(data => {
                console.log(data)
                this.setState({messages: data})
            })
    }

    deleteMessage(messageId) {
        this.messageRepository.deleteMessage(messageId)
            .then(response => {
                this.displayMessages()
            })
    }

    render() {
        let leadSection = <form>
            <TextInputForm inputRef={this.inputRef}
                           componentId="name"
                           componentLabel="New Special Message"
                           componentPlaceholder="New Special Message..."/>
            <button type="button" onClick={this.saveMessage} className="btn btn-primary">Submit</button>
        </form>

        let bottomSection =
            <ul class="list-group">
                {this.state.messages.map(message => {
                    return <li className="list-group-item d-flex justify-content-between align-items-center"
                               key={message.id}>
                        {message.message}
                        <span className="badge badge-danger badge-pill"
                              onClick={this.deleteMessage.bind(this, message.id)}>
                            <i className="fas fa-trash-alt fa-lg"></i> Delete
                        </span>
                    </li>
                })}
            </ul>
        return <Jumbotron title="Special Message!"
                          inputRef={this.inputRef}
                          leadSection={leadSection}
                          bottomSection={bottomSection}/>
    }
}