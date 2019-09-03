package org.fakester.common;

import java.util.Set;

import com.google.common.collect.Sets;
import com.inductiveautomation.perspective.common.api.BrowserResource;

public class RadComponents {

    public static final String MODULE_ID = "org.fakester.radcomponents";
    public static final String URL_ALIAS = "radcomponents";
    public static final String COMPONENT_CATEGORY = "Rad Things";
    public static final Set<BrowserResource> BROWSER_RESOURCES =
        Sets.newHashSet(
            new BrowserResource(
                "rad-components-js",
                String.format("/res/%s/RadComponents.js", URL_ALIAS),
                BrowserResource.ResourceType.JS
            ),
            new BrowserResource("rad-components-css",
                String.format("/res/%s/RadComponents.css", URL_ALIAS),
                BrowserResource.ResourceType.CSS
            )
        );
}
