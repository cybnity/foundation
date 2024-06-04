package org.cybnity.framework.domain;

import org.cybnity.framework.domain.model.ReadModelProjectionDescriptor;

/**
 * Represents an optimized read-model projection allowing query and read of denormalized version of domain layer object (e.g status and value of a domain object version at a moment of life).
 * This interface contract is covering a perimeter of read-model projection based on a type of denormalized domain object view.
 * Each projection specialized for a domain layer (e.g aggregate view) shall extend this type of contract to add specific methods supporting optimized queries for the type of domain object view managed.
 */
public interface IReadModelProjection {

    /**
     * Capture an event relative to a write model content change.
     * Represent a monitoring capability supporting the creation or change of an origin data interesting this read-model projection which shall maintain its up-to-date status.
     * Similar to an Aggregate instance, this read model is receiving and handling the events relative to domain layer and ensure the build of projection's state.
     * Read Model Projection is persisted after each update and can be accessed by many readers, both inside and outside its bounded context.
     *
     * @param evt Notified change regarding a subject potentially presenting an interest for this Read Model projection (e.g event store stream change).
     */
    public void when(DomainEvent evt);

    /**
     * Get description of this read-model projection.
     *
     * @return A description providing specification element regarding this projection (e.g label, ownership, categorization elements).
     */
    public ReadModelProjectionDescriptor description();
}
