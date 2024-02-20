package org.cybnity.framework.domain.infrastructure;

import org.cybnity.framework.domain.ISessionContext;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.persistence.IFactRepository;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.util.Collection;

/**
 * Represents a persistence-oriented repository (also sometimes called Aggregate
 * store, or Aggregate-Oriented database) basic contract for a bounded context.
 * <p>
 * For example, manage the domain data (e.g sharded database for a tenant)
 * ensuring isolation of persistent domain model from the other bounded
 * contexts.
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Robusteness, reqId = "REQ_ROB_3")
public interface IDomainRepository<T> extends IFactRepository<T> {

    /**
     * Get a next technical identity manageable by this repository.
     *
     * @param ctx Mandatory context of persistence providing elements (e.g tenant
     *            id) usable for persistence management.
     * @return A technical identifier.
     */
    public T nextIdentity(ISessionContext ctx);

    /**
     * Find a historical fact identified.
     *
     * @param aFactId An identifier of fact to search.
     * @param ctx     Optional context of persistence providing elements (e.g
     *                tenant id) usable for persistence management.
     * @return Found fact or null.
     */
    public T factOfId(Identifier aFactId, ISessionContext ctx);

    /**
     * Delete a fact from this repository.
     *
     * @param fact Instance to remove.
     * @param ctx  Optional context of persistence providing elements (e.g tenant
     *             id) usable for persistence management.
     * @return True if previous existent item was found and was removed from this
     * repository. False if none previous fact found and removed.
     */
    public boolean remove(T fact, ISessionContext ctx);

    /**
     * Delete a collection of facts from this repository.
     *
     * @param aFactCollection Facts list to remove.
     * @param ctx             Optional context of persistence providing elements
     *                        (e.g tenant id) usable for persistence management.
     */
    public void removeAll(Collection<T> aFactCollection, ISessionContext ctx);

    /**
     * Save an instance of fact into this repository.
     *
     * @param aFact To save. Ignored if null.
     * @param ctx   Optional context of persistence providing elements (e.g tenant
     *              id) usable for persistence management.
     * @return The technical identifier assigned to the saved fact (e.g
     * auto-generated if new one created for the saved instance). Null if no
     * saved fact.
     */
    public T save(T aFact, ISessionContext ctx);

    /**
     * Save a collection of facts into this repository.
     *
     * @param aFactCollection Facts to save.
     * @param ctx             Optional context of persistence providing elements
     *                        (e.g tenant id) usable for persistence management.
     */
    public void saveAll(Collection<T> aFactCollection, ISessionContext ctx);
}
