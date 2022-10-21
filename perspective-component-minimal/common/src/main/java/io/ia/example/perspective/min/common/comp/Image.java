package io.ia.example.perspective.min.common.comp;

import com.inductiveautomation.ignition.common.jsonschema.JsonSchema;
import com.inductiveautomation.perspective.common.api.ComponentDescriptor;
import com.inductiveautomation.perspective.common.api.ComponentDescriptorImpl;
import io.ia.example.perspective.min.common.OneComponent;


/**
 * Describes the component to the Java registry so the gateway and designer know to look for the front end elements.
 * In a 'common' scope so that it's referencable by both gateway and designer.
 */
public class Image  {

    // unique ID of the component which perfectly matches that provided in the javascript's ComponentMeta implementation
    public static String COMPONENT_ID = "com.gh.ia.simple.img";

    /**
     * The schema provided with the component descriptor. Use a schema instead of a plain JsonObject because it gives
     * us a little more type information, allowing the designer to highlight mismatches where it can detect them.
     *
     * We use a json file that follows the json schema format.  Alternatively, you may create the JsonSchema object
     * programmatically.
     */
    public static JsonSchema SCHEMA =
        JsonSchema.parse(OneComponent.class.getResourceAsStream("/image.props.json"));

    /**
     * Components register with the Java side ComponentRegistry but providing a ComponentDescriptor.  Here we
     * build the descriptor for this one component. Icons on the component palette are optional.
     */
    public static ComponentDescriptor DESCRIPTOR = ComponentDescriptorImpl.ComponentBuilder.newBuilder()
        .setPaletteCategory(OneComponent.COMPONENT_CATEGORY)
        .setId(COMPONENT_ID)
        .setModuleId(OneComponent.MODULE_ID)
        .setSchema(SCHEMA) //  this could alternatively be created purely in Java if desired
        .setName("OneComponent Image")
        .addPaletteEntry("", "Image", "A simple image component.", null, null)
        .setDefaultMetaName("oneImage")
        .setResources(OneComponent.BROWSER_RESOURCES)
        .build();

}
