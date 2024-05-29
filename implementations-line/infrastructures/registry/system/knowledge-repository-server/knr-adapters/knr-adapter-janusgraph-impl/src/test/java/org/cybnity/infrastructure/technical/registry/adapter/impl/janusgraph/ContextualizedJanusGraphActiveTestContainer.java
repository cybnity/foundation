package org.cybnity.infrastructure.technical.registry.adapter.impl.janusgraph;

import org.cybnity.framework.Context;
import org.cybnity.framework.IContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

/**
 * Auto configuration and start of JanusGraph container usable during a test execution.
 * Each unit test requiring a redis container started shall extend this class.
 **/
@ExtendWith({SystemStubsExtension.class})
public class ContextualizedJanusGraphActiveTestContainer {

    /**
     * Current started process' environment variables.
     */
    @SystemStub
    private EnvironmentVariables environmentVariables;

    /**
     * In-memory storage backend.
     */
    static String STORAGE_BACKEND_TYPE = "inmemory";

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
    protected IContext getContext() {
        return this.ctx;
    }

    /**
     * Define current environment variables simulating their setting on the executed system host.
     */
    public void initEnvVariables() {
        // Define environment variables regarding write model
        environmentVariables.set(
                ConfigurationVariable.JANUSGRAPH_STORAGE_BACKEND_TYPE.getName(),
                STORAGE_BACKEND_TYPE);
    }

    @BeforeEach
    public void initJanusGraphConnectionChainValues() {
        // Build reusable context
        this.ctx = new Context();
        ctx.addResource(STORAGE_BACKEND_TYPE, ConfigurationVariable.JANUSGRAPH_STORAGE_BACKEND_TYPE.getName(), false);

        // Synchronize environment variables test values
        initEnvVariables();
    }

    @AfterEach
    public void cleanValues() {
        ctx = null;
    }

}
