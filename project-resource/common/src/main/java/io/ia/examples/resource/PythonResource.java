package io.ia.examples.resource;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Consumer;
import com.inductiveautomation.ignition.common.ImmutableBytes;
import com.inductiveautomation.ignition.common.resourcecollection.Resource;
import com.inductiveautomation.ignition.common.resourcecollection.ResourceBuilder;
import com.inductiveautomation.ignition.common.resourcecollection.ResourceType;
import com.inductiveautomation.ignition.common.script.typing.ExtensionFunctionDescriptor;
import com.inductiveautomation.ignition.common.script.typing.TypeDescriptor;
import com.inductiveautomation.ignition.common.util.ResourceUtil;
import org.jetbrains.annotations.NotNull;

/**
 * An extremely basic record that serves to demonstrate best practices for an Ignition 'resource class'.
 * <p>
 * The static {@link PythonResource#fromResource} method is used to allow evolution of the resource format over time in
 * a forwards compatible way, as any prior version of your module/Ignition could be storing your resource in a different
 * way.
 * <p>
 * The {@link PythonResource#toResource} method returns a {@link Consumer} to ease interop with various Ignition
 * platform methods.
 */
public record PythonResource(
        @NotNull String userCode,
        boolean enabled
) {
    public static final ResourceType RESOURCE_TYPE = new ResourceType(Constants.MODULE_ID, "example-resource");
    public static final String RESOURCE_FILE = "code.py";

    public static final ExtensionFunctionDescriptor FUNCTION_DESCRIPTOR =
            new ExtensionFunctionDescriptor.Builder("onEvent")
                    .param("payload", "The payload sent to the event handler", TypeDescriptor.Dictionary)
                    .build();


    public PythonResource {
        Objects.requireNonNull(userCode);
    }

    public static PythonResource fromResource(Resource resource) {
        String code = resource.getData(RESOURCE_FILE)
                .map(ImmutableBytes::getBytesAsString)
                .orElse("\t");

        return new PythonResource(code, ResourceUtil.isEnabled(resource));
    }

    public static Consumer<ResourceBuilder> toResource(PythonResource resource) {
        return builder -> builder
                .putAttribute("enabled", resource.enabled())
                .putData(
                        RESOURCE_FILE,
                        resource.userCode().getBytes(StandardCharsets.UTF_8)
                );
    }

    @NotNull
    @Override
    public String toString() {
        return String.format("PythonResource{userCode='%s', enabled=%s}",
                userCode, enabled);
    }
}
