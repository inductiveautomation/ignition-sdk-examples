package org.fakester.common;

import java.util.Set;

import com.google.common.collect.Sets;
import com.inductiveautomation.perspective.common.api.BrowserResource;

public class RadComponents {

    public static final String MODULE_ID = "org.fakester.radcomponents";
    public static final String URL_ALIAS = "radcomponents";
    public static final Set<BrowserResource> BROWSER_RESOURCES =
        Sets.newHashSet(
            new BrowserResource(
            "rad-components",
            String.format("/res/%s/js/RadComponents.js", URL_ALIAS),
            BrowserResource.ResourceType.JS)
        );
}
