package org.cybnity.framework.application.vertx.common.service.filter;

import org.cybnity.framework.application.vertx.common.service.FactBaseHandler;
import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.ConformityViolation;
import org.cybnity.framework.domain.IDescribed;
import org.cybnity.framework.domain.event.IEventType;

import java.util.Collection;
import java.util.logging.Level;

/**
 * The goal of this type o handler is to eliminate undesired message from a channel based on a set of criteria.
 * When the content type of a message (e.g Command event type) is source of interest for the component using this filter, this filter confirm interest.
 */
public class InterestEventFilter extends FactBaseHandler {

    /**
     * Reference to the collection of interest sources.
     */
    private final Collection<IEventType> sourcesOfInterest;

    /**
     * Current handled event type name.
     */
    private String detectedFactEventTypeName;

    /**
     * Default constructor.
     *
     * @param sourcesOfInterest Mandatory set of event types that are considered as interest source by this filter.
     */
    public InterestEventFilter(Collection<IEventType> sourcesOfInterest) throws IllegalArgumentException {
        if (sourcesOfInterest == null) throw new IllegalArgumentException("sourcesOfInterest parameter is required!");
        this.sourcesOfInterest = sourcesOfInterest;
    }

    /**
     * Perform the evaluation of the fact to determine if it shall be ignored (e.g because identified as source of interest).
     * This realization analyze the event content (e.g supported event type name) and identify received event as supported by the capability domain.
     *
     * @param fact To filter.
     * @return True when event identified as source of interest. Else return false.
     */
    @Override
    public boolean process(IDescribed fact) {
        if (canHandle(fact)) {
            // Identify event type
            if (detectedFactEventTypeName != null) {
                // Check if event is source of interest
                for (IEventType soi :
                        sourcesOfInterest) {
                    // Evaluate if equals type name
                    if (soi.name().equals(detectedFactEventTypeName)) {
                        return true; // Confirm fact is identified source of interest
                    }
                }
            } else {
                String errorMsg = ConformityViolation.UNIDENTIFIED_EVENT_TYPE.name() +
                        ": null fact type filtered as not source of interest and ignored!";
                // Invalid structure of received event
                logger().log(Level.SEVERE, errorMsg);
            }
        } else {
            // Invalid fact event type received
            // Several potential cause can be managed regarding this situation in terms of security violation
            // For example:
            // - development error of command transmission to the right stream
            // - security attack attempt with bad command send test through any channel for test of entry by any capability api entry point
            logger().log(Level.SEVERE, ConformityViolation.UNIDENTIFIED_EVENT_TYPE.name() + ": invalid fact type which can be source of filtering!");
        }

        // Event is filtered because not considered as interesting
        return false; // Default out of interest regarding unidentifiable event type
    }

    /**
     * Check that is defined fact.
     *
     * @param fact Fact to identify.
     * @return True when fact parameter is not null.
     */
    @Override
    protected boolean canHandle(IDescribed fact) {
        detectedFactEventTypeName = null; // Clean temp container
        if (fact != null) {
            // Identify event type
            Attribute eventType = fact.type();
            detectedFactEventTypeName = (eventType != null && !eventType.value().isEmpty()) ? eventType.value() : null;
        }
        return (detectedFactEventTypeName != null && !detectedFactEventTypeName.isEmpty());
    }
}
