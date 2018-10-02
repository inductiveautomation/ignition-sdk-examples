package com.inductiveautomation.ignition.examples.scripting;

public class GatewayScriptModule extends AbstractScriptModule {

    @Override
    protected int multiplyImpl(int arg0, int arg1) {
        return arg0 * arg1;
    }

}
