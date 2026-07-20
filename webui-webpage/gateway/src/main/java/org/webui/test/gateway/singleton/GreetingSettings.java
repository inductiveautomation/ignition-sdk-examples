package org.webui.test.gateway.singleton;

import com.inductiveautomation.ignition.common.resourcecollection.ResourceType;
import com.inductiveautomation.ignition.gateway.config.ResourceTypeMeta;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.DefaultValue;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Description;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.FormField;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Label;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Minimum;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Required;
import com.inductiveautomation.ignition.gateway.web.nav.FormFieldType;
import org.webui.test.common.WebuiWebpageModule;

/**
 * Configuration for the example's <b>singleton</b> resource.
 *
 * <p>A singleton resource is one that has exactly one instance for the whole gateway - think
 * "Gateway Settings" or "Web Server Settings". Structurally it is nearly identical to a named
 * resource; the only differences are:
 *
 * <ul>
 *   <li>the {@link ResourceTypeMeta} is built with {@link
 *       com.inductiveautomation.ignition.gateway.config.DefaultResourceTypeMeta.Builder#singleton()
 *       singleton()},
 *   <li>the resource is stored at the type's root path (it has no name), and
 *   <li>a {@link com.inductiveautomation.ignition.gateway.config.SingletonResourceHandler
 *       SingletonResourceHandler} manages the single instance and seeds it from {@link
 *       #defaultConfig()} the first time the gateway starts.
 * </ul>
 *
 * @param pageTitle the title shown on the example's landing page
 * @param showTimestamp whether the landing page should show a "last updated" timestamp
 * @param refreshIntervalMs how often (ms) the landing page should re-poll the gateway
 */
public record GreetingSettings(
    @Required
    @Label("Page Title")
    @FormField(FormFieldType.TEXT)
    @Description("Title shown at the top of the example's landing page.")
    @DefaultValue("\"WebUI Examples\"")
    String pageTitle,

    @Label("Show Timestamp")
    @FormField(FormFieldType.CHECKBOX)
    @Description("Whether the landing page displays a last-updated timestamp.")
    @DefaultValue("true")
    boolean showTimestamp,

    @Required
    @Label("Refresh Interval (ms)")
    @FormField(FormFieldType.NUMBER)
    @Minimum("0")
    @Description("How often, in milliseconds, the landing page re-polls the gateway.")
    @DefaultValue("5000")
    int refreshIntervalMs) {

  public static final ResourceType RESOURCE_TYPE =
      new ResourceType(WebuiWebpageModule.MODULE_ID, "greeting-settings");

  /**
   * The default (and initial) configuration. For singletons this is important: the {@code
   * SingletonResourceHandler} writes these values on first startup so the resource always exists.
   */
  public static GreetingSettings defaultConfig() {
    return new GreetingSettings("WebUI Examples", true, 5000);
  }

  public static final ResourceTypeMeta<GreetingSettings> META =
      ResourceTypeMeta.newBuilder(GreetingSettings.class)
          .resourceType(RESOURCE_TYPE)
          .categoryName("Greeting Settings")
          .description("Global settings for the WebUI example module.")
          .defaultConfig(defaultConfig())
          // The .singleton() call is the only thing that distinguishes this from a named resource.
          .singleton()
          .buildRouteDelegate(
              routes ->
                  routes
                      .configSchema(GreetingSettings.class)
                      .openApiGroupName("webui-example")
                      .openApiTagName("webui-example-greeting-settings"))
          .build();
}
