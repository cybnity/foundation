package org.cybnity.framework.application.vertx.common.service;

import org.cybnity.infrastructure.technical.message_bus.adapter.api.Channel;

/**
 * Contract of configuration relative to a pipelined feature settings supporting its specificities.
 */
public interface ConfigurableFeature {

    /**
     * Get logical name of this feature module usable in logs.
     * @return A label.
     */
    public String featureModuleLogicalName();

    /**
     * Get the name of this feature service.
     * @return A label.
     */
    public String featureServiceName();

    /**
     * Get the channel where this feature can be announced as available and ready to process events.
     * @return A channel.
     */
    public Channel featureGatewayAnnouncingChannel();

    public Channel featureGatewayRoutingPlanChangesChannel();
}
