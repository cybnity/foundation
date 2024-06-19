package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis;

import org.cybnity.infrastructure.technical.message_bus.adapter.api.StreamObserver;

/**
 * Basic implementation class providing common services.
 */
public abstract class StreamObserverImpl implements StreamObserver {

    /**
     * Implementation method verifying the observer's stream name and observation pattern when defined.
     *
     * @param obj To check.
     * @return True when Stream names are equals and pattern are equals when they are defined.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof StreamObserver) {
            StreamObserver other = (StreamObserver) obj;
            boolean equalsObservedStreamName = this.observed().name().equals(other.observed().name());
            boolean equalsPattern = false;
            if (this.observationPattern() != null && other.observationPattern() != null) {
                equalsPattern = this.observationPattern().equals(other.observationPattern());
            } else if (this.observationPattern() == null && other.observationPattern() == null) {
                equalsPattern = true;
            }
            return equalsObservedStreamName && equalsPattern;
        }
        return false;
    }

}
