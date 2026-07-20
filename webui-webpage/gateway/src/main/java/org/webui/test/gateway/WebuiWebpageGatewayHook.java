package org.webui.test.gateway;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.config.ConfigurationManager;
import com.inductiveautomation.ignition.gateway.config.DecodedResource;
import com.inductiveautomation.ignition.gateway.config.ExtensionPoint;
import com.inductiveautomation.ignition.gateway.config.ExtensionPointCollection;
import com.inductiveautomation.ignition.gateway.config.ExtensionPointConfig;
import com.inductiveautomation.ignition.gateway.config.ImmutableExtensionPointCollection;
import com.inductiveautomation.ignition.gateway.config.NamedResourceHandler;
import com.inductiveautomation.ignition.gateway.config.ResourceTypeMeta;
import com.inductiveautomation.ignition.gateway.config.SingletonResourceHandler;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.web.systemjs.SystemJsModule;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.webui.test.common.WebuiWebpageModule;
import org.webui.test.gateway.extensionpoint.GreetingProvider;
import org.webui.test.gateway.extensionpoint.GreetingProviderConfig;
import org.webui.test.gateway.extensionpoint.GreetingProviderExtensionPoint;
import org.webui.test.gateway.extensionpoint.StaticGreetingProviderExtensionPoint;
import org.webui.test.gateway.extensionpoint.TimeBasedGreetingProviderExtensionPoint;
import org.webui.test.gateway.named.GreetingConfig;
import org.webui.test.gateway.singleton.GreetingSettings;

/**
 * Gateway-scope entry point for the WebUI examples module.
 *
 * <p>This hook ties the three configuration-resource examples together:
 *
 * <ol>
 *   <li><b>Named</b> - {@link GreetingConfig}: many named instances, CRUD via REST + web UI.
 *   <li><b>Singleton</b> - {@link GreetingSettings}: exactly one instance for the whole gateway.
 *   <li><b>Extension point</b> - {@link GreetingProviderConfig}: a category with multiple
 *       module-provided types ({@link StaticGreetingProviderExtensionPoint}, {@link
 *       TimeBasedGreetingProviderExtensionPoint}).
 * </ol>
 *
 * <p>For each it (a) registers a {@link ResourceTypeMeta} so the platform mounts CRUD REST routes
 * and exposes a schema-driven editor, and (b) starts a resource <i>handler</i> that reacts to
 * configuration changes at runtime. It also serves a small React landing page (see the {@code
 * web-ui} sub-project) that reads these resources back through the REST API.
 */
public class WebuiWebpageGatewayHook extends AbstractGatewayModuleHook {

  private static final LoggerEx logger = LoggerEx.newBuilder().build(WebuiWebpageGatewayHook.class);

  /**
   * The compiled React bundle that backs this module's web UI. The URL resolves to a file mounted
   * from the module's {@code mounted/} resource folder via the {@code web-ui-test} path alias (see
   * {@link #getMountedResourceFolder()} / {@link #getMountPathAlias()}).
   */
  private static final SystemJsModule JS_MODULE =
      new SystemJsModule(WebuiWebpageModule.MODULE_ID, "/res/web-ui-test/webuiExamples.js");

  private GatewayContext context;

  // The extension point resource type meta must be built with the module manager (to gather all
  // contributed types), so - unlike the named/singleton metas - it isn't a static constant.
  private ResourceTypeMeta<ExtensionPointConfig<GreetingProviderConfig, ?>> providerMeta;
  private ExtensionPointCollection<GreetingProviderExtensionPoint<?>> providerTypes;

  // Runtime handlers: each mirrors the on-disk configuration into live, in-memory state.
  private NamedResourceHandler<GreetingConfig> greetingHandler;
  private SingletonResourceHandler<GreetingSettings> settingsHandler;
  private NamedResourceHandler<ExtensionPointConfig<GreetingProviderConfig, ?>> providerHandler;

  /** Live provider objects, one per configured extension point resource, keyed by resource name. */
  private final Map<String, GreetingProvider> providers = new ConcurrentHashMap<>();

