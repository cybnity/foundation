package org.cybnity.tool.test;

import org.cybnity.framework.Context;
import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.application.vertx.common.AppConfigurationVariable;
import org.cybnity.framework.domain.ISessionContext;
import org.cybnity.framework.domain.infrastructure.ISnapshotRepository;
import org.cybnity.framework.domain.model.SessionContext;
import org.cybnity.infastructure.technical.persistence.store.impl.redis.SnapshotRepositoryRedisImpl;
import org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.ReadModelConfigurationVariable;
import org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.WriteModelConfigurationVariable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.GenericContainer;
import redis.embedded.RedisServer;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import java.util.logging.Logger;

/**
 * Generic helped about unit test contextualized with environment variables.
 * <p>
 * Automatic configuration and start of Redis, JanusGraph, Keycloak servers usable during a test execution according to unit test instantiation configuration.
 * Each unit test requiring optionally a Redis and/or JanusGraph and/or Keycloak server started, shall extend this class.
 */
@ExtendWith({SystemStubsExtension.class})
public class InfrastructureContextualizedTest {

    private ISnapshotRepository snapshotsRepo;

    /**
     * Utility logger
     */
    protected Logger logger;

    /**
     * Test context.
     */
    private IContext context;

    /**
     * Current started process' environment variables.
     */
    @SystemStub
    protected EnvironmentVariables environmentVariables;

    static protected Integer GATEWAY_HTTP_SERVER_PORT = 8080;
    static protected int WORKER_INSTANCES = 1;
    static protected int WORKER_THREAD_POOL = 1;

    /**
     * Redis instance optionally started.
     */
    protected RedisServer redisServer;

    /**
     * Keycloak instance optionally started.
     */
    private GenericContainer<?> keycloak;

    /**
     * Redis server auth password.
     * Read Redis Kubernetes configuration's REDISCLI_AUTH environment variable
     */
    static protected String REDIS_DEFAULT_AUTH_PASSWORD = "1gEGHneiLT";

    /**
     * Redis server address.
     */
    static protected String REDIS_SERVER_HOST = "localhost";

    /**
     * Default port of Redis.
     */
    static protected int REDIS_SERVER_PORT = 6379;

    /**
     * Default user account declared on Redis server.
     */
    static protected String REDIS_CONNECTION_USER_ACCOUNT = "default";

    /**
     * Default first db number of Redis.
     */
    static protected String REDIS_DATABASE_NUMBER = "1";

    /**
     * In-memory storage backend of JanusGraph server.
     */
    static public final String JANUSGRAPH_STORAGE_BACKEND_TYPE = "inmemory";

    private ISessionContext sessionCtx;

    private final boolean activeRedis;
    private final boolean activeJanusGraph;
    private final boolean activeKeycloak;

    /**
     * True if Keycloak existing instance shall be stopped after each test execution.
     */
    private final boolean stopKeycloakAfterEach;

    /**
     * True when snapshots usage shall be configured.
     */
    private final boolean supportedBySnapshotRepository;

    /**
     * Default constructor.
     *
     * @param withRedis                     True when Redis embedded server shall be started.
     * @param withJanusGraph                True when JanusGraph embedded server shall be started.
     * @param withKeycloak                  True when Keycloak embedded server shall be started.
     * @param stopKeycloakAfterEach         True if Keycloak server instance shall be stopped after each test execution. False when none stop shall be performed.
     * @param supportedBySnapshotRepository True when snapshots usage shall be configured for potential reuse by a store.
     */
    public InfrastructureContextualizedTest(boolean withRedis, boolean withJanusGraph, boolean withKeycloak, boolean stopKeycloakAfterEach, boolean supportedBySnapshotRepository) {
        // Define each desired services
        this.activeRedis = withRedis;
        this.activeJanusGraph = withJanusGraph;
        this.activeKeycloak = withKeycloak;
        this.stopKeycloakAfterEach = stopKeycloakAfterEach;
        this.supportedBySnapshotRepository = supportedBySnapshotRepository;
    }

    /**
     * Default constructor with Keycloak instance (when desired by parameter) not stopped after each unit test execution.
     *
     * @param withRedis                     True when Redis embedded server shall be started.
     * @param withJanusGraph                True when JanusGraph embedded server shall be started.
     * @param withKeycloak                  True when Keycloak embedded server shall be started.
     * @param supportedBySnapshotRepository True when snapshots usage shall be configured for potential reuse by a store.
     */
    public InfrastructureContextualizedTest(boolean withRedis, boolean withJanusGraph, boolean withKeycloak, boolean supportedBySnapshotRepository) {
        this(withRedis, withJanusGraph, withKeycloak, /* reuse same Keycloak instance by default without stop after each unit test execution */ false, supportedBySnapshotRepository);
    }

