package org.webui.test.gateway;

import java.util.Optional;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.gateway.dataroutes.RouteGroup;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.model.GatewayModule;
import com.inductiveautomation.ignition.gateway.model.GatewayModuleHook;
import com.inductiveautomation.ignition.gateway.web.systemjs.SystemJsModule;
import jakarta.servlet.http.HttpServletResponse;
import org.webui.test.common.WebuiWebpageModule;

/**
 * Class which is instantiated by the Ignition platform when the module is loaded in the gateway scope.
 */
public class WebuiWebpageGatewayHook extends AbstractGatewayModuleHook {
    /**
     * Called before startup. This is the chance for the module to add its extension points and update persistent
     * records and schemas. None of the managers will be started up at this point, but the extension point managers will
     * accept extension point types.
     */
    @Override
    public void setup(GatewayContext context) {

        GatewayModule module = context.getModuleManager().getModule(WebuiWebpageModule.MODULE_ID);
        if (module == null || module.getHook() != this) {
            System.exit(0);
        }

        SystemJsModule jsModule =
            new SystemJsModule("org.webui.test.WebuiWebpage",
                "/res/web-ui-test/helloIgnition.js");

        // note that we are adding the nav to the "home" section of the nav by using getHome(), but this can be added
        // to different sections by using the appropriate method (IE: getPlatform(), getConnections(), getNetwork(), ect)
        context.getWebResourceManager().getNavigationModel().getHome().addCategory("webuiwebpage", cat -> cat
            .label("Web UI Webpage")
            .addPage("Hello Ignition", page -> page
                .position(10)
                // Note the second parameter is the name of the JS component that was exported
                .mount("/hello-ignition", "HelloIgnition", jsModule)
            )
        );

    }

    /**
     * Called to initialize the module. Will only be called once. Persistence interface is available, but only in
     * read-only mode.
     */
    @Override
    public void startup(LicenseState activationState) {

    }

    /**
     * Called to shutdown this module. Note that this instance will never be started back up - a new one will be created
     * if a restart is desired
     */
    @Override
    public void shutdown() {

    }

    /**
     * @return the path to a folder in one of the module's gateway jar files that should be mounted at
     * /res/module-id/foldername
     */
    @Override
    public Optional<String> getMountedResourceFolder() {
        return Optional.of("mounted");
    }

    /**
     * Provides a chance for the module to mount any route handlers it wants. These will be active at
     * <tt>/data/module-id/*</tt> See {@link RouteGroup} for details. Will be called after startup().
     */
    @Override
    public void mountRouteHandlers(RouteGroup routes) {

    }

    /**
     * Used by the mounting underneath /res/module-id/* and /data/module-id/* as an alternate mounting path instead
     * of your module id, if present.
     */
    @Override
    public Optional<String> getMountPathAlias() {
        return Optional.of("web-ui-test");
    }

    /**
     * @return {@code true} if this is a "free" module, i.e. it does not participate in the licensing system. This is
     * equivalent to the now defunct FreeModule attribute that could be specified in module.xml.
     */
    @Override
    public boolean isFreeModule() {
        return true;
    }

    /**
     * Register any {@link ResourceTypeAdapter}s this module needs with {@code registry}.
     * <p>
     * ResourceTypeAdapters are used to adapt a legacy (7.9 or prior) resource type name or payload into a nicer format
     * for the Ignition 8.0 project resource system.Ò Only override this method for modules that aren't known by the
     * {@link ResourceTypeAdapterRegistry} already.
     * <p>
     * <b>This method is called before {@link #setup(GatewayContext)} or {@link #startup(LicenseState)}.</b>
     *
     * @param registry the shared {@link ResourceTypeAdapterRegistry} instance.
     */
    // @Override
    // public void initializeResourceTypeAdapterRegistry(ResourceTypeAdapterRegistry registry) {
    //
    // }

    /**
     * Called prior to a 'mounted resource request' being fulfilled by requests to the mounted resource servlet serving
     * resources from /res/module-id/ (or /res/alias/ if {@link GatewayModuleHook#getMountPathAlias} is implemented). It
     * is called after the target resource has been successfully located.
     *
     * <p>
     * Primarily intended as an opportunity to amend/alter the response's headers for purposes such as establishing
     * Cache-Control. By default, Ignition sets no additional headers on a resource request.
     * </p>
     *
     * @param resourcePath path to the resource being returned by the mounted resource request
     * @param response     the response to read/amend.
     */
    @Override
    public void onMountedResourceRequest(String resourcePath, HttpServletResponse response) {

    }
}
