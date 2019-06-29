package com.inductiveautomation.ignition.examples;

import java.util.ArrayList;
import java.util.List;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.examples.service.GetLogsService;
import com.inductiveautomation.ignition.examples.service.GetLogsServiceImpl;
import com.inductiveautomation.ignition.examples.task.GetWrapperLogTaskType;
import com.inductiveautomation.ignition.gateway.clientcomm.ClientReqSession;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.tasks.TaskType;
import com.inductiveautomation.metro.api.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main Gateway entry point to the remote logging module.
 */
public class GatewayHook extends AbstractGatewayModuleHook {

    public static final String TASK_OWNERID = "remotelogging";

    private GatewayContext context;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private GetLogsRPCImpl rpc;
    private GetLogsService getLogsService;
    private List<TaskType> registeredTasks = new ArrayList<>();

    @Override
    public void setup(GatewayContext gatewayContext) {
        this.context = gatewayContext;
        this.rpc = new GetLogsRPCImpl(context);

        // Properties file setup
        BundleUtil.get().addBundle("remotelogging", getClass(), "RemoteLogging");

        // Service setup
        ServiceManager sm = context.getGatewayAreaNetworkManager().getServiceManager();
        getLogsService = new GetLogsServiceImpl(context);
        sm.registerService(GetLogsService.class, getLogsService);

        // Task setup
        registeredTasks.add(new GetWrapperLogTaskType());
        for(TaskType tt: registeredTasks){
            context.getTaskManager().registerTaskType(tt);
        }
    }

    @Override
    public void startup(LicenseState licenseState) {
        // no-op in this example
    }

    @Override
    public void shutdown() {
        // Remove services
        ServiceManager sm = context.getGatewayAreaNetworkManager().getServiceManager();
        sm.unregisterService(GetLogsService.class);

        // Remove tasks
        for(TaskType tt: registeredTasks){
            context.getTaskManager().unregisterTaskType(tt);
        }

        // Remove properties files
        BundleUtil.get().removeBundle(getClass());
    }

    @Override
    public void initializeScriptManager(ScriptManager manager) {
        manager.addScriptModule("system.example", new GetLogsGatewayFunctions(rpc), new GetLogsScriptFunctions.Documentation());
    }

    @Override
    public Object getRPCHandler(ClientReqSession session, String projectName) {
        return rpc;
    }
    
}
