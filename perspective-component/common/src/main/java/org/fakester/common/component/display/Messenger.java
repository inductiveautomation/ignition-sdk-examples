package org.fakester.common.component.display;

import javax.swing.ImageIcon;

import com.inductiveautomation.ignition.common.jsonschema.JsonSchema;
import com.inductiveautomation.perspective.common.api.ComponentDescriptor;
import com.inductiveautomation.perspective.common.api.ComponentDescriptorImpl;
import org.fakester.common.RadComponents;


/**
 * Common meta information about the Messenger component.  See {@link Image} for docs on each field.
 */
public class Messenger {

    public static String COMPONENT_ID = "rad.display.messenger";


    public static JsonSchema SCHEMA =
        JsonSchema.parse(RadComponents.class.getResourceAsStream("/messenger.props.json"));

    public static ComponentDescriptor DESCRIPTOR = ComponentDescriptorImpl.ComponentBuilder.newBuilder()
        .withPaletteCategory(RadComponents.COMPONENT_CATEGORY)
        .withPaletteDescription("A component that uses component messaging and data fetching delegates.")
        .withId(COMPONENT_ID)
        .withModuleId(RadComponents.MODULE_ID)
        .withSchema(SCHEMA) //  this could alternatively be created purely in Java if desired
        .withPaletteName("Gateway Messenger")
        .withDefaultMetaName("messenger")
        .shouldAddToPalette(true)
        .withResources(RadComponents.BROWSER_RESOURCES)
        .build();
}
