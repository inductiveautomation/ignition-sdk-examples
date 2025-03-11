package io.ia.ignition.sdk.examples.slack.profile;

import com.inductiveautomation.ignition.common.resourcecollection.ResourceType;
import com.inductiveautomation.ignition.gateway.config.ResourceTypeMeta;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.DefaultValue;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.DescriptionKey;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Required;
import io.ia.ignition.sdk.examples.slack.GatewayHook;

public record DemoNamedResource(
        @DescriptionKey("SlackNotificationProfileSettings.DemoNamed.someField")
        @DefaultValue("someDefault")
        @Required
        String someField,
        int someOtherField,
        boolean aConfig,
        boolean anotherConfig
) {
        public static final ResourceType TYPE = new ResourceType(GatewayHook.MODULE_ID, "demo-named");

        public static final DemoNamedResource DEFAULT = new DemoNamedResource(
                "default",
                42,
                true,
                false
        );

        public static final ResourceTypeMeta<DemoNamedResource> META = ResourceTypeMeta.newBuilder(DemoNamedResource.class)
                .resourceType(TYPE)
                .defaultConfig(DEFAULT)
                .buildValidator((resource, validator) -> {
                        validator.checkField(
                                resource.someOtherField() < 1000,
                                "someOtherField",
                                "someOtherField must be less than 1000"
                        );
                })
                .build();
}
