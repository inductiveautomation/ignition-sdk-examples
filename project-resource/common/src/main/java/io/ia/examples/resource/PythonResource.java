package io.ia.examples.resource;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Consumer;

import com.inductiveautomation.ignition.common.gson.JsonElement;
import com.inductiveautomation.ignition.common.project.resource.ProjectResource;
import com.inductiveautomation.ignition.common.project.resource.ProjectResourceBuilder;
import com.inductiveautomation.ignition.common.project.resource.ResourceType;
import com.inductiveautomation.ignition.common.script.typing.ExtensionFunctionDescriptor;
import com.inductiveautomation.ignition.common.script.typing.TypeDescriptor;
import org.jetbrains.annotations.NotNull;

public final class PythonResource {
    public static final ResourceType RESOURCE_TYPE = new ResourceType(Constants.MODULE_ID, "example-resource");
    public static final String RESOURCE_FILE = "code.py";

    public static final ExtensionFunctionDescriptor FUNCTION_DESCRIPTOR =
        new ExtensionFunctionDescriptor.Builder("onEvent")
            .param("payload", "The payload sent to the event handler", TypeDescriptor.Dictionary)
            .build();
    private final String userCode;
    private final boolean enabled;

    public PythonResource(@NotNull String userCode, boolean enabled) {
        this.userCode = Objects.requireNonNull(userCode);
        this.enabled = enabled;
    }

    public static PythonResource fromResource(ProjectResource resource) {
        String code = new String(
            Objects.requireNonNull(resource.getData(RESOURCE_FILE)),
            StandardCharsets.UTF_8
        );
        boolean isEnabled = resource.getAttribute("enabled")
            .map(JsonElement::getAsBoolean)
            .orElse(true);
        return new PythonResource(code, isEnabled);
    }

    public static Consumer<ProjectResourceBuilder> toResource(@NotNull PythonResource resource) {
        return builder -> builder
            .putAttribute("enabled", resource.enabled)
            .putData(
                RESOURCE_FILE,
                resource.getUserCode().getBytes(StandardCharsets.UTF_8)
            );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PythonResource that = (PythonResource) o;

        if (enabled != that.enabled) {
            return false;
        }
        return userCode.equals(that.userCode);
    }

    @Override
    public int hashCode() {
        int result = userCode.hashCode();
        result = 31 * result + (enabled ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("PythonResource{userCode='%s', enabled=%s}", userCode, enabled);
    }

    public String getUserCode() {
        return userCode;
    }
}
