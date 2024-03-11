package io.ia.example.perspective.min.common;

import java.util.Set;
import com.inductiveautomation.perspective.common.api.BrowserResource;


/**
 * This class acts as a central location to hold module and component metadata.
 */
public class OneComponent {

    public static final String MODULE_ID = "io.ia.example.perspective.min";

    // this URL alias needs to be consistent with the alias chosen in the GatewayHook's
    public static final String URL_ALIAS = "onecomponent";
    public static final String COMPONENT_CATEGORY = "OneComponent";

    /**
     * The client-side (browser) dependencies that are required for our components to function in the browser. In this
     * case we have a single javascript file.  Production component modules will often have more than one file, each
     * of which should have a unique BrowserResource 'name'.
     */
    public static final Set<BrowserResource> BROWSER_RESOURCES =
        Set.of(
            new BrowserResource(
                "one-components-js",
                String.format("/res/%s/js/onecomponent.js", URL_ALIAS),
                BrowserResource.ResourceType.JS
            )
        );
}
