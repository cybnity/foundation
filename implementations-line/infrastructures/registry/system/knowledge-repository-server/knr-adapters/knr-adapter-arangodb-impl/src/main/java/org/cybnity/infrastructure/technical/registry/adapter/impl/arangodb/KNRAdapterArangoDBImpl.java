package org.cybnity.infrastructure.technical.registry.adapter.impl.arangodb;

import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.infrastructure.technical.registry.adapter.api.KNRAdapter;

import java.util.logging.Logger;

/**
 * Implementation class of the adapter client to the Knowledge Repository
 * provided via a ArangoDB server. It's a client library supporting the
 * interactions with the ArangoDB solution, and that encapsulate the utility
 * components (e.g specific ArangoDB Java Driver client implementation).
 * <p>
 * For help about ArangoDB client usage, see <a href="https://docs.arangodb.com/stable/develop/drivers/java/reference-version-7/">Java Driver documentation</a>
 *
 * @author olivier
 */
public class KNRAdapterArangoDBImpl implements KNRAdapter {
    /**
     * Technical logging
     */
    private static final Logger logger = Logger.getLogger(KNRAdapterArangoDBImpl.class.getName());

    /**
     * Current context of adapter runtime.
     */
    private final IContext context;


    /**
     * Utility class managing the verification of operable adapter instance.
     */
    private ExecutableAdapterChecker healthyChecker;

    /**
     * Default constructor of the adapter ready to manage interactions with the
     * ArangoDB instance(s).
     *
     * @param context Mandatory context provider of reusable configuration allowing
     *                to connect to ArangoDB instance(s).
     * @throws IllegalArgumentException    When mandatory parameter is missing.
     * @throws UnoperationalStateException When any required environment variable is
     *                                     not defined or have not value ready for
     *                                     use.
     */
    public KNRAdapterArangoDBImpl(IContext context) throws IllegalArgumentException, UnoperationalStateException {
        if (context == null)
            throw new IllegalArgumentException("Context parameter is required!");
        this.context = context;

        // Check the minimum required data allowing connection to the targeted Redis
        // server
        checkHealthyState();
    }

    @Override
    public void freeUpResources() {
        //try {
        // Remove any existing listeners managed by this client

        // Disconnect client from database

        // Remove singleton instance which is not usable (to force potential singleton re-instantiation in case of getClient() future call)
        /*} catch (UnoperationalStateException e) {
            logger.severe(e.getMessage());
        }*/
    }

    @Override
    public void checkHealthyState() throws UnoperationalStateException {
        if (healthyChecker == null)
            healthyChecker = new ExecutableAdapterChecker(context);
        // Execution the health check
        healthyChecker.checkOperableState();
    }
}
