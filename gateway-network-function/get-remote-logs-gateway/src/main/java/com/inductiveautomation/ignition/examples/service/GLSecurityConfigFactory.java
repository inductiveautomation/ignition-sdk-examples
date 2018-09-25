package com.inductiveautomation.ignition.examples.service;

import com.inductiveautomation.ignition.common.config.PropertySet;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.examples.service.GLSecurityConfigValues.Mode;
import com.inductiveautomation.ignition.gateway.gan.security.SecuredEntity.ConfigFactory;

/**
 * Created by mattgross on 9/26/2016. This class sets up the properties that will later be used to determine whether
 * service access is granted or denied. Basically, we return a GLSecurityConfigValues object that is later retrieved
 * by the GetLogs service. This object can be used by different methods in the service, and encapsulates all security
 * functionality into a single class.
 */
public class GLSecurityConfigFactory implements ConfigFactory<GLSecurityConfigValues> {
    @Override
    public GLSecurityConfigValues create(PropertySet propertySet) {
        GLSecurityConfigValues config = new GLSecurityConfigValues();

        for (PropertyValue pv : propertySet) {
            if(pv.getProperty().getName().startsWith(GLSecurityDescriptorFactory.DYNAMIC_PROP_ID)){
                config.setDynamicProp(pv.getProperty().getName(), (Mode) pv.getValue());
            }
            else{
                config.setAccessValue(pv.getProperty().getName(), pv.getValue());
            }
        }
        return config;
    }
}
