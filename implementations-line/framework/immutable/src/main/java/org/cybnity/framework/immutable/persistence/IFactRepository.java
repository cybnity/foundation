package org.cybnity.framework.immutable.persistence;

import java.util.Collection;

import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represents a persistence-oriented repository (also sometimes called Aggregate
 * store, or Aggregate-Oriented database) basic contract.
 * 
 * Explicit save() both new and changed objects into the store, effectively
 * replacing any value previously associated with the given key.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Robusteness, reqId = "REQ_ROB_3")
public interface IFactRepository<T> {

    /**
     * Get a next technical identity manageable by this repository.
     * 
     * @return A technical identifier.
     */
    public T nexIdentity();

    /**
     * Find a historical fact identified.
     * 
     * @param aFactId A identifier of fact to search.
     * @return Found fact or null.
     */
    public T factOfId(Identifier aFactId);

    /**
     * Delete a fact from this repository.
     * 
     * @param fact Instance to remove.
     * @return True if previous existent item was found and was removed from this
     *         repository. Fals if none previous fact found and removed.
     */
    public boolean remove(T fact);

    /**
     * Delete a collection of facts from this repository.
     * 
     * @param aFactCollection Facts list to remove.
     */
    public void removeAll(Collection<T> aFactCollection);

    /**
     * Save an instance of fact into this repository.
     * 
     * @param aFact To save. Ignored if null.
     * @return The technical identifier assigned to the saved fact (e.g
     *         auto-generated if new one created for the saved instance). Null if no
     *         saved fact.
     */
    public T save(T aFact);

    /**
     * Save a collection of facts into this repository.
     * 
     * @param aFactCollection Facts to save.
     */
    public void saveAll(Collection<T> aFactCollection);
}
