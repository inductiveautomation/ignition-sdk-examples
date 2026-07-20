package org.webui.test.gateway.named;

import com.inductiveautomation.ignition.common.resourcecollection.ResourceType;
import com.inductiveautomation.ignition.gateway.config.ResourceTypeMeta;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.DefaultValue;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Description;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.FormChoices;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.FormField;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Label;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Maximum;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Minimum;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Required;
import com.inductiveautomation.ignition.gateway.web.nav.FormFieldType;
import org.webui.test.common.WebuiWebpageModule;

/**
 * Configuration for the "Greeting" <b>named</b> resource.
 *
 * <p>A named resource is the most common shape of configuration in Ignition 8.3: the user may
 * create any number of instances, each identified by a unique name (think database connections,
 * device connections, etc.). The gateway stores each instance as a resource inside a resource
 * collection, and this record is the in-memory, strongly-typed view of one such resource.
 *
 * <p>The record is deliberately a plain, immutable {@code record}. Because it is naturally
 * Gson-serializable, the platform can encode/decode it to/from JSON with no extra work (see {@link
 * #META}). The {@code openapi.annotations.*} annotations on each component are read by {@link
 * com.inductiveautomation.ignition.gateway.dataroutes.openapi.SchemaUtil SchemaUtil} to produce a
 * JSON schema that carries {@code x-form} metadata; the gateway's web UI renders an editor form
 * directly from that schema, so no custom React is required for editing.
 *
 * @param message the greeting text to display; required
 * @param tone a coarse "tone" for the greeting, rendered as a dropdown ({@code SELECT})
 * @param repeatCount how many times the greeting should be repeated (1-10)
 * @param shout if {@code true}, the greeting is rendered in all-caps
 */
public record GreetingConfig(
    @Required
    @Label("Message")
    @FormField(FormFieldType.TEXT)
    @Description("The text of the greeting.")
    @DefaultValue("\"Hello, world!\"")
    String message,

    @Label("Tone")
    @FormField(FormFieldType.SELECT)
    @FormChoices(
        ids = {"FRIENDLY", "FORMAL", "CASUAL"},
        labels = {"Friendly", "Formal", "Casual"})
    @Description("The tone of the greeting.")
    @DefaultValue("\"FRIENDLY\"")
    String tone,

    @Label("Repeat Count")
    @FormField(FormFieldType.NUMBER)
    @Minimum("1")
    @Maximum("10")
    @Description("How many times to repeat the greeting.")
    @DefaultValue("1")
    int repeatCount,

    @Label("Shout")
    @FormField(FormFieldType.CHECKBOX)
    @Description("If enabled, the greeting is rendered in ALL CAPS.")
    @DefaultValue("false")
    boolean shout) {

  /**
   * The {@link ResourceType} that identifies this category of resource. The first argument is the
   * owning module id; the second is a unique-within-the-module type id. Together they form the
   * resource type string used by the REST API and the web UI (here {@code
   * org.webui.test.WebuiWebpage/greeting}).
   */
  public static final ResourceType RESOURCE_TYPE =
      new ResourceType(WebuiWebpageModule.MODULE_ID, "greeting");

  /** A prototype used as the starting point when a user creates a new greeting. */
  public static GreetingConfig defaultConfig() {
    return new GreetingConfig("Hello, world!", "FRIENDLY", 1, false);
  }

  /**
   * The {@link ResourceTypeMeta} describes everything the platform needs to know about this
   * resource type. It is registered with the {@code ResourceTypeMetaRegistry} at gateway startup
   * (see the gateway hook).
   *
   * <p>Because we supply a route delegate, the platform automatically mounts CRUD REST routes for
   * this resource under {@code /data/api/v1/resources/...}. The {@code configSchema(Class)} call
   * derives the editor form schema from this record's annotations.
   */
  public static final ResourceTypeMeta<GreetingConfig> META =
      ResourceTypeMeta.newBuilder(GreetingConfig.class)
          .resourceType(RESOURCE_TYPE)
          .categoryName("Greetings")
          .description("Named greeting messages provided by the WebUI example module.")
          .defaultConfig(defaultConfig())
          .buildRouteDelegate(
              routes ->
                  routes
                      .configSchema(GreetingConfig.class)
                      .openApiGroupName("webui-example")
                      .openApiTagName("webui-example-greeting"))
          .build();

  /** Renders this greeting to a display string, applying the {@code shout} option. */
  public String render() {
    String base = message == null ? "" : message;
    if (shout) {
      base = base.toUpperCase();
    }
    return base;
  }
}
