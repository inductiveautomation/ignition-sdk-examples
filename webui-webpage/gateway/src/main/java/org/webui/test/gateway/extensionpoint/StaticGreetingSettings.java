package org.webui.test.gateway.extensionpoint;

import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.DefaultValue;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Description;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.FormField;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Label;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Required;
import com.inductiveautomation.ignition.gateway.web.nav.FormFieldType;

/**
 * Type-specific settings for the {@link StaticGreetingProviderExtensionPoint}.
 *
 * <p>These annotations feed the "settings" half of the extension point's editor form. Each
 * extension point type has its own settings shape, and the gateway swaps the settings portion of
 * the form based on the selected type.
 *
 * @param text the fixed greeting text this provider returns
 */
public record StaticGreetingSettings(
    @Required
    @Label("Text")
    @FormField(FormFieldType.TEXTAREA)
    @Description("The fixed text this provider returns.")
    @DefaultValue("\"Hello from the static provider!\"")
    String text) {}
