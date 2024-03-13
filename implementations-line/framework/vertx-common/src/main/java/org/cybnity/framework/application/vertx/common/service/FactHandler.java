package org.cybnity.framework.application.vertx.common.service;

import org.cybnity.framework.domain.IDescribed;

/**
 * Fact event handler that decide to process a fact and/or to continue the call of next Responsibility Chain contributors.
 * It's an element of the Responsibility Chain design pattern.
 */
public interface FactHandler {

    /**
     * Define a next handler callable
     *
     * @param processor Performer of a fact registered as next processing unit which can be executed after the realization of this fact handling.
     */
    public void setNext(FactHandler processor);

    /**
     * Perform the treatment of the fact is fact supported by this handler.
     * If existing next handler, call the next handler after the optionally processed fact.
     *
     * @param fact To process.
     */
    public void handle(IDescribed fact);
}
