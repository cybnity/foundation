package org.cybnity.framework.domain;

import org.cybnity.framework.IContext;

/**
 * A service in a domain is a stateless operation that fulfills a
 * domain-specific task. Often the best indication that a Service is created in
 * a domain model is when the operation to perform feels out of place as a
 * method on an Aggregate or a Value Object.
 * <p>
 * Domain Service is usable to perform a significant business process, to
 * transform a domain object from one composition to another, or to calculate a
 * Value requiring input from more than one domain object.
 *
 * @author olivier
 */
public interface IService {

    /**
     * Manage a fact event.
     *
     * @param factEvent Mandatory event that can be treated.
     * @param ctx       Context providing resources which could be required for
     *                  event treatment.
     * @throws IllegalArgumentException When event parameter is null or when an
     *                                  event processing can't be performed for
     *                                  cause of invalidity (e.g missing
     *                                  required contents).
     */
    public void handle(IDescribed factEvent, IContext ctx) throws IllegalArgumentException;

}
