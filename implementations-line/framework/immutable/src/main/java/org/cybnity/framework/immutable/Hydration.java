package org.cybnity.framework.immutable;

import java.time.OffsetDateTime;
import java.util.ArrayList;

/**
 * Hydration contract allowing restoration of an instance from its event changes sourced history.
 */
public interface Hydration {
    /**
     * Get the entity which is predecessor of the child fact.
     *
     * @return A parent entity as owner of the child fact.
     */
    public Entity parent();

    /**
     * Get the date of occurrence.
     *
     * @return A data.
     */
    public OffsetDateTime occurredAt();

    /**
     * Get the fact event representing the change historized.
     *
     * @return A fact (e.g creation, deletion, update event relative to the instance).
     */
    public IdentifiableFact event();

    /**
     * Get the list of identifiers which are the basic of unique identifier of the child fact.
     *
     * @return A list of identifier relative to the child fact.
     */
    public ArrayList<Identifier> identifiedBy();
}
