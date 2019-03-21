package org.fakester.common.component.display;

import java.util.Objects;

import com.inductiveautomation.ignition.common.jsonschema.JsonSchema;
import com.inductiveautomation.perspective.common.api.ComponentDescriptor;
import com.inductiveautomation.perspective.common.api.ComponentDescriptorImpl;
import org.fakester.common.RadComponents;


/**
 * Describes the component to the Java registry so the gateway and designer know to look for the front end elements.
 */
public class Image {

    // unique ID of the component which perfectly matches that provided in the javascript's ComponentMeta implementation.
    public static String COMPONENT_ID = "rad.display.image";

    /**
     * The schema provided with the component descriptor. Use a schema instead of a plain JsonObject because it gives
     * us a little more type information, allowing the designer to highlight mismatches where it can detect them.
     */
    public static JsonSchema SCHEMA =
        JsonSchema.parse(RadComponents.class.getResourceAsStream("/radimage.props.json"));

    /**
     * Components register with the Java side ComponentRegistry but providing a ComponentDescriptor.  Here we
     * build the descriptor for this one component.
     */
    public static ComponentDescriptor DESCRIPTOR = ComponentDescriptorImpl.ComponentBuilder.component()
        .withPaletteCategory("display")
        .withPaletteDescription("A simple image component")
        .withId(COMPONENT_ID)
        .withModuleId(RadComponents.MODULE_ID)
        .withSchema(SCHEMA) //  this could alternatively be created purely in Java if desired
        .withPaletteName("Radness")
        .withDefaultMetaName("radImage")
        .shouldAddToPalette(true)
        .withResources(RadComponents.BROWSER_RESOURCES)
        .build();
}
