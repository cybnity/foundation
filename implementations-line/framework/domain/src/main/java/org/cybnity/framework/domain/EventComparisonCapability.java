package org.cybnity.framework.domain;

import org.cybnity.framework.immutable.Evaluations;
import org.cybnity.framework.immutable.IdentifiableFact;
import org.cybnity.framework.immutable.ImmutabilityException;

/**
 * Utility class providing comparison capability.
 */
public class EventComparisonCapability {

    /**
     * Compare equality of an event with a command other event.
     *
     * @param event To compare.
     * @param to    Compared with.
     * @return False by default. True when same reference, or when event is a IdentifiableFact instance and the two instance identifiers are same.
     * @throws ImmutabilityException When impossible usage of immutable version of any event internal attribute (e.g identifier).
     */
    public boolean isEquals(Object event, IdentifiableFact to) throws ImmutabilityException {
        if (event == to)
            return true;
        if (event != null && IdentifiableFact.class.isAssignableFrom(event.getClass())) {
            // Compare equality based on each instance's identifier (unique or based on
            // identifying information combination)
            return Evaluations.isIdentifiedEquals(to, (IdentifiableFact) event);
        }
        return false;
    }
}