    @BeforeEach
    final public void initServices() {
        // Initialize shared data and configurations
        logger = Logger.getLogger(this.getClass().getName());
        // Build reusable context
        this.context = new Context();
        this.sessionCtx = new SessionContext(null);

        if (this.activeJanusGraph)
            // Set JanusGraph repository environment
            setJanusGraphServer(this.context);

        // Synchronize all environment variables test values
        initEnvVariables();

        if (this.activeRedis)
            // Set Redis server environment
            setRedisServer();

        if (this.activeKeycloak) // Set Keycloak server environment
            setKeycloakServer();
    }

    /**
     * Define runtime environment variable sets relative to each server.
     */
    private void initEnvVariables() {
        if (this.activeJanusGraph)
            initJanusGraphEnvVariables();
        if (this.activeRedis)
            initRedisEnvVariables();
        if (this.activeKeycloak)
            initKeycloakEnvVariables();
        initGatewayVariables();
    }

    /**
     * Start Keycloak server instance.
     */
    private void setKeycloakServer() {
        // Get server image ready for start (singleton instance)
        boolean reusableActivation = !stopKeycloakAfterEach; // Inverse of reuse requested by this class constructor
        keycloak = SSOTestContainer.getKeycloakContainer(reusableActivation);
        // Start the Keycloak server if not already running
        SSOTestContainer.start(keycloak);
        // Check finalized initial start
        Assertions.assertTrue(keycloak.isRunning(), "Shall have been started via command environment variable!");
    }

    /**
     * Start Redis server instance.
     */
    private void setRedisServer() {
        // Start Redis instance (EmbeddedRedisExtension.class for Redis 6.0.5 used by default)
        // See https://redis.io/docs/management/config-file/ for more detail about supported start options
        redisServer = RedisServer.builder().port(REDIS_SERVER_PORT)
                .setting("bind 127.0.0.1 -::1") // good for local development on Windows to prevent security popups
                //.slaveOf(SERVER_HOST, 6378)
                .setting("daemonize no")
                .setting("masteruser " + REDIS_CONNECTION_USER_ACCOUNT)
                .setting("masterauth " + REDIS_DEFAULT_AUTH_PASSWORD)
                //.setting("databases " + DATABASE_NUMBER)
                //.setting("appendonly no")
                //.setting("maxmemory 128M")
                .build();
        // Start redis server usable by worker
        redisServer.start();
    }

    /**
     * Prepare JanusGraph server's specific required resources (e.g backend type).
     */
    private void setJanusGraphServer(IContext context) {
        // Set configuration resources required by JanusGraph server
        context.addResource(JANUSGRAPH_STORAGE_BACKEND_TYPE, org.cybnity.infrastructure.technical.registry.adapter.impl.janusgraph.ReadModelConfigurationVariable.JANUSGRAPH_STORAGE_BACKEND.getName(), false);
    }

    /**
     * Define common variable required by Keycloak server to start.
     * Take care to call it in a subclass method if this method is redefined.
     */
    protected void initKeycloakEnvVariables() {
        if (environmentVariables != null) {
            // Define environment variables regarding server initialization
        }
    }

    /**
     * Define common variable required by JanusGraph server to start.
     * Take care to call it in a subclass method if this method is redefined.
     */
    protected void initJanusGraphEnvVariables() {
        if (environmentVariables != null) {
            // Define environment variables regarding server initialization
            environmentVariables.set(
                    org.cybnity.infrastructure.technical.registry.adapter.impl.janusgraph.ReadModelConfigurationVariable.JANUSGRAPH_STORAGE_BACKEND.getName(),
                    JANUSGRAPH_STORAGE_BACKEND_TYPE);
        }
    }

