package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis;

import org.cybnity.framework.IContext;

import io.vertx.redis.client.RedisOptions;

/**
 * Factory of Redis adapter based on Vert.x Redis plugin.
 */
public class RedisOptionFactory {

    /**
     * Create a configured set of options allowing to discuss with Users
     * Interactions Space.
     *
     * @param connectionUserAccount Mandatory account name defined by users.acl file
     *                              setting in Redis container.
     * @param connectionPassword    Mandatory connection password.
     * @param serverHost            Mandatory server host ip address or machine
     *                              name.
     * @param serverPort            Mandatory server port of Redis instance.
     * @param databaseNumber        Mandatory select database as per spec is the
     *                              numerical path of the URL.
     * @param defaultUserPassword   Mandatory password for cluster/sentinel
     *                              connections.
     * @return An instance.
     * @throws IllegalArgumentException When any mandatory parameter is missing or
     *                                  undefined.
     */
    static public RedisOptions createUsersInteractionsSpaceOptions(String connectionUserAccount,
	    String connectionPassword, String serverHost, String serverPort, String databaseNumber,
	    String defaultUserPassword) throws IllegalArgumentException {
	if (connectionUserAccount == null || "".equalsIgnoreCase(connectionUserAccount))
	    throw new IllegalArgumentException("connectionUserAccount parameter is required!");
	if (connectionPassword == null || "".equalsIgnoreCase(connectionPassword))
	    throw new IllegalArgumentException("connectionPassword parameter is required!");
	if (serverHost == null || "".equalsIgnoreCase(serverHost))
	    throw new IllegalArgumentException("serverHost parameter is required!");
	if (serverPort == null || "".equalsIgnoreCase(serverPort))
	    throw new IllegalArgumentException("serverPort parameter is required!");
	if (databaseNumber == null || "".equalsIgnoreCase(databaseNumber))
	    throw new IllegalArgumentException("databaseNumber parameter is required!");
	if (defaultUserPassword == null || "".equalsIgnoreCase(defaultUserPassword))
	    throw new IllegalArgumentException("defaultUserPassword parameter is required!");
	return new RedisOptions().setConnectionString(new StringBuffer("redis://").append(connectionUserAccount)
		.append(":").append(connectionPassword).append("@").append(serverHost).append(":").append(serverPort)
		.append("/").append(databaseNumber).toString()).setPassword(defaultUserPassword);
    }

    /**
     * Create a default set of options allowing to discuss with Users Interactions
     * Space based on default user account and authorization password defined by the
     * environment variables available into the current runtime context.
     * 
     * @param ctx Mandatory context to use for environment variables read.
     * @return An instance.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     * @throws SecurityException        If a security manager exists and its
     *                                  {@link SecurityManager#checkPermission
     *                                  checkPermission} method doesn't allow access
     *                                  to any environment variable required by the
     *                                  Redis adapter.
     * @throws Exception                When any variable environment required for
     *                                  space connection is missing.
     */
    static public RedisOptions createUsersInteractionsWriteModelSpaceOptions(IContext ctx)
	    throws IllegalArgumentException, SecurityException, Exception {
	if (ctx == null)
	    throw new IllegalArgumentException("ctx parameter is required!");
	return createUsersInteractionsSpaceOptions(
		ctx.get(WriteModelConfigurationVariable.REDIS_WRITEMODEL_CONNECTION_DEFAULT_USERACCOUNT),
		ctx.get(WriteModelConfigurationVariable.REDIS_WRITEMODEL_CONNECTION_DEFAULT_AUTH_PASSWORD),
		ctx.get(WriteModelConfigurationVariable.REDIS_WRITEMODEL_SERVER_HOST),
		ctx.get(WriteModelConfigurationVariable.REDIS_WRITEMODEL_SERVER_PORT),
		ctx.get(WriteModelConfigurationVariable.REDIS_WRITEMODEL_DATABASE_NUMBER),
		ctx.get(WriteModelConfigurationVariable.REDIS_WRITEMODEL_CONNECTION_DEFAULT_AUTH_PASSWORD));
	// Any undefined environment variable that is not found durign the context
	// reading and that generate an IllegalArgumentException from the
	// createUsersInteractionsSpaceOptions(...) method call
    }
}
