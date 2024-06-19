package org.cybnity.framework.application.vertx.common.routing;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

/**
 * Manager of routing services providing the routes exposed with health control capability.
 */
public interface HealthControllableRouter extends Router {

    /**
     * Create a router supporting health HTTP protocol.
     *
     * @param vertx a Vert.x instance.
     * @return A router including routes with SockJS handlers.
     */
    static Router httpHealthRouter(Vertx vertx) {
        return new HealthHTTPRouterImpl(vertx);
    }
}
