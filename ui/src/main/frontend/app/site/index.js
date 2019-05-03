import React from 'react';
import ReactDOM from 'react-dom';
import MainSiteApp from "./MainSiteApp";

if(document.getElementById('app')){
    ReactDOM.render(<MainSiteApp />, document.getElementById('app'));
}