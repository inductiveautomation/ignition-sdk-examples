package com.inductiveautomation.ignition.examples.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mattgross on 9/26/2016. All security-related functions are encapsulated within this class. Properties
 * are created in GLSecurityDescriptorFactory, and are configured in GLSecurityConfigFactory. If any method determines
 * that the passed property is insufficient compared to the stored property, then the method throws a SecurityException.
 */
public class GLSecurityConfigValues {

    public enum Mode {Enabled, Disabled}
    public static final Mode DEFAULT_MODE = Mode.Enabled;

    private Map<String, Object> accessProps = new HashMap<>();
    private Map<String, Mode> dynamicProps = new HashMap<>();

    /**
     * Compares the passed value against the stored value in the accessProps map.
     * @param propertyName
     * @param valueToTest
     * @return true if the values match, or the value is unavailable due to use of the unmodified default policy.
     */
    private boolean checkAccessValue(String propertyName, Object valueToTest){
        Object theValue = accessProps.get(propertyName);
        if(theValue == null){
            // If the value is not available, it means the value has never been changed from the default policy.
            // For our module, the default policy values allow wrapper download without restriction. So return true here.
            return true;
        }

        return theValue.equals(valueToTest);
    }

    public void setAccessValue(String propertyName, Object value){
        accessProps.put(propertyName, value);
    }

    /**
     * Returns the Mode for the dynamic property with the passed name.
     * @param dynamicPropName
     * @return
     */
    public Mode getDynamicPropMode(String dynamicPropName){
        return dynamicProps.getOrDefault(dynamicPropName, DEFAULT_MODE);
    }

    public void setDynamicProp(String propName, Mode mode){
        dynamicProps.put(propName, mode);
    }


    /**
     * Check the access key sent from the remote machine. If the access key does not match the one saved in the
     * Service Security settings, deny access.
     * @param sentKey
     */
    public void checkAccessKey(String sentKey){
        boolean accessGranted = checkAccessValue(GetLogsService.ACCESS_KEY, sentKey);
        if(!accessGranted){
            String msg = String.format("Sent access key '%s' did not match the configured service security key.", sentKey);
            throw new SecurityException(msg);
        }
    }

    /**
     * Checks the 'Allow wrapper log access' setting. If the setting has been disabled in Service Security settings, deny access.
     */
    public void checkAllowWrapperSetting(){
        boolean allowWrapper = checkAccessValue(GetLogsService.WRAPPER_ALLOWED_PROP, true);
        if(!allowWrapper){
            String msg = "'Allow wrapper log access' security setting is disabled. Wrapper log will not be sent.";
            throw new SecurityException(msg);
        }
    }

    /**
     * Verify that all of the dynamic properties are set to enabled. If any dynamic property is set to disabled,
     * throw a security exception and deny access.
     */
    public void checkDynamicProperties(){
        Mode propOneMode = getDynamicPropMode(GLSecurityDescriptorFactory.DYNAMIC_PROP_ID + "DynamicPropOne");
        if(Mode.Disabled == propOneMode){
            String msg = "DynamicPropOne mode is set to Disabled.  Wrapper log will not be sent.";
            throw new SecurityException(msg);
        }

        Mode propTwoMode = getDynamicPropMode(GLSecurityDescriptorFactory.DYNAMIC_PROP_ID + "DynamicPropTwo");
        if(Mode.Disabled == propTwoMode){
            String msg = "DynamicPropTwo mode is set to Disabled.  Wrapper log will not be sent.";
            throw new SecurityException(msg);
        }
    }
}
