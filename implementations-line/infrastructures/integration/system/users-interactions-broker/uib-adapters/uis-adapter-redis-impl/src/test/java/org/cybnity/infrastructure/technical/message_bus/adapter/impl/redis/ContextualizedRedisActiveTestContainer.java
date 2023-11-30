package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis;

import org.cybnity.framework.Context;
import org.cybnity.framework.IContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import redis.embedded.RedisServer;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import java.util.HashMap;
import java.util.Map;

/**
 * Auto configuration and start of Redis container usable during a test execution.
 * Each unit test requiring a redis container started shall extend this class.
 * EmbeddedRedisExtension.class for Redis 6.0.5 used by default
 **/
@ExtendWith({SystemStubsExtension.class})
public class ContextualizedRedisActiveTestContainer {

    /**
     * Current started process' environment variables.
     */
    @SystemStub
    private EnvironmentVariables environmentVariables;

    private RedisServer redisServer;

    /**
     * Redis server auth password.
     * Read Redis Kubernetes configuration's REDISCLI_AUTH environment variable
     */
    static String DEFAULT_AUTH_PASSWORD = "1gEGHneiLT";
    /**
     * System address
     */
    static String SERVER_HOST = "localhost";
    /**
     * Default port
     */
    static int SERVER_PORT = 6379;
    /**
     * Default user account declared on Redis server.
     */
    static String CONNECTION_USER_ACCOUNT = "default";
    /**
     * Default first db number
     */
    static String DATABASE_NUMBER = "1";
    static Map<String, String> serverEnvironmentVariables = new HashMap<>();

    /**
     * Added code to a static code block, so that it runs before the dependencies are injected, and tests are run
     * Start Redis container version equals to the Helmized system of Users Interactions Space project
     * registry: docker.io
     * repository: bitnami/redis
     * tag: 7.0.8-debian-11-r0
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
                WriteModelConfigurationVariable.REDIS_WRITEMODEL_CONNECTION_DEFAULT_AUTH_PASSWORD.getName(),
                DEFAULT_AUTH_PASSWORD);
        environmentVariables.set(
                WriteModelConfigurationVariable.REDIS_WRITEMODEL_CONNECTION_DEFAULT_USERACCOUNT.getName(),
                CONNECTION_USER_ACCOUNT);
        environmentVariables.set(WriteModelConfigurationVariable.REDIS_WRITEMODEL_DATABASE_NUMBER.getName(),
                DATABASE_NUMBER);
        environmentVariables.set(WriteModelConfigurationVariable.REDIS_WRITEMODEL_SERVER_HOST.getName(), SERVER_HOST);
        environmentVariables.set(WriteModelConfigurationVariable.REDIS_WRITEMODEL_SERVER_PORT.getName(), Integer.toString(SERVER_PORT));
        // Variables regarding read model
        environmentVariables.set(
                ReadModelConfigurationVariable.REDIS_READMODEL_CONNECTION_DEFAULT_AUTH_PASSWORD.getName(),
                DEFAULT_AUTH_PASSWORD);
        environmentVariables.set(
                ReadModelConfigurationVariable.REDIS_READMODEL_CONNECTION_DEFAULT_USERACCOUNT.getName(),
                CONNECTION_USER_ACCOUNT);
        environmentVariables.set(ReadModelConfigurationVariable.REDIS_READMODEL_DATABASE_NUMBER.getName(),
                DATABASE_NUMBER);
        environmentVariables.set(ReadModelConfigurationVariable.REDIS_READMODEL_SERVER_HOST.getName(), SERVER_HOST);
        environmentVariables.set(ReadModelConfigurationVariable.REDIS_READMODEL_SERVER_PORT.getName(), Integer.toString(SERVER_PORT));
    }

    @BeforeEach
    public void initRedisConnectionChainValues() {
        // Build reusable context
        this.ctx = new Context();
        ctx.addResource(DEFAULT_AUTH_PASSWORD, "defaultAuthPassword", false);
        ctx.addResource(SERVER_HOST, "serverHost", false);
        ctx.addResource(Integer.toString(SERVER_PORT), "serverPort", false);
        ctx.addResource(DATABASE_NUMBER, "databaseNumber", false);
        ctx.addResource(CONNECTION_USER_ACCOUNT, "connectionUserAccount", false);
        ctx.addResource(DEFAULT_AUTH_PASSWORD, "connectionPassword", false);
        // Synchronize environment variables test values
        initEnvVariables();
        // Start Redis instance
        // See https://redis.io/docs/management/config-file/ for more detail about supported start options
        redisServer = RedisServer.builder().port(SERVER_PORT)
                .setting("bind 127.0.0.1 -::1") // good for local development on Windows to prevent security popups
                //.slaveOf(SERVER_HOST, 6378)
                .setting("daemonize no")
                .setting("masteruser " + CONNECTION_USER_ACCOUNT)
                .setting("masterauth " + DEFAULT_AUTH_PASSWORD)
                //.setting("databases " + DATABASE_NUMBER)
                //.setting("appendonly no")
                //.setting("maxmemory 128M")
                .build();
        redisServer.start();
        //System.out.println("Redis test server started");
    }

    @AfterEach
    public void cleanValues() {
        redisServer.stop();
        //System.out.println("Redis test server stopped");
        ctx = null;
    }

}
