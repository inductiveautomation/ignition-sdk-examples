import React from 'react';
import { createStore, applyMiddleware } from 'redux';
import { Provider } from 'react-redux';
import thunkMiddleware from 'redux-thunk';

import reducer from './model.js';
import ConnectStatus from './ConnectStatus.jsx';

const createStoreWithMiddleware = applyMiddleware(thunkMiddleware)(createStore);
const store = createStoreWithMiddleware(reducer);

const MountableApp = React.createClass({
    render: function() {
        return <Provider store={store}><ConnectStatus dispatch={store.dispatch}/></Provider>
    }
});

export default MountableApp;