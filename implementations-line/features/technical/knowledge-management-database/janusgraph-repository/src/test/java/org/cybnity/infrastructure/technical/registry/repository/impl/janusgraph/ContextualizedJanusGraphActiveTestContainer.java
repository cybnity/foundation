package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph;

import org.cybnity.framework.Context;
import org.cybnity.framework.IContext;
import org.cybnity.infrastructure.technical.registry.adapter.impl.janusgraph.ReadModelConfigurationVariable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import java.util.logging.Logger;

/**
 * Autoconfiguration and start of JanusGraph container usable during a test execution.
 * Each unit test requiring a redis container started shall extend this class.
 **/
@ExtendWith({SystemStubsExtension.class})
public class ContextualizedJanusGraphActiveTestContainer {

    /**
     * Utility logger
     */
    protected Logger logger;

    /**
     * Current started process' environment variables.
     */
    @SystemStub
    protected EnvironmentVariables environmentVariables;

    /**
     * In-memory storage backend.
     */
    public static final String STORAGE_BACKEND_TYPE = "inmemory";

    /**
     * Added code to a static code block, so that it runs before the dependencies are injected, and tests are run
     * Start JanusGraph container version simulate the Helmized system of Knowledge Repository.
     **/
    private IContext ctx;

    /**
     * Get test context.
     *
     * @return A context instance including environment variable names and values.
     */
    protected IContext context() {
        return this.ctx;
    }

    @BeforeEach
    public void initJanusGraphConnectionChainValues() {
        logger = Logger.getLogger(this.getClass().getName());
        // Build reusable context
        this.ctx = new Context();
        ctx.addResource(STORAGE_BACKEND_TYPE, ReadModelConfigurationVariable.JANUSGRAPH_STORAGE_BACKEND.getName(), false);

        // Synchronize environment variables test values
        initEnvVariables();
    }

    /**
     * Define current environment variables simulating their setting on the executed system host.
     */
    protected void initEnvVariables() {
        if (environmentVariables != null) {
            // Define environment variables regarding write model
            environmentVariables.set(
                    ReadModelConfigurationVariable.JANUSGRAPH_STORAGE_BACKEND.getName(),
                    STORAGE_BACKEND_TYPE);
        }
    }

    /**
     * Get new instance of context.
     *
     * @return A context including environment variables.
     */
    public static IContext getContextInstance() {
        ContextualizedJanusGraphActiveTestContainer c = new ContextualizedJanusGraphActiveTestContainer();
        c.initJanusGraphConnectionChainValues();
        return c.context();
    }

    @AfterEach
    public void cleanValues() {
        environmentVariables = null;
        ctx = null;
    }

}
