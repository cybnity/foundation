package org.cybnity.framework.application.vertx.common.service;

import org.cybnity.framework.domain.IDescribed;

/**
 * Service activator can be one-way (request only) or two-way (Request-Reply).
 * Part of the domain's Service Layer which handles all o the messaging details and invokes the component realizing the feature.
 * Activator can be as simple as a method call synchronous and non-remote; or can manage the integration with the Domain object over remote calls (e.g via space).
 * The Service Activator handles receiving the request message (as Polling Consumer or as Event-Driven Consumer).
 * It knows the message's format and processes the message to extract the information necessary to know what service to invoke and what parameter values to pass in.
 * When the service completes and returns a value (e.g listening of changed domain event), the activator can optionally create a reply message containing the value and return it to th requestor.
 */
public abstract class AbstractServiceActivator extends FactBaseHandler {

    /**
     * Callable during the processing step (implemented into the process() method) when this activator cannot process the message successfully.
     * When cause of non processing is problem of message entry condition (e.g quality, conformity issue, message that make no sense), the activator invalidate the message and move if into a dedicated Invalid Message Channel (pattern realization).
     *
     * @param unprocessedEvent Source event not processed with success.
     * @param cause            Optional cause of invalidity (e.g ConformityViolation.UNIDENTIFIED_EVENT_TYPE.name()).
     */
    abstract protected void moveToInvalidMessageChannel(IDescribed unprocessedEvent, String cause);

    /**
     * Callable during the processing step (implemented into the process() method) when this activator cannot process the message successfully.
     * When cause of non processing is an execution problem (e.g technical failure; impossible delivery to another system/features), the activator invalidate the message and move it into a Dead Letter Channel (pattern realization).
     *
     * @param unprocessedEvent Source event not processed with success.
     * @param cause            Optional cause of error.
     */
    abstract protected void moveToDeadLetterChannel(IDescribed unprocessedEvent, String cause);

}
