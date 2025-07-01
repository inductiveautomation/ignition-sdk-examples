package io.ia.ignition.sdk.examples.slack.profile;

import com.inductiveautomation.ignition.common.resourcecollection.ResourceType;
import com.inductiveautomation.ignition.gateway.config.ResourceTypeMeta;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.DefaultValue;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.DescriptionKey;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Required;
import io.ia.ignition.sdk.examples.slack.GatewayHook;

/**
 * A simple "dummy" resource intended only as a proof of concept.
 * Singleton resources are suitable for "global" state, such as gateway-wide settings that would not apply to a single
 * "profile" or instance of configuration (think port bindings, or a single shared API key).
 */
public record DemoSingletonResource(
        @DescriptionKey("SlackNotificationProfileSettings.DemoSingleton.someField")
        @DefaultValue("someDefault")
        @Required
        String someField,
        int someOtherField,
        NestedConfigObject nestedConfig
) {
        public record NestedConfigObject(
                @DescriptionKey("SlackNotificationProfileSettings.DemoSingleton.NestedConfigObject.someField")
                String aField,
                int anotherField
        ) {
        }

        public static final ResourceType TYPE = new ResourceType(GatewayHook.MODULE_ID, "demo-singleton");

        public static final DemoSingletonResource DEFAULT = new DemoSingletonResource(
                "default",
                42,
                new NestedConfigObject("nested", 24)
        );

        public static final ResourceTypeMeta<DemoSingletonResource> META = ResourceTypeMeta.newBuilder(DemoSingletonResource.class)
                .resourceType(TYPE)
                .singleton()
                .defaultConfig(DEFAULT)
                .categoryName("Demo Singleton Resource")
                .buildValidator((resource, validator) -> {
                        validator.requireNotEmpty("someField", resource.someField());
                })
                .build();
}
