package org.cybnity.framework.domain.infrastructure;

import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.ICleanup;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.persistence.IFactRepository;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Represents a persistence-oriented repository (also sometimes called Aggregate
 * store, or Aggregate-Oriented database) basic contract for a bounded context.
 * <br>
 * For example, manage the domain data (e.g sharded database for a tenant)
 * ensuring isolation of persistent domain model from the other bounded
 * contexts.
 * <br>
 * A domain repository is optimized for management and query of domain Read-Model Projections.
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Robusteness, reqId = "REQ_ROB_3")
public interface IDomainRepository<T> extends IFactRepository<T>, ICleanup {

    /**
     * Get a next technical identity manageable by this repository.
     *
     * @param ctx Mandatory context of persistence providing elements (e.g tenant
     *            id) usable for persistence management.
     * @return A technical identifier.
     */
    public T nextIdentity(IContext ctx);

    /**
     * Find a historical fact identified.
     *
     * @param aFactId An identifier of fact to search.
     * @param ctx     Optional context of persistence providing elements (e.g
     *                tenant id) usable for persistence management.
     * @return Found fact or null.
     */
    public T factOfId(Identifier aFactId, IContext ctx);

    /**
     * Delete a fact from this repository.
     *
     * @param fact Instance to remove.
     * @param ctx  Optional context of persistence providing elements (e.g tenant
     *             id) usable for persistence management.
     * @return True if previous existent item was found and was removed from this
     * repository. False if none previous fact found and removed.
     */
    public boolean remove(T fact, IContext ctx);

    /**
     * Delete a collection of facts from this repository.
     *
     * @param aFactsCollection Facts list to remove.
     * @param ctx              Optional context of persistence providing elements
     *                         (e.g tenant id) usable for persistence management.
     */
    public void removeAll(Collection<T> aFactsCollection, IContext ctx);

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
    public T save(T aFact, IContext ctx);

    /**
     * Save a collection of facts into this repository.
     *
     * @param aFactCollection Facts to save.
     * @param ctx             Optional context of persistence providing elements
     *                        (e.g tenant id) usable for persistence management.
     */
    public void saveAll(Collection<T> aFactCollection, IContext ctx);

    /**
     * Find facts from specific parameters.
     *
     * @param queryParameters A set of parameters (e.g type of query to execute; parameters with values) as filtering criteria allowing the isolation of facts to retrieve.
     * @param ctx             Optional context of persistence layer usage.
     * @return A list of found result(s), or null.
     * @throws IllegalArgumentException      When any mandatory parameter (e.g unknown query name not provided by parameters list); when a required parameter's value is missing or is not valid (e.g not supported by the real query executed regarding a database data structure).
     * @throws UnsupportedOperationException When impossible execution of requested query.
     * @throws UnoperationalStateException   When query execution technical problem occurred.
     */
    public List<T> queryWhere(Map<String, String> queryParameters, IContext ctx) throws IllegalArgumentException, UnsupportedOperationException, UnoperationalStateException;

    /**
     * Get the name of the search criteria that can be evaluated to identify a query.
     * This information (e.g Command.TYPE) is generally added into each query parameters set that allow repository to identify query event types from domain's referential of queries supported.
     *
     * @return A query name based on query type (projection that support the query parameters and specific data path/structure).
     */
    public String queryNameBasedOn();
}
