package com.inductiveautomation.ignition.examples.scripting.client;

import com.inductiveautomation.ignition.client.gateway_interface.GatewayConnection;
import com.inductiveautomation.ignition.examples.scripting.AbstractScriptModule;
import com.inductiveautomation.ignition.examples.scripting.Constants;
import com.inductiveautomation.ignition.examples.scripting.Metadata;
import com.inductiveautomation.ignition.examples.scripting.RpcFunctions;


public class ClientScriptModule extends AbstractScriptModule {

    private static final RpcFunctions RPC = GatewayConnection.getRpcInterface(
            RpcFunctions.SERIALIZER,
            Constants.MODULE_ID,
            RpcFunctions.class
    );

    public ClientScriptModule() {}

    @Override
    protected Metadata getArchImpl() {
        return RPC.getGatewayMetadata();
    }
}
