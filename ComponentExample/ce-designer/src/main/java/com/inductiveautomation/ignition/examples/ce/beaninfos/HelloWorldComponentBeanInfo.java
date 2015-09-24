/* Filename: HelloWorldComponentBeanInfo.java
 * Created by Perry Arellano-Jones on 12/11/14.
 * Copyright Inductive Automation 2014
 */
package com.inductiveautomation.ignition.examples.ce.beaninfos;


import static com.inductiveautomation.ignition.examples.ce.components.HelloWorldComponent.ANIMATION_LTR;
import static com.inductiveautomation.ignition.examples.ce.components.HelloWorldComponent.ANIMATION_OFF;
import static com.inductiveautomation.ignition.examples.ce.components.HelloWorldComponent.ANIMATION_RTL;

import com.inductiveautomation.factorypmi.designer.property.customizers.DynamicPropertyProviderCustomizer;
import com.inductiveautomation.factorypmi.designer.property.customizers.StyleCustomizer;
import com.inductiveautomation.ignition.examples.ce.components.HelloWorldComponent;
import com.inductiveautomation.vision.api.designer.beans.CommonBeanInfo;
import com.inductiveautomation.vision.api.designer.beans.VisionBeanDescriptor;

import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.SimpleBeanInfo;
import javax.swing.ImageIcon;


/**
 * This BeanInfo class describes the {@link HelloWorldComponent}, which is the component that this example module adds
 * to the Vision Module
 *
 * @author Carl Gould
 */
public class HelloWorldComponentBeanInfo extends CommonBeanInfo {

	public HelloWorldComponentBeanInfo() {
	    /*
	     * Our superclass constructor takes the class of the component we describe and the customizers that are
		 * applicable
		 */
		super(HelloWorldComponent.class, DynamicPropertyProviderCustomizer.VALUE_DESCRIPTOR, StyleCustomizer.VALUE_DESCRIPTOR);
	}

	@Override
	protected void initProperties() throws IntrospectionException {
		// Adds common properties
		super.initProperties();

		// Remove some properties which aren't used in our component.
		removeProp("foreground");
		removeProp("background");
		removeProp("opaque");

		// Add our properties
		// Note that all String properties are automatically added to the component's translatable terms
		// unless you add NOT_TRANSLATABLE_MASK
		addProp("text", "Text", "The text to display in the component", CAT_DATA, PREFERRED_MASK | BOUND_MASK);

		addEnumProp("animation", "Animation Mode", "This mode turns on or off animation marquee.", CAT_BEHAVIOR,
				new int[]{ANIMATION_OFF, ANIMATION_LTR, ANIMATION_RTL},
				new String[]{"Off", "Left to Right", "Right to Left"});
		addProp("animationRate", "Animation Rate", "The time between frames of animation, if it is turned on.",
				CAT_BEHAVIOR);

		addProp("fillColor", "Fill Color", "The color to fill the letters with.", CAT_APPEARANCE, PREFERRED_MASK);
		addProp("strokeColor", "Stroke Color", "The color to use for the letter outline.", CAT_APPEARANCE);
		addProp("strokeWidth", "Stroke Width", "The width of the letter outline, or 0 to turn outlining off.",
				CAT_APPEARANCE);
	}

	@Override
	public Image getIcon(int kind) {
		switch (kind) {
			case BeanInfo.ICON_COLOR_16x16:
			case BeanInfo.ICON_MONO_16x16:
				return new ImageIcon(getClass().getResource("/images/hello_world_16.png")).getImage();
			case SimpleBeanInfo.ICON_COLOR_32x32:
			case SimpleBeanInfo.ICON_MONO_32x32:
				return new ImageIcon(getClass().getResource("/images/hello_world_32.png")).getImage();
		}
		return null;
	}

	@Override
	protected void initDesc() {
		VisionBeanDescriptor bean = getBeanDescriptor();
		bean.setName("Hello World");
		bean.setDisplayName("Hello World");
		bean.setShortDescription("A component that displays the text 'Hello World'.");
		// This adds any extra translatable terms (other than String properties above)
		// Alter HelloWorldComponentTermFinder to add static and dynamic props
		bean.setValue(CommonBeanInfo.TERM_FINDER_CLASS, HelloWorldComponentTermFinder.class);
	}

}
