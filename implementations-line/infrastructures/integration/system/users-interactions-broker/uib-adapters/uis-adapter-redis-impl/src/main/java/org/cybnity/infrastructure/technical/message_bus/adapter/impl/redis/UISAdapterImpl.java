package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis;

import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.UISAdapter;

/**
 * Implementation class of the adapter client to the Users Interactions Space
 * provided via a Redis server. It's a client library supporting the
 * interactions with the Redis solution, and that encapsulate the utility
 * components (e.g specific Lettuce Redis client implementation).
 * 
 * @author olivier
 *
 */
public class UISAdapterImpl implements UISAdapter {

    /**
     * Current context of adapter runtime.
     */
    private IContext context;

    /**
     * Utility class managing the verification of operable adapter instance.
     */
    private ExecutableAdapterChecker healthyChecker;

    /**
     * Default constructor of the adapter ready to manage interactions with the
     * Redis instance(s).
     * 
     * @param context Mandatory context provider of reusable configuration allowing
     *                to connect to Redis instance(s).
     * @throws IllegalArgumentException    When mandatory parameter is missing.
     * @throws UnoperationalStateException When any required environment variable is
     *                                     not defined or have not value ready for
     *                                     use.
     */
    public UISAdapterImpl(IContext context) throws IllegalArgumentException, UnoperationalStateException {
	if (context == null)
	    throw new IllegalArgumentException("Context parameter is required!");
	this.context = context;
	// Check the minimum required data allowing connection to the targeted Redis
	// server
	checkHealthyState();
    }

    @Override
    public void checkHealthyState() throws UnoperationalStateException {
	if (healthyChecker == null)
	    healthyChecker = new ExecutableAdapterChecker(context);
	// execution the health check
	healthyChecker.checkOperableState();
    }

}
