/**
 * Created by kapplebaum on 9/22/16.
 */
import React, {Component} from 'react';
import {connect} from 'react-redux';
import {pollWaitAck} from 'ignition-lib';
import {getConnectionsStatus} from './model';
import {BlankState, Gauge, ItemTable, Loading} from 'ignition-react';

const BLANK_STATE = {
    image: <img src="/main/res/alarm-notification/img/blank_alarms.png" alt=""/>,
    heading: 'There are no connections defined.',
    body: 'Home connections allow you to control your home through Ignition.',

    links: [<a className="primary button"
               target="_blank"
               href="https://google.com">Learn More</a>
    ]
};


class ConnectOverview extends Component {
    constructor(props) {
        super(props);
    }

    componentWillMount() {
        const {dispatch} = this.props;
        // // refresh the connection status every 5 seconds, but don't start a new request until the last one has returned
        this.cancelPoll = pollWaitAck(dispatch, getConnectionsStatus, 5000);
    }

    componentWillUnmount() {
        if (this.cancelPoll) {
            this.cancelPoll();
        }
    }

    render() {
        const {connections, connectionsError} = this.props;
        console.log("connections", connections);
        if (connections != null){
            const HEADERS = [
                { header: 'Hub Name', weight: 2 },
                { header: 'Broadcast SSID?', weight: 1 },
                { header: 'Device Count', weight: 1 },
                { header: 'IP Address', weight: 2 },
                { header: 'Allow Interop?', weight: 1 },
                { header: 'Power Output', weight: 2 },
            ];
            const connectionCount = connections.count;

            if (connectionCount > 0){
                const connectionList = connections.connections;
                let items = [];
                if (connectionList != null){
                    items = connectionList.map((connection) => {
                        return [
                            connection.HomeConnectHubName,
                            connection.BroadcastSSID.toString(),
                            connection.DeviceCount,
                            connection.IPAddress,
                            connection.AllowInterop.toString(),
                            connection.PowerOutput,
                        ];
                    });
                }

                return (<div>
                    <div className="row">
                        <div className="small-12 columns">
                            <div className="page-heading">
                                <div className="quick-links">
                                    <a href="/web/config/hce.hub">Configure</a>
                                </div>
                                <h6>Systems</h6>
                                <h1>Performance</h1>
                            </div>
                        </div>
                    </div>
                    <div className="row">
                        <div className="small-12 medium-5 large-3 columns">
                            <Gauge label="Connections" value={connectionCount}/>
                        </div>
                    </div>
                    <div className="row">
                        <div className="small-12 columns">
                            <ItemTable headers={ HEADERS } items={ items } errorMessage={connectionsError}/>
                        </div>
                    </div>
                </div>);
            } else {
                return (<div><BlankState { ...BLANK_STATE } /></div>);
            }

        }else {
            return (<div><Loading /></div>);
        }
    }
}

function selector(state) {
    return {
        connections: state.getConnections,
        connectionsError: state.getConnectionsError,
    }
}

export default connect(selector)(ConnectOverview);
