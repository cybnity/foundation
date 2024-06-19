package org.cybnity.framework.immutable.persistence;

import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Stream store (with an append-only approach) which maintain history of a type
 * of fact (e.g Aggregate versions).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Robusteness, reqId = "REQ_ROB_3")
public interface IFactStore<T> {

    /**
     * Add a fact into the store.
     * 
     * @param fact To store.Ignored if null.
     * @throws IllegalArgumentException    When fact to store is not compatible to be stored.
     * @throws ImmutabilityException       When problem of immutable version of stored event is occurred.
     * @throws UnoperationalStateException When technical problem is occurred regarding this store usage.
     */
    void append(T fact) throws IllegalArgumentException, ImmutabilityException, UnoperationalStateException;

    /**
     * Search a fact from store.
     * 
     * @param uid Identifier of the fact to find.
     * @return Found fact or null.
     * @throws IllegalArgumentException    When missing mandatory parameter.
     * @throws UnoperationalStateException When technical problem is occurred regarding this store usage.
     */
    T findEventFrom(Identifier uid) throws IllegalArgumentException, UnoperationalStateException;

}
