import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import registerServiceWorker from './registerServiceWorker';
import 'semantic-ui-css/semantic.min.css';

document.body.style.backgroundColor = '#eed';
document.body.style.height = '101%';

//render app
ReactDOM.render(<App />, document.getElementById('root'));
registerServiceWorker();