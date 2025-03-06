package com.inductiveautomation.ignition.examples.gn;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.logging.LogEvent;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.script.hints.PropertiesFileDocProvider;
import com.inductiveautomation.ignition.examples.gn.protoserializers.LogEventSerializer;
import com.inductiveautomation.ignition.examples.gn.service.GetLogsService;
import com.inductiveautomation.ignition.examples.gn.service.GetLogsServiceImpl;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.rpc.GatewayRpcImplementation;
import com.inductiveautomation.metro.api.MetroProtobufRegistry;
import com.inductiveautomation.metro.api.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main Gateway entry point to the remote logging module.
 */
public class GatewayHook extends AbstractGatewayModuleHook {
    private GatewayContext context;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private GetLogsGatewayFunctions gatewayFunctions;
    private GatewayRpcImplementation rpc;
    private GetLogsService getLogsService;

    @Override
    public void setup(GatewayContext gatewayContext) {
        this.context = gatewayContext;
        this.gatewayFunctions = new GetLogsGatewayFunctions(context, this);

        // This sets up the gateway side of the RPC implementation for this module. It is used by
        // getRpcImplementation() below. The designer/client side of the RPC is set up by the GetLogsClientFunctions
        // constructor.
        rpc = GatewayRpcImplementation.of(gatewayFunctions.rpcSerializer, this.gatewayFunctions);

        // Register any needed gateway network Protobuf classes. Without this, the gateway will have no idea how to
        // serialize LogEvents over the gateway network.
        MetroProtobufRegistry factory = context.getGatewayNetworkManager().getProtobufFactory();
        factory.register(new LogEventSerializer(), LogEvent.class);

        // Service setup
        ServiceManager sm = context.getGatewayNetworkManager().getServiceManager();
        getLogsService = new GetLogsServiceImpl(context, this);
        sm.registerService(GetLogsService.class, getLogsService);
    }

    @Override
    public void startup(LicenseState licenseState) {
        // no-op in this example
    }

    @Override
    public void shutdown() {
        // Remove services
        ServiceManager sm = context.getGatewayNetworkManager().getServiceManager();
        sm.unregisterService(GetLogsService.class);

        // Remove properties files
        BundleUtil.get().removeBundle(getClass());
    }

    @Override
    public void initializeScriptManager(ScriptManager manager) {
        super.initializeScriptManager(manager);

        manager.addScriptModule("system.example.gn",
            this.gatewayFunctions,
            new PropertiesFileDocProvider());
    }

    /**
     * This has to be implemented for the module. Without it, you get these types of errors when trying to use the
     * functions: "java.lang.IllegalArgumentException: No RPC interfaces found on implementation class"
     * @return the RPC implementation for this module
     */
    @Override
    public Optional<GatewayRpcImplementation> getRpcImplementation() {
        return Optional.of(rpc);
    }


    /**
     *  Makes a temporary copy of system_logs.idb
     * @return the full file path of system_logs.idb on the local gateway
     */
    public File getTempLogFile() throws IOException {
        // Make a temporary copy of the logs db
        File logFile = new File(context.getSystemManager().getLogsDir() + File.separator + "system_logs.idb");
        File tempLog;
        if(logFile.exists()) {
            tempLog = new File(System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID()
                + ".system_logs.idb");

            Files.copy(logFile.toPath(), tempLog.toPath());
        }
        else {
            throw new IOException(String.format("%s does not exist. Log file will not be sent",
                logFile.getAbsolutePath()));
        }

        return tempLog;
    }
}
