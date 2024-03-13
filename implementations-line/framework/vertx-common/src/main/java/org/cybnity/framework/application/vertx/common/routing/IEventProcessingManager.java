package org.cybnity.framework.application.vertx.common.routing;

/**
 * Processing management contract regarding one or several event types.
 */
public interface IEventProcessingManager {

    /**
     * Get the map of supported event types and processing supporters.
     *
     * @return A map.
     */
    public RouteRecipientList delegateDestinations();
}
