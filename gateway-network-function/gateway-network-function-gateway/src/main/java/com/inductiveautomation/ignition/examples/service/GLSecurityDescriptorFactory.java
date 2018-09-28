package com.inductiveautomation.ignition.examples.service;

import java.util.ArrayList;
import java.util.List;

import com.inductiveautomation.ignition.common.config.BasicDescriptiveProperty;
import com.inductiveautomation.ignition.common.config.DescriptiveProperty;
import com.inductiveautomation.ignition.common.i18n.LocalizedString;
import com.inductiveautomation.ignition.examples.service.GLSecurityConfigValues.Mode;
import com.inductiveautomation.ignition.gateway.gan.security.SecuredEntity.ConfigPropDescriptionFactory;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

/**
 * Created by mattgross on 9/26/2016. In this class, we set up all the security properties that can later be configured
 * on the Service Security page in the Gateway. The properties will also be added to a new GLSecurityConfigValues object which
 * is created in GLSecurityConfigFactory.create(). The GLSecurityConfigValues object will later be used by methods in GetLogsService
 * to allow or deny access.
 */
public class GLSecurityDescriptorFactory implements ConfigPropDescriptionFactory {

    public final static DescriptiveProperty<Boolean> WRAPPER_ALLOWED = new BasicDescriptiveProperty<Boolean>(
            GetLogsService.WRAPPER_ALLOWED_PROP,
            "remotelogging.services.security.wrapper.retrieval",
            "",
            "remotelogging.services.security.wrapper.retrieval.desc",
            Boolean.class,
            Boolean.TRUE);

    public static final DescriptiveProperty<String> ACCESS_KEY = new BasicDescriptiveProperty<String>(
            GetLogsService.ACCESS_KEY,
            "remotelogging.services.security.wrapper.accesskey",
            "",
            "remotelogging.services.security.wrapper.accesskey.desc",
            String.class,
            "");

    public static final DescriptiveProperty<Mode> CUSTOM_PROP_TEMPLATE = new BasicDescriptiveProperty<>(
            "", "", null, Mode.class, Mode.Enabled);

    public static final String DYNAMIC_PROP_ID = "dynamic_prop:";

    @Override
    public List<DescriptiveProperty<?>> getProperties(GatewayContext gatewayContext) {
        List<DescriptiveProperty<?>> ret = new ArrayList<>();
        ret.add(WRAPPER_ALLOWED);
        ret.add(ACCESS_KEY);

        // Set up some dynamic properties. These show how to build properties directly from code.
        BasicDescriptiveProperty<Mode> customPropOne = new BasicDescriptiveProperty<>(
                CUSTOM_PROP_TEMPLATE);
        customPropOne.setName(DYNAMIC_PROP_ID + "DynamicPropOne");
        customPropOne.setDefaultValue(Mode.Enabled);
        customPropOne.setDisplayName(new LocalizedString("remotelogging.services.security.dynamicprop.name", "One"));
        ret.add(customPropOne);

        BasicDescriptiveProperty<Mode> customPropTwo = new BasicDescriptiveProperty<>(
                CUSTOM_PROP_TEMPLATE);
        customPropTwo.setName(DYNAMIC_PROP_ID + "DynamicPropTwo");
        customPropTwo.setDefaultValue(Mode.Enabled);
        customPropTwo.setDisplayName(new LocalizedString("remotelogging.services.security.dynamicprop.name", "Two"));
        ret.add(customPropTwo);

        return ret;
    }
}
