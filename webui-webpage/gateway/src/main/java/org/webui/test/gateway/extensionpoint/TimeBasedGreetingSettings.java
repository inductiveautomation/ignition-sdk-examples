package org.webui.test.gateway.extensionpoint;

import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.DefaultValue;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Description;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.FormField;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Label;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Required;
import com.inductiveautomation.ignition.gateway.web.nav.FormFieldType;

/**
 * Type-specific settings for the {@link TimeBasedGreetingProviderExtensionPoint}. Demonstrates that
 * each extension point type may have a completely different settings shape from its siblings.
 *
 * @param morning greeting used before noon
 * @param afternoon greeting used between noon and 18:00
 * @param evening greeting used after 18:00
 */
public record TimeBasedGreetingSettings(
    @Required
    @Label("Morning Greeting")
    @FormField(FormFieldType.TEXT)
    @Description("Shown before noon.")
    @DefaultValue("\"Good morning!\"")
    String morning,

    @Required
    @Label("Afternoon Greeting")
    @FormField(FormFieldType.TEXT)
    @Description("Shown between noon and 6pm.")
    @DefaultValue("\"Good afternoon!\"")
    String afternoon,

    @Required
    @Label("Evening Greeting")
    @FormField(FormFieldType.TEXT)
    @Description("Shown after 6pm.")
    @DefaultValue("\"Good evening!\"")
    String evening) {}
