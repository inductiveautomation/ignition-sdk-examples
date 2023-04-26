package io.ia.examples.resource;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import com.inductiveautomation.ignition.common.project.resource.ProjectResource;
import com.inductiveautomation.ignition.common.project.resource.ProjectResourceBuilder;
import com.inductiveautomation.ignition.common.project.resource.ResourceType;
import com.inductiveautomation.ignition.common.script.typing.ExtensionFunctionDescriptor;
import com.inductiveautomation.ignition.common.script.typing.TypeDescriptor;
import org.jetbrains.annotations.NotNull;

public final class PythonResource {
    public static final ResourceType RESOURCE_TYPE = new ResourceType(Constants.MODULE_ID, "example-resource");

    public static final ExtensionFunctionDescriptor FUNCTION_DESCRIPTOR =
        new ExtensionFunctionDescriptor.Builder("onEvent")
            .param("payload", "The payload sent to the event handler", TypeDescriptor.Dictionary)
            .build();
    private final String name;
    private final String userCode;

    public PythonResource(@NotNull String name, @NotNull String userCode) {
        this.name = Objects.requireNonNull(name);
        this.userCode = Objects.requireNonNull(userCode);
    }

    public static PythonResource fromResource(ProjectResource resource) {
        String resourceName = resource.getResourceName();
        return new PythonResource(
            resourceName,
            new String(Objects.requireNonNull(resource.getData(resourceName + ".py")), StandardCharsets.UTF_8)
        );
    }

    public static Consumer<ProjectResourceBuilder> toResource(@NotNull PythonResource resource) {
        return builder -> builder.setData(
            Map.of(
                resource.getName() + ".py",
                resource.getUserCode().getBytes(StandardCharsets.UTF_8)
            ));
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

        if (!Objects.equals(name, that.name)) {
            return false;
        }
        return Objects.equals(userCode, that.userCode);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + userCode.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("PythonResource{name='%s', userCode='%s'}", name, userCode);
    }

    public String getName() {
        return name;
    }

    public String getUserCode() {
        return userCode;
    }
}
