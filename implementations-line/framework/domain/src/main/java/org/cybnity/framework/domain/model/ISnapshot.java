package org.cybnity.framework.domain.model;

import org.cybnity.framework.immutable.Unmodifiable;

import java.util.Date;

/**
 * Represent a specific state of value regarding a subject (e.g full state aggregate instance).
 * This contract can be implemented by Aggregate children class that are persistent and shall propose snapshot capability for re-hydration optimization.
 */
public interface ISnapshot extends Unmodifiable {

    /**
     * Date when this snapshot have been taken.
     *
     * @return A date.
     */
    Date taken();

    /**
     * Identifier that represents the snapshot version based on last change event of the origin object that is subject of this snapshot.
     * @return Value defining unique version.
     */
    String commitVersion();

    /**
     * Unique identifier of the origin object which is subject of this snapshot.
     *
     * @return An identifier (e.g aggregate identifier) or null.
     */
    String versionedObjectUID();
}
