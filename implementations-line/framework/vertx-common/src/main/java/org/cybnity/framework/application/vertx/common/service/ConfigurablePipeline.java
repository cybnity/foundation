package org.cybnity.framework.application.vertx.common.service;

import org.cybnity.infrastructure.technical.message_bus.adapter.api.Channel;

/**
 * Contract of configuration relative to a pipelined capability settings supporting its specificities.
 */
public interface ConfigurablePipeline {

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
     * Get the channel where this pipeline can be announced as available and ready to process events.
     * @return A channel or null.
     */
    public Channel proxyAnnouncingChannel();

    /**
     * Get the channel where a proxy allowing to forward event to this pipeline make notifications about its events routing paths changes.
     * @return A channel.
     */
    public Channel proxyRoutingPlanChangesChannel();
}
