package org.cybnity.framework.application.vertx.common.service.security;

import org.cybnity.framework.application.vertx.common.event.AttributeName;
import org.cybnity.framework.application.vertx.common.service.FactBaseHandler;
import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.ConformityViolation;
import org.cybnity.framework.domain.IDescribed;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.Stream;

import java.util.Collection;
import java.util.logging.Level;

/**
 * Anti-Corruption Layer (ACL) chain's element that apply the access control check relative to a function execution request (e.g capability, feature, process) relative to an event type.
 * This verification service identify the public or reserved (e.g secure capability with SSO check required and based on credentials provided into the fact event to process) state of capability to execute and apply the security controls when required via verification of the fact embedded credential.
 * It's a detection of need security checks to apply for an entrypoint received entries.
 */
public class AccessControlChecker extends FactBaseHandler {

    /**
     * Entrypoint name under optional access control check.
     */
    private final Stream receivedFrom;

    /**
     * Referential of criteria that allow (e.g by evaluation as filtering pattern) to select or not the event as supported.
     * Each item is defined by an event type name (key) and a recipient path (e.g value identifying a topic/stream name).
     */
    private Collection<String> eventTypeNamesUnderAccessControl;

    /**
     * Current handled event type name.
     */
    private String detectedFactEventTypeName;

    /**
     * Default constructor.
     *
     * @param receivedFrom                     Mandatory entrypoint of collecting events to filter.
     * @param eventTypeNamesUnderAccessControl Optional list of event types under access control. It's static referential of command or domain event types that require security check.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public AccessControlChecker(Stream receivedFrom, Collection<String> eventTypeNamesUnderAccessControl) throws IllegalArgumentException {
        super();
        if (receivedFrom == null) throw new IllegalArgumentException("ReceivedFrom parameter is required!");
        this.receivedFrom = receivedFrom;
        this.eventTypeNamesUnderAccessControl = eventTypeNamesUnderAccessControl;
    }

    @Override
    public boolean process(IDescribed fact) {
        if (canHandle(fact)) {
            // Identify if the detected event type name shall be treated according to access control verification
            if (this.eventTypeNamesUnderAccessControl != null && this.eventTypeNamesUnderAccessControl.contains(detectedFactEventTypeName)) {
                // --- THE CAPABILITY TO PERFORM IS UNDER SECURED ACCESS ---
                // Access control credential identification for security check
                String credentialValue = null;
                for (Attribute eventSpecification : fact.specification()) {
                    // Identify credential attribute from event specification
                    if (AttributeName.AccessToken.name().equalsIgnoreCase(eventSpecification.name())) {
                        credentialValue = eventSpecification.value();
                        if (credentialValue != null && !credentialValue.isEmpty()) {
                            // Stop credential search from event specification attributes
                            break;
                        }
                    }
                }

                if (credentialValue != null && !credentialValue.isEmpty()) {
                    // Execute the credential validity check (e.g SSO access token verification)
                    // TODO code the JWT validation with/without Keycloak contribution to validate the authorized/rejected status of capability usage

                    // Authorization confirmed for capability usage
                }

                // Denied usage of the capability
                // Interrupt next step activation and notify reject
                return false;
            }

            // Capability to perform is public
            return true; // Confirm next step activation
        } else { // Invalid fact event type received
            // Several potential cause can be managed regarding this situation in terms of security violation
            // For example:
            // - development error of command transmission to the right stream
            // - security attack attempt with bad command send test through any channel for test of entry by any capability api entry point
            logger().log(Level.SEVERE, ConformityViolation.UNIDENTIFIED_EVENT_TYPE.name() + ": invalid fact type received from channel (" + receivedFrom.name() + ")!");
        }
        return false; // Interrupt next step activation
    }

    @Override
    protected boolean canHandle(IDescribed fact) {
        detectedFactEventTypeName = null; // Clean temp container
        if (fact != null) {
            // Identify event type
            Attribute eventType = fact.type();
            detectedFactEventTypeName = (eventType != null && !eventType.value().isEmpty()) ? eventType.value() : null;
        }
        return (detectedFactEventTypeName != null);
    }
}
