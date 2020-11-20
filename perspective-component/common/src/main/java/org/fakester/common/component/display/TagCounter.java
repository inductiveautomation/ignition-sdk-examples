package org.fakester.common.component.display;

import com.inductiveautomation.ignition.common.jsonschema.JsonSchema;
import com.inductiveautomation.perspective.common.api.ComponentDescriptor;
import com.inductiveautomation.perspective.common.api.ComponentDescriptorImpl;
import org.fakester.common.RadComponents;

/**
 * Meta information about the TagCounter component.  See {@link Image} for docs on each field.
 */
public class TagCounter {
    public static String COMPONENT_ID = "rad.display.tagcounter";

    public static JsonSchema SCHEMA =
        JsonSchema.parse(RadComponents.class.getResourceAsStream("/tagcounter.props.json"));

    public static ComponentDescriptor DESCRIPTOR = ComponentDescriptorImpl.ComponentBuilder.newBuilder()
        .setPaletteCategory(RadComponents.COMPONENT_CATEGORY)
        .setId(COMPONENT_ID)
        .setModuleId(RadComponents.MODULE_ID)
        .setSchema(SCHEMA) //  this could alternatively be created purely in Java if desired
        .setName("Tag Counter")
        .setDefaultMetaName("tagCounter")
        .setResources(RadComponents.BROWSER_RESOURCES)
        .build();

}
