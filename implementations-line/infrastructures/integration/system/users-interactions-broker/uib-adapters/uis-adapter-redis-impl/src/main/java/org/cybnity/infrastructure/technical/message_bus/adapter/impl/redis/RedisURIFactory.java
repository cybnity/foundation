package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis;

import io.lettuce.core.RedisURI;
import org.cybnity.framework.IContext;

/**
 * Factory of Redis uri.
 */
public class RedisURIFactory {

    /**
     * Build a URI allowing connection to write model of Users Interactions Space.
     *
     * @param ctx Mandatory provider of configuration elements.
     * @return an URI.
     * @throws IllegalArgumentException When mandatory parameter is missing, or when uri build exception.
     */
    static public RedisURI createUISWriteModelURI(IContext ctx) throws IllegalArgumentException {
        if (ctx == null) throw new IllegalArgumentException("ctx parameter is required!");
        try {
            RedisURI.Builder builder = RedisURI.Builder
                    .redis(ctx.get(WriteModelConfigurationVariable.REDIS_WRITEMODEL_SERVER_HOST), Integer.parseInt(ctx.get(WriteModelConfigurationVariable.REDIS_WRITEMODEL_SERVER_PORT)));

            return builder
                    .withAuthentication(/* username*/ ctx.get(WriteModelConfigurationVariable.REDIS_WRITEMODEL_CONNECTION_DEFAULT_USERACCOUNT), /* password */ctx.get(WriteModelConfigurationVariable.REDISCLI_AUTH))
                    .withDatabase(Integer.parseInt(ctx.get(WriteModelConfigurationVariable.REDIS_WRITEMODEL_DATABASE_NUMBER)))
                    .withHost(ctx.get(WriteModelConfigurationVariable.REDIS_WRITEMODEL_SERVER_HOST))
                    .withPort(Integer.parseInt(ctx.get(WriteModelConfigurationVariable.REDIS_WRITEMODEL_SERVER_PORT)))
                    .build();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
