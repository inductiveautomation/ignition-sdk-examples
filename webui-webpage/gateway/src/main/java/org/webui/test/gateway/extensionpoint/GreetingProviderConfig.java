package org.webui.test.gateway.extensionpoint;

import com.inductiveautomation.ignition.common.resourcecollection.ResourceType;
import com.inductiveautomation.ignition.gateway.config.ExtensionPointProfileConfig;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.DefaultValue;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Description;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.FormField;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.IsNullable;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Label;
import com.inductiveautomation.ignition.gateway.web.nav.FormFieldType;
import org.webui.test.common.WebuiWebpageModule;

/**
 * The <b>profile</b> configuration for the "Greeting Provider" extension point.
 *
 * <p>An extension point is a category of configuration that can have many "flavors" (types), each
 * contributed by a module. The configuration of an extension point resource is split into two
 * parts, stored together in one resource:
 *
 * <ul>
 *   <li>the <b>profile</b> - settings shared by <i>every</i> type of this extension point, defined
 *       here by implementing {@link ExtensionPointProfileConfig}, and
 *   <li>the <b>settings</b> - settings specific to the chosen type, defined by each {@link
 *       GreetingProviderExtensionPoint} implementation (e.g. {@link StaticGreetingSettings}).
 * </ul>
 *
 * <p>The single mandatory field is {@code type}, which selects the extension point type. The
 * platform automatically populates the set of legal {@code type} values (as a dropdown) from the
 * registered {@link com.inductiveautomation.ignition.gateway.config.ExtensionPointCollection
 * ExtensionPointCollection}, so we don't annotate it as a form field ourselves.
 *
 * @param type the extension point type id (e.g. {@code "static"} or {@code "timeBased"})
 * @param prefix an optional string prepended to whatever greeting the chosen provider produces;
 *     shared by all provider types
 */
public record GreetingProviderConfig(
    String type,
    @Label("Prefix")
    @FormField(FormFieldType.TEXT)
    @Description("Optional text prepended to every greeting this provider produces.")
    @IsNullable
    @DefaultValue("null")
    String prefix)
    implements ExtensionPointProfileConfig {

  public static final ResourceType RESOURCE_TYPE =
      new ResourceType(WebuiWebpageModule.MODULE_ID, "greeting-provider");
}
