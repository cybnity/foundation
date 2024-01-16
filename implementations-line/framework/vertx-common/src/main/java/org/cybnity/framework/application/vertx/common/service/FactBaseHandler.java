package org.cybnity.framework.application.vertx.common.service;

import org.cybnity.framework.domain.IDescribed;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Basis handler of fact to process.
 * Shall be extended by any subclass realizing a treatment onto a fact event as element of the responsibility chain pattern.
 */
public abstract class FactBaseHandler implements FactHandler {

    /**
     * Eligible to call as next actor of responsibility chain.
     */
    private FactHandler next;

    /**
     * Technical logging
     */
    private final Logger logger;

    public FactBaseHandler() {
        // Init the logger
        logger = Logger.getLogger(this.getClass().getName());
    }

    /**
     * Get technical logger.
     *
     * @return A logger configured for this type of instance class.
     */
    protected Logger logger() {
        return this.logger;
    }

    @Override
    public void setNext(FactHandler next) {
        this.next = next;
    }

    /**
     * Detection of existing next handler and call of it if existing.
     * Default behavior of the handling process that only check and call the next handle when existing.
     *
     * @param fact To process.
     */
    @Override
    public void handle(IDescribed fact) {
        try {
            // Perform the processing of the fact
            if (process(fact)) {
                if (next != null) next.handle(fact);
            }
        } catch (Exception e) {
            logger().log(Level.WARNING, "Fact processing problem", e);
            // TODO feed errors into logs store according to type of exception (e.g technical, conformity)
        }
    }

    /**
     * Concrete realization of the event processing according to decision criteria.
     * This method shall evaluate if the fact need to be processed and/or if fact shall be forwarded to the next Responsibility Chain member.
     * This method is called by the handle(IDescribed fact) method.
     *
     * @param fact To process.
     * @return True when responsibility chain can be continued. Else false when chain shall be interrupted.
     */
    public abstract boolean process(IDescribed fact);

    /**
     * Check if the fact can be processed by this handler (evaluation based on specific fact description elements).
     *
     * @param fact Fact to identify.
     * @return False by default. True when the fact is supported and can be processed by this handler.
     */
    protected abstract boolean canHandle(IDescribed fact);
}
