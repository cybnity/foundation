package org.cybnity.framework.application.vertx.common.data;


import org.cybnity.infrastructure.technical.message_bus.adapter.api.ICapabilityChannel;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.NamingConventions;

/**
 * Channel sample regarding a Capabilities Domain exposed as an entry point via the
 * User Interactions Space.
 * Each channel name shall be a action capability (e.g registration, deletion).
 */
public enum UICapabilityChannelSample implements ICapabilityChannel {

    /**
     * Boundary regarding the capabilities of Access Control domain IO entrypoint.
     */
    access_control_in("ac" + NamingConventions.STREAM_NAME_SEPARATOR + "in"),

    /**
     * Boundary regarding the feature managing an organization registration.
     */
    access_control_organization_registration("ac" + NamingConventions.STREAM_NAME_SEPARATOR + "organization_registration"),

    /**
     * Boundary regarding the feature managing the processing unit presence announces (e.g input channel for any PU declaration as eligible delegate responsible for treatment of specific event types).
     * Pub/sub channel allowing listening by multiple consumers.
     */
    access_control_pu_presence_announcing("ac" + NamingConventions.CHANNEL_NAME_SEPARATOR + "pu_presence_announcing"),

    /**
     * Boundary regarding the promotion of the dynamic recipients list changes that are managed by a domain IO Gateway about registered routing paths.
     * Pub/sub channel allowing listening by multiple consumers.
     */
    access_control_io_gateway_dynamic_routing_plan_evolution("ac" + NamingConventions.CHANNEL_NAME_SEPARATOR + "io_gateway_routing_plan_evolution"),
    ;

    private final String shortName;

    private UICapabilityChannelSample(String acronym) {
        this.shortName = acronym;
    }

    /**
     * Get the short label (e.g acronym) regarding this channel.
     *
     * @return An acronym or null.
     */
    @Override
    public String shortName() {
        return this.shortName;
    }
}
