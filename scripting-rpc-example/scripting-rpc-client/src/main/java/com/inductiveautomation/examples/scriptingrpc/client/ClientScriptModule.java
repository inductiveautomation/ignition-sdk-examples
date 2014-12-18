package com.inductiveautomation.examples.scriptingrpc.client;

import com.inductiveautomation.examples.scriptingrpc.AbstractScriptModule;
import com.inductiveautomation.examples.scriptingrpc.MathBlackBox;
import com.inductiveautomation.ignition.client.gateway_interface.ModuleRPCFactory;

public class ClientScriptModule extends AbstractScriptModule {

    private final MathBlackBox rpc;

    public ClientScriptModule() {
        rpc = ModuleRPCFactory.create(
                "com.inductiveautomation.ignition.examples.scripting-rpc",
                MathBlackBox.class);
    }

    @Override
    protected int multiplyImpl(int arg0, int arg1) {
        return rpc.multiply(arg0, arg1);
    }

}