    /**
     * Define common variable required by Redis server to start.
     * Take care to call it in a subclass method if this method is redefined.
     */
    protected void initRedisEnvVariables() {
        if (environmentVariables != null) {
            // Define environment variables regarding write model
            environmentVariables.set(
                    WriteModelConfigurationVariable.REDIS_WRITEMODEL_CONNECTION_DEFAULT_AUTH_PASSWORD.getName(),
                    REDIS_DEFAULT_AUTH_PASSWORD);
            environmentVariables.set(
                    WriteModelConfigurationVariable.REDIS_WRITEMODEL_CONNECTION_DEFAULT_USERACCOUNT.getName(),
                    REDIS_CONNECTION_USER_ACCOUNT);
            environmentVariables.set(WriteModelConfigurationVariable.REDIS_WRITEMODEL_DATABASE_NUMBER.getName(),
                    REDIS_DATABASE_NUMBER);
            environmentVariables.set(WriteModelConfigurationVariable.REDIS_WRITEMODEL_SERVER_HOST.getName(), REDIS_SERVER_HOST);
            environmentVariables.set(WriteModelConfigurationVariable.REDIS_WRITEMODEL_SERVER_PORT.getName(), Integer.toString(REDIS_SERVER_PORT));

            // Variables regarding read model
            environmentVariables.set(
                    ReadModelConfigurationVariable.REDIS_READMODEL_CONNECTION_DEFAULT_AUTH_PASSWORD.getName(),
                    REDIS_DEFAULT_AUTH_PASSWORD);
            environmentVariables.set(
                    ReadModelConfigurationVariable.REDIS_READMODEL_CONNECTION_DEFAULT_USERACCOUNT.getName(),
                    REDIS_CONNECTION_USER_ACCOUNT);
            environmentVariables.set(ReadModelConfigurationVariable.REDIS_READMODEL_DATABASE_NUMBER.getName(),
                    REDIS_DATABASE_NUMBER);
            environmentVariables.set(ReadModelConfigurationVariable.REDIS_READMODEL_SERVER_HOST.getName(), REDIS_SERVER_HOST);
            environmentVariables.set(ReadModelConfigurationVariable.REDIS_READMODEL_SERVER_PORT.getName(), Integer.toString(REDIS_SERVER_PORT));
        }
    }

    /**
     * Define common variable required by Gateway server to start.
     * Take care to call it in a subclass method if this method is redefined.
     */
    protected void initGatewayVariables() {
        // Define additional environment variables regarding gateway
        environmentVariables.set(
                AppConfigurationVariable.ENDPOINT_HTTP_SERVER_PORT.getName(),
                GATEWAY_HTTP_SERVER_PORT);
        environmentVariables.set(AppConfigurationVariable.DOMAIN_WORKER_THREAD_POOL_SIZE.getName(), WORKER_THREAD_POOL);
        environmentVariables.set(AppConfigurationVariable.DOMAIN_WORKER_INSTANCES.getName(), WORKER_INSTANCES);

        // Define workers environment variables
    }

    /**
     * Be care full that when a Keycloak instance have been started, it is not automatically stopped after each unit test execution.
     * A reusable Keycloak instance previously started (according to this class's constructor defined parameter's value), shall be manually stopped by any subclass of this contextualized test.
     */
    @AfterEach
    public void cleanValues() {
        if (snapshotsRepo != null) {
            try {
                snapshotsRepo.freeUpResources();
            } catch (Exception ignored) {
            }
        }
        snapshotsRepo = null;
        if (this.activeRedis) {
            try {
                // Stop redis server used by worker
                redisServer.stop();
            } catch (Exception ignored) {
            }
        }
        if (this.stopKeycloakAfterEach) {
            if (getKeycloak() != null) {
                try {
                    // Stop keycloak server because shall not be reused by next test
                    SSOTestContainer.stop(getKeycloak());
                } catch (Exception ignored) {
                }
            }
        }
        this.environmentVariables = null;
        this.redisServer = null;
        this.keycloak = null;
        context = null;
        logger = null;
    }

    /**
     * Get instance of snapshots repository if defined/started.
     *
     * @return An instance or null (when support by snapshot repository have not been desired by this test's constructor parameter).
     * @throws UnoperationalStateException When snapshots repo based on Redis can't be instantiated.
     */
    protected ISnapshotRepository getSnaphotRepository() throws UnoperationalStateException {
        if (snapshotsRepo == null) {
            // Instantiate snapshot repo instance
            snapshotsRepo = (supportedBySnapshotRepository) ? new SnapshotRepositoryRedisImpl(context()) : null;
        }
        return this.snapshotsRepo;
    }

    /**
     * Get test context.
     *
     * @return A context instance including environment variable names and values.
     */
    final protected IContext context() {
        return this.context;
    }


    /**
     * Get started Keycloak instance.
     *
     * @return Instance or null (when none started according to the test's constructor execution defined).
     */
    final protected GenericContainer<?> getKeycloak() {
        return this.keycloak;
    }

    /**
     * Get the started Redis server as defined by the constructor parameter.
     *
     * @return A server instance ready for use. Or null.
     */
    final protected RedisServer getRedisServer() {
        return this.redisServer;
    }

    /**
     * Get session context.
     *
     * @return A session context instance created for this test scope.
     */
    final protected ISessionContext sessionContext() {
        return this.sessionCtx;
    }

}
