package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis;

import io.vertx.redis.client.RedisOptions;

/**
 * Factory of Redis adapter based on Vert.x Redis plugin.
 */
public class RedisOptionFactory {

    /**
     * Create a configured set of options allowing to discuss with Users
     * Interactions Space.
     *
     * @param connectionUserAccount Defined by users.acl file setting in Redis
     *                              container.
     * @param connectionPassword    Connection password.
     * @param serverHost            The server host ip address or machine name.
     * @param serverPort            The server port of Redis instance.
     * @param databaseNumber        The select database as per spec is the numerical
     *                              path of the URL.
     * @param defaultUserPassword   A password for cluster/sentinel connections.
     * @return An instance.
     */
    static public RedisOptions createUsersInteractionsSpaceOptions(String connectionUserAccount,
	    String connectionPassword, String serverHost, String serverPort, String databaseNumber,
	    String defaultUserPassword) {
	return new RedisOptions().setConnectionString(new StringBuffer("redis://").append(connectionUserAccount)
		.append(":").append(connectionPassword).append("@").append(serverHost).append(":").append(serverPort)
		.append("/").append(databaseNumber).toString()).setPassword(defaultUserPassword);
    }
}
