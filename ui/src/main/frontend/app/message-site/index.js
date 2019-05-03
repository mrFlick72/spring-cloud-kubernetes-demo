import React from 'react';
import ReactDOM from 'react-dom';
import MessageSiteApp from "./MessageSiteApp";

if(document.getElementById('app')){
    ReactDOM.render(<MessageSiteApp />, document.getElementById('app'));
}