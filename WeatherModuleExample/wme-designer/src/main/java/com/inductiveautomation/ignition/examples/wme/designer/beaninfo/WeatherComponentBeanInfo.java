/* Filename: WeatherBeanInfo.java
 * Created on Jul 9, 2010
 * Author: Kevin Herron
 * Copyright Inductive Automation 2010
 * Project: WeatherModule_Designer
 */
package com.inductiveautomation.ignition.examples.wme.designer.beaninfo;

import com.inductiveautomation.factorypmi.designer.property.customizers.DynamicPropertyProviderCustomizer;
import com.inductiveautomation.ignition.examples.wme.client.WeatherComponent;
import com.inductiveautomation.vision.api.designer.beans.CommonBeanInfo;
import com.inductiveautomation.vision.api.designer.beans.CustomizerDescriptor;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.beans.SimpleBeanInfo;
import javax.swing.ImageIcon;


public class WeatherComponentBeanInfo extends CommonBeanInfo {

    public WeatherComponentBeanInfo() {
        super(WeatherComponent.class, new CustomizerDescriptor[]{DynamicPropertyProviderCustomizer.VALUE_DESCRIPTOR});
    }

    @Override
    protected void initProperties() throws IntrospectionException {
        super.initProperties();

        addBoundProp("zipCode", "ZIP Code", "The ZIP code this weather is for.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addBoundProp("currentTemp", "Current Temp", "The current temperature.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addBoundProp("lowTemp", "Low Temp", "The predicted low temperature.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addBoundProp("highTemp", "High Temp", "The predicted high temperature.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addBoundProp("iconName", "Icon Name", "The name of the icon that represents the current weather conditions.",
                CAT_DATA, PREFERRED_MASK | BOUND_MASK);

        addProp("dynamicProps", "Dynamic Properties", "", HIDDEN_MASK);

        addDataQuality();
    }

    @Override
    public Image getIcon(int kind) {
        Image image = null;

        switch (kind) {
            case SimpleBeanInfo.ICON_COLOR_16x16:
            case SimpleBeanInfo.ICON_MONO_16x16:
                image = new ImageIcon(WeatherComponent.class.getResource("icon_16x16.png")).getImage();
                break;

            case SimpleBeanInfo.ICON_COLOR_32x32:
            case SimpleBeanInfo.ICON_MONO_32x32:
                image = new ImageIcon(WeatherComponent.class.getResource("icon_32x32.png")).getImage();
                break;
        }

        return image;
    }

    @Override
    protected void initDesc() {
        getBeanDescriptor().setName("Weather Component");
        getBeanDescriptor().setDisplayName("Weather Component");
        getBeanDescriptor().setShortDescription("Get live weather conditions on your windows.");
    }
}
