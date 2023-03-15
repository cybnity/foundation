package org.cybnity.framework.immutable.persistence;

import org.cybnity.framework.immutable.Identifier;
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
     */
    public void append(T fact);

    /**
     * Search a fact from store.
     * 
     * @param uid Identifier of the fact to find.
     * @return Found fact or null.
     */
    public T findEventFrom(Identifier uid);

}