  @Override
  public void setup(GatewayContext context) {
    this.context = context;

    ConfigurationManager configManager = context.getConfigurationManager();

    // 1 & 2: register the named and singleton resource types (their metas are self-contained).
    configManager.getResourceTypeMetaRegistry().register(GreetingConfig.META);
    configManager.getResourceTypeMetaRegistry().register(GreetingSettings.META);

    // 3: build the extension point collection. Passing an empty static list means the collection is
    // populated entirely from module-contributed types gathered via every module hook's
    // getExtensionPoints() - including this module's own (see #getExtensionPoints below).
    this.providerTypes =
        ImmutableExtensionPointCollection.<GreetingProviderExtensionPoint<?>>build(
            GreetingProviderConfig.RESOURCE_TYPE,
            context.getModuleManager(),
            List.<GreetingProviderExtensionPoint<?>>of());

    this.providerMeta =
        ResourceTypeMeta.newExtensionPointBuilder(GreetingProviderConfig.class)
            .resourceType(GreetingProviderConfig.RESOURCE_TYPE)
            .extensionPointCollection(providerTypes)
            .categoryName("Greeting Providers")
            .description("Pluggable sources of greetings, contributed as extension point types.")
            .buildRouteDelegate(
                routes ->
                    routes
                        .profileSchema(GreetingProviderConfig.class)
                        .openApiGroupName("webui-example")
                        .openApiTagName("webui-example-greeting-provider"))
            .build();
    configManager.getResourceTypeMetaRegistry().register(providerMeta);

    // Register the front-end bundle and mount a landing page under the gateway "Home" section.
    context.getWebResourceManager().getSystemJsModuleRegistry().add(JS_MODULE);
    context
        .getWebResourceManager()
        .getNavigationModel()
        .getHome()
        .addCategory(
            "webuiwebpage",
            cat ->
                cat.label("WebUI Examples")
                    .addPage(
                        "Overview",
                        page ->
                            page.position(10)
                                // "WebUiExamples" must match a named export from the bundle's entry
                                // (web-ui/src/index.ts).
                                .mount("/webui-examples", "WebUiExamples", JS_MODULE)));
  }

  @Override
  public void startup(LicenseState activationState) {
    // Named resource handler: keeps the module apprised of greeting create/update/delete.
    greetingHandler =
        NamedResourceHandler.newBuilder(GreetingConfig.META)
            .context(context)
            .onInitialResources(list -> logger.infof("Loaded %d greeting(s) on startup", list.size()))
            .onResourceAdded(
                r -> logger.infof("Greeting '%s' added -> \"%s\"", r.name(), r.config().render()))
            .onResourceUpdated(m -> logger.infof("Greeting '%s' updated", m.name()))
            .onResourceRemoved(r -> logger.infof("Greeting '%s' removed", r.name()))
            .build();
    greetingHandler.startup();

    // Singleton resource handler: seeds the default settings on first startup, then tracks changes.
    settingsHandler =
        SingletonResourceHandler.newBuilder(GreetingSettings.META)
            .context(context)
            .onChange(settings -> logger.infof("Greeting settings updated -> %s", settings))
            .build();
    settingsHandler.startup();

    // Extension point handler: instantiate a live GreetingProvider for each configured resource.
    providerHandler =
        NamedResourceHandler.newBuilder(providerMeta)
            .context(context)
            .onInitialResources(list -> list.forEach(this::addProvider))
            .onResourceAdded(this::addProvider)
            .onResourceUpdated(
                m -> {
                  removeProvider(m.oldResource().name());
                  addProvider(m.newResource());
                })
            .onResourceRemoved(r -> removeProvider(r.name()))
            .build();
    providerHandler.startup();
  }

  @Override
  public void shutdown() {
    if (providerHandler != null) {
      providerHandler.shutdown();
    }
    if (settingsHandler != null) {
      settingsHandler.shutdown();
    }
    if (greetingHandler != null) {
      greetingHandler.shutdown();
    }
    providers.clear();
  }

  /**
   * Exposes this module's extension point types to the platform. Every module hook's
   * {@code getExtensionPoints()} is aggregated by the module manager, which is how the
   * {@link ExtensionPointCollection} built in {@link #setup(GatewayContext)} discovers them (and how
   * a <i>different</i> module could contribute additional greeting provider types).
   */
  @Override
  public List<? extends ExtensionPoint<?>> getExtensionPoints() {
    return List.of(
        new StaticGreetingProviderExtensionPoint(), new TimeBasedGreetingProviderExtensionPoint());
  }

  private void addProvider(DecodedResource<ExtensionPointConfig<GreetingProviderConfig, ?>> r) {
    String typeId = r.config().type();
    providerTypes
        .getType(typeId)
        .ifPresentOrElse(
            ep -> {
              try {
                GreetingProvider provider = ep.createProvider(context, r);
                providers.put(r.name(), provider);
                logger.infof(
                    "Greeting provider '%s' (%s) says: \"%s\"",
                    r.name(), typeId, provider.getGreeting());
              } catch (Exception ex) {
                logger.warn("Failed to create greeting provider '" + r.name() + "'", ex);
              }
            },
            () -> logger.warnf("Unknown greeting provider type '%s' for '%s'", typeId, r.name()));
  }

  private void removeProvider(String name) {
    providers.remove(name);
  }

  @Override
  public Optional<String> getMountedResourceFolder() {
    return Optional.of("mounted");
  }

  @Override
  public Optional<String> getMountPathAlias() {
    return Optional.of("web-ui-test");
  }

  @Override
  public boolean isFreeModule() {
    return true;
  }
}
