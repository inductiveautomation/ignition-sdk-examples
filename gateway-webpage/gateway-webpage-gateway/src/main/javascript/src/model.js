import {combineReducers} from 'redux';
import {checkStatus} from 'ignition-lib';

const CONNECTIONS_LOAD = 'hce/CONNECTIONS_LOAD';
const CONNECTIONS_ERR = 'hce/CONNECTIONS_ERR';
const CONNECTIONS_DETAIL_LOAD = 'hce/CONNECTIONS_DETAIL_LOAD';
const CONNECTIONS_DETAIL_ERR = 'hce/CONNECTIONS_DETAIL_ERR';

const VIEW_ALL = "hce/VIEW_ALL";
const VIEW_CONNECTION = "hce/VIEW_CONNECTION";
const FETCH_PERMISSIONS = "hce/FETCH_PERMISSIONS";

function getConnections(state = null, action) {
    if (action.type === CONNECTIONS_LOAD) {
        return action.connections;
    } else if (action.type === CONNECTIONS_ERR) {
        return null;
    }
    return state;
}

function getConnectionsError(state = null, action) {
    if (action.type === CONNECTIONS_ERR) {
        return action.reason;
    } else if (action.type === CONNECTIONS_LOAD) {
        return null;
    }
    return state;
}

function getDetails(state = null, action) {
    if (action.type === CONNECTIONS_DETAIL_LOAD) {
        return action.connection;
    } else if (action.type === CONNECTIONS_DETAIL_ERR) {
        return null;
    }
    return state;
}

function getDetailsError(state = null, action) {
    if (action.type === CONNECTIONS_DETAIL_ERR) {
        return action.reason;
    } else if (action.type === CONNECTIONS_DETAIL_LOAD) {
        return null;
    }
    return state;
}

function connectionName(state = null, action) {
    if (action.type === VIEW_CONNECTION) {
        return action.connection;
    } else if (action.type === VIEW_ALL) {
        return null;
    }
    return state;
}


function permissions(state = {'config': false}, action) {
    if (action.type === FETCH_PERMISSIONS) {
        return action.permissions;
    }
    return state;
}

const reducer = combineReducers({
    getConnections,
    getConnectionsError,
    connectionName,
    permissions,
    getDetails,
    getDetailsError,
});
export default reducer;

/*
 ACTION CREATORS
 */
export function getConnectionsStatus(startNextPoll) {
    return function (dispatch) {
        fetch(`/data/hce/status/connections`, {
            method: 'get',
            credentials: 'same-origin',
            headers: {
                'Accept': 'application/json'
            }
        })
            .then(checkStatus)
            .then(response => response.json())
            .then(json => {
                if(startNextPoll()){
                    dispatch({type: CONNECTIONS_LOAD, connections: json})
                }

            })
            .catch(reason => {
                startNextPoll();
                dispatch({type: CONNECTIONS_ERR, reason: reason.toString()});
            });
    }
}

// Not used in this example. Shown here as an example of how to pass parameters to status routes
export function getConnectionDetail(connectionName, startNextPoll) {
    const encodedConnectionName = encodeURIComponent(encodeURIComponent(connectionName));   // Needs to be double. Do this for anything that could be user generated
    return function (dispatch) {
        fetch(`/main/data/hce/status/connections/${encodedConnectionName}`, {
            method: 'get',
            credentials: 'same-origin',
            headers: {
                'Accept': 'application/json'
            }
        })
            .then(checkStatus)
            .then(response => response.json())
            .then(json => {
                if(startNextPoll()){
                    dispatch({type: CONNECTIONS_DETAIL_LOAD, detail: json});
                }
            })
            .catch(reason => {
                startNextPoll();
                dispatch({type: CONNECTIONS_DETAIL_ERR, reason: reason.toString()});
            });
    }
}

// Not used in this example status page, but can be used to only allow users with config permission to make changes from the status page
export function fetchPermissions() {
    return function (dispatch) {
        fetch('/main/data/status/permissions', {
            method: 'get',
            credentials: 'same-origin',
            headers: {
                'Accept': 'application/json'
            }
        })
            .then(checkStatus)
            .then(response => response.json())
            .then(json => {
                dispatch({
                    type: FETCH_PERMISSIONS,
                    permissions: json
                });
            })
    }
}
