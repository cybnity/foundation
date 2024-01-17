package org.cybnity.framework.domain;

import org.cybnity.framework.INaming;
import org.cybnity.framework.domain.event.IAttribute;
import org.cybnity.framework.immutable.EntityReference;

/**
 * Capability regarding the presence life cycle management (e.g of a processing unit, of an application feature module) and allowing other assets (e.g system) to understand/identify a presence status.
 */
public interface IPresenceObservability {

    /**
     * Type of status regarding a presence.
     */
    public enum PresenceState implements IAttribute {
        /**
         * Status of active presence and materializing a "ready state" (e.g to deliver services).
         */
        AVAILABLE,

        /**
         * Status of no active presence that materialize a "not ready state" (e.g end of life cycle regarding a component that previously provided services when it was in available state).
         */
        UNAVAILABLE;
    }

    /**
     * Prepare and publish a presence event (e.g over a collaboration space) allowing to other component(s) to detect presence changes of the observable subject.
     *
     * @param currentStatus Mandatory status of presence to announce.
     * @param priorEventRef Optional origin event (e.g request of announce renewal received from another component) that was prior to new event to generate and to publish.
     * @throws Exception When problem during the event preparation or publication.
     */
    public void announcePresence(PresenceState currentStatus, EntityReference priorEventRef) throws Exception;
}
