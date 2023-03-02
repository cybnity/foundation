package org.cybnity.framework.domain.model.sample.writemodel;

import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.ISubscribable;
import org.cybnity.framework.domain.application.sample.UserAccountAggregate;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Persistence system (store) UserAccountAggregate.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public interface UserAccountStore extends ISubscribable {

    /**
     * Add object into the store.
     * 
     * @param version          Mandatory object to store.
     * @param transactionOrder Mandatory original transaction requesting the change.
     * @throws IllegalArgumentException When object to store is not compatible to be
     *                                  stored (e.g missing mandatory content into
     *                                  the instance to store).
     * @throws ImmutabilityException    When problem of immutable version of stored
     *                                  object is occurred.
     */
    public void append(UserAccountAggregate version, Command transactionOrder)
	    throws IllegalArgumentException, ImmutabilityException;

    /**
     * Search in store an event logged.
     * 
     * @param uid Mandatory identifier of the object to find.
     * @return Found last version of object, or null.
     */
    public UserAccountAggregate findFrom(String uid);

}
