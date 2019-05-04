const SAY_HELLO_TO = (name) => `/ui/hello-service/hello/${name}`;

export default class MessageRepository {

    sayHelloTo(name) {
        return fetch(SAY_HELLO_TO(name)).then(data => data.json());
    }

}