package org.cybnity.framework.domain.event;

import org.cybnity.framework.immutable.Identifier;

import java.time.OffsetDateTime;

/**
 * Providing of specific specification attributes allowing re-hydration of a subject that have been changed.
 * This capability allow to change event the dynamic collect of mandatory or optional value objects (e.g domain object identifiers, entity reference of predecessor fact) from a change event.
 */
public interface HydrationAttributeProvider {

    /**
     * Get an identifier regarding a predecessor entity of the object which is eligible to re-hydration.
     *
     * @return Identifier or child fact's parent or null.
     */
    public Identifier changeSourcePredecessorReferenceId();

    /**
     * Set the identifier regarding a predecessor entity of the object which is eligible to re-hydration.
     *
     * @param id An identifier.
     */
    public void setChangeSourcePredecessorReferenceId(Identifier id);

    /**
     * Get an identifier defining the object that is eligible to re-hydration.
     *
     * @return Identifier or null.
     */
    public Identifier changeSourceIdentifier();

    /**
     * Set the identifier defining the object that is eligible to re-hydration.
     *
     * @param id Identifier.
     */
    public void setChangeSourceIdentifier(Identifier id);

    /**
     * Origin date of creation of object that is eligible to re-hydration.
     *
     * @return A date.
     */
    public OffsetDateTime changeSourceOccurredAt();

    /**
     * Set the origin date of the object that is eligible to re-hydration.
     *
     * @param date A date (e.g origin creation date).
     */
    public void setChangeSourceOccurredAt(OffsetDateTime date);
}
