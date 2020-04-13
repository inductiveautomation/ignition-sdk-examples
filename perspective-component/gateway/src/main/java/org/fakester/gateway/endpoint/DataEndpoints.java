package org.fakester.gateway.endpoint;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.http.HttpServletResponse;

import com.inductiveautomation.ignition.common.gson.JsonObject;
import com.inductiveautomation.ignition.common.tags.model.TagProvider;
import com.inductiveautomation.ignition.common.tags.model.TagProviderInformation;
import com.inductiveautomation.ignition.common.util.Futures;
import com.inductiveautomation.ignition.gateway.dataroutes.RequestContext;
import com.inductiveautomation.ignition.gateway.dataroutes.RouteGroup;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.tags.model.GatewayTagManager;


/**
 * Class containing dynamic data 'routes' or 'endpoints'.
 */
public final class DataEndpoints {
    // how long to hold cached value before allowing new count calculation
    private static final long CACHE_DURATION_MS = Long.getLong("RadComponents.TagCounter.CacheDuration", 30000L);
    private static AtomicReference<Long> tagCount = new AtomicReference<>(0L);
    private static AtomicReference<Long> timestampOfLastCount = new AtomicReference<>(0L);

    private DataEndpoints() {
        // private constructor
    }

    public static void mountRoutes(RouteGroup routes) {
        // creates a new data route reachable at host:port/main/data/radcomppnents/component/tagcount
        routes.newRoute("/component/tagcount")
            .type(RouteGroup.TYPE_JSON)
            .handler(DataEndpoints::fetchTagCount)
            .mount();
    }

    /**
     * Returns a simple json object in form of
     * <pre>
     *     {
     *         "tagCount": &lt;number&gt;
     *     }
     * </pre>
     *
     * Tag counts are cached and re-caclcated no more often than {@link DataEndpoints#CACHE_DURATION_MS}.
     *
     * Note: an 'ideal' implementation of this sort would require some additional concurrency handling for optimal
     * performance and safety, but was skipped to keep example focused on big picture functionality.
     */
    private static JsonObject fetchTagCount(RequestContext req, HttpServletResponse res) {
        // if we've exceeded our throttle duration, update count
        if (System.currentTimeMillis() - timestampOfLastCount.get() > CACHE_DURATION_MS) {
            timestampOfLastCount.set(System.currentTimeMillis());

            GatewayContext context = req.getGatewayContext();
            GatewayTagManager tagManager = context.getTagManager();
            List<TagProvider> providers = tagManager.getTagProviders();

            long count = providers.stream()
                .map(TagProvider::getStatusInformation)
                .map(Futures::getSafe)
                .filter(Objects::nonNull)
                .mapToLong(TagProviderInformation::getTagCount)
                .sum();

            tagCount.set(count);
        }

        JsonObject json = new JsonObject();
        json.addProperty("tagCount", tagCount.get());
        return json;
    }
}
