package com.inductiveautomation.ignition.examples.usersource.mongodb;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.gateway.config.ExtensionPoint;
import com.inductiveautomation.ignition.gateway.config.NamedResourceHandler;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import java.util.List;

/**
 * The GatewayHook is the main entry point for a Gateway module. It is instantiated very early in
 * the Gateway startup process, before most other services are available. It is responsible for
 * registering extension points and other module-level functionality.
 */
public class GatewayHook extends AbstractGatewayModuleHook {
  public static final String MODULE_ID =
      "com.inductiveautomation.ignition.examples.mongodb-user-source";

  private NamedResourceHandler<MongoDbUserSourceResource> namedResourceHandler;
  private GatewayContext context;

  static {
    // Register our localized properties with BundleUtil
    BundleUtil.get().addBundle(MongoDbUserSource.class);
  }

  @Override
  public void setup(GatewayContext context) {
    this.context = context;

    // Register our named resource handler for the MongoDbUserSourceResource type.
    namedResourceHandler =
        NamedResourceHandler.newBuilder(MongoDbUserSourceResource.META).context(context).build();
  }

  @Override
  public void startup(LicenseState licenseState) {
    // Start up the named resource handler to make our resource type available in the system.
    namedResourceHandler.startup();

    // Register user properties that can be used to extend the properties of a user in the system.
    context
        .getUserSourceManager()
        .registerUserProperties(
            MongoDbUserSource.FAVORITE_COLOR,
            MongoDbUserSource.FAVORITE_NUMBER,
            MongoDbUserSource.LIKES_APPLES);
  }

  @Override
  public void shutdown() {
    // Unregister the user properties we registered at startup.
    context
        .getUserSourceManager()
        .unregisterUserProperties(
            MongoDbUserSource.FAVORITE_COLOR,
            MongoDbUserSource.FAVORITE_NUMBER,
            MongoDbUserSource.LIKES_APPLES);

    // Shut down the named resource handler
    namedResourceHandler.shutdown();
  }

  /**
   * In a change from the <=8.1 model, any and all extension points, no matter <b>which</b>
   * extension point they extend, must be declared in your GatewayHook. They will be separated by
   * the gateway and the appropriate lifecycle management will be handled for you.
   */
  @Override
  public List<? extends ExtensionPoint<?>> getExtensionPoints() {
    return List.of(new MongoDbUserSourceExtensionPoint());
  }
}
