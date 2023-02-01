package org.cybnity.infrastructure.technical.message_bus.system.impl.redis;

import io.vertx.redis.client.RedisOptions;

/**
 * Factory of Redis adapter based on Vert.x Redis plugin.
 */
public class RedisOptionFactory {

	static private String defaultUserPassword = "PRg53nV82DQy";
	static private String serverHost = "localhost", serverPort = "6379", dbNumber = "1";

	public RedisOptionFactory() {
	}

	/**
	 * Create a configured set of options allowing to discuss with Users
	 * Interactions Space.
	 *
	 * param connectionUserAccount  Defined by users.acl file setting in Redis container.
	 * param connectionPassword
	 * @return An instance.
	 */
	static public RedisOptions createUsersInteractionsSpaceOptions(String connectionUserAccount, String connectionPassword) {
		return new RedisOptions().setConnectionString(new StringBuffer("redis://").append(connectionUserAccount)
				.append(":").append(connectionPassword).append("@").append(serverHost).append(":")
				.append(serverPort).append("/").append(dbNumber).toString()).setPassword(defaultUserPassword);
	}
}
