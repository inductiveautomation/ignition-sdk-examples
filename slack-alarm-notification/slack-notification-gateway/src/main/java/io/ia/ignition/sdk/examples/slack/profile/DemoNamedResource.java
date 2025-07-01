package io.ia.ignition.sdk.examples.slack.profile;

import com.inductiveautomation.ignition.common.resourcecollection.Resource;
import com.inductiveautomation.ignition.common.resourcecollection.ResourceType;
import com.inductiveautomation.ignition.common.util.ResourceUtil;
import com.inductiveautomation.ignition.gateway.config.DecodedResource;
import com.inductiveautomation.ignition.gateway.config.ResourceTypeMeta;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.DefaultValue;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.DescriptionKey;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Required;
import io.ia.ignition.sdk.examples.slack.GatewayHook;

/**
 * A simple "dummy" resource intended only as a proof of concept.
 * Named resources are suitable for "instance" state, such as a single "profile" or instance of configuration (think
 * a particular notification profile, database connection, or similar).
 * <p/>
 * Note: This resource does not have a name, UUID, enabled, or description field.
 * Those are provided "for free" via the resource system, and should not be repeated on your individual config.
 *
 * @see ResourceUtil#isEnabled(Resource)
 * @see ResourceUtil#getUuid(Resource)
 * @see DecodedResource
 */
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
                .categoryName("Demo Named Resource")
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
