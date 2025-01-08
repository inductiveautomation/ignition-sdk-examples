package com.inductiveautomation.ignition.examples.scripting;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.script.hints.PropertiesFileDocProvider;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.rpc.GatewayRpcImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class GatewayHook extends AbstractGatewayModuleHook {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void setup(GatewayContext gatewayContext) {
        logger.info("setup()");
    }

    @Override
    public void startup(LicenseState licenseState) {
        logger.info("startup()");
    }

    @Override
    public void shutdown() {
        logger.info("shutdown()");
    }

    @Override
    public void initializeScriptManager(ScriptManager manager) {
        super.initializeScriptManager(manager);

        manager.addScriptModule(
                "system.example",
                new GatewayScriptModule(GatewayHook::getMetadata),
                new PropertiesFileDocProvider());
    }

    @Override
    public Optional<GatewayRpcImplementation> getRpcImplementation() {
        return Optional.of(GatewayRpcImplementation.of(
                RpcFunctions.SERIALIZER,
                new RpcFunctionsImpl(GatewayHook::getMetadata)
        ));
    }

    private static Metadata getMetadata() {
        return new Metadata(
                System.getProperty("os.name"),
                System.getProperty("os.arch"),
                System.getProperty("os.version")
        );
    }
}
