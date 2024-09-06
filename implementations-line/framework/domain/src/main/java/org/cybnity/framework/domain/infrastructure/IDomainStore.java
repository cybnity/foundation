package org.cybnity.framework.domain.infrastructure;

import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.persistence.IFactStore;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Stream store (with an append-only approach) which maintain history of a type
 * of domain fact (e.g Aggregate versions).
 * <br>
 * For example, manage the domain data (e.g sharded database for a tenant)
 * ensuring isolation of persistent domain model from the other bounded
 * contexts.
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Robusteness, reqId = "REQ_ROB_3")
public interface IDomainStore<T> extends IFactStore<T> {

    /**
     * Add a fact into the store.
     *
     * @param fact To store. Ignored if null.
     * @param ctx  Optional context of persistence providing elements (e.g aggregate object id) usable for persistence management.
     * @throws IllegalArgumentException    When event to store is not compatible to be
     *                                     stored (e.g missing mandatory content into
     *                                     the event to store).
     * @throws ImmutabilityException       When problem of immutable version of stored
     *                                     event is occurred.
     * @throws UnoperationalStateException When technical problem is occurred regarding this store usage.
     */
    void append(T fact, IContext ctx) throws IllegalArgumentException, ImmutabilityException, UnoperationalStateException;

    /**
     * Search a fact from store.
     *
     * @param uid Identifier of the fact to find.
     * @param ctx Optional context of persistence providing elements (e.g tenant
     *            id) usable for persistence management.
     * @return Found fact or null.
     * @throws IllegalArgumentException    When missing mandatory parameter.
     * @throws UnoperationalStateException When technical problem is occurred regarding this store usage.
     */
    T findEventFrom(Identifier uid, IContext ctx) throws IllegalArgumentException, UnoperationalStateException;

}
