package org.cybnity.tool.test;

import org.cybnity.framework.IContext;
import org.cybnity.framework.application.vertx.common.AppConfigurationVariable;
import org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.WriteModelConfigurationVariable;
import org.junit.jupiter.api.Assertions;

/**
 * Utility class verifying by sampling approach that system environment variables are defined.
 */
public class DefinedEnvVariableVerificationRule {

    /**
     * Verify that environment variables have been defined for the expected server started
     * and should be usable by the Unit Test (e.g via adapters).
     *
     * @param ctx             Mandatory context to evaluate.
     * @param redisCheck      To check.
     * @param janusGraphCheck To check.
     * @param keycloakCheck   To check.
     * @param gatewayCheck    To check.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    static public void verifyEnvironmentVariablesDefinedForContext(IContext ctx, boolean redisCheck, boolean janusGraphCheck, boolean keycloakCheck, boolean gatewayCheck) throws IllegalArgumentException {
        if (redisCheck) {
            // Search a Redis variable (sampling)
            Assertions.assertNotNull(ctx.get(WriteModelConfigurationVariable.REDISCLI_AUTH), "Should be retrieved by context from the system current env!"); // from enum
            Assertions.assertNotNull(ctx.get(WriteModelConfigurationVariable.REDISCLI_AUTH.getName()), "Should be retrieved by context from the system current env!"); // from resource name
        }
        if (janusGraphCheck) {
            // Search a JanusGraph variable (sampling)
            Assertions.assertNotNull(ctx.get(org.cybnity.infrastructure.technical.registry.adapter.impl.janusgraph.ReadModelConfigurationVariable.JANUSGRAPH_STORAGE_BACKEND), "Should be retrieved by context from the system current env!"); // from enum
            Assertions.assertNotNull(ctx.get(org.cybnity.infrastructure.technical.registry.adapter.impl.janusgraph.ReadModelConfigurationVariable.JANUSGRAPH_STORAGE_BACKEND.getName()), "Should be retrieved by context from the system current env!"); // from resource name
        }
        if (keycloakCheck) {
            // Search a Keycloak variable (sampling)
            // None currently defined
        }
        if (gatewayCheck) {
            // Search a Gateway variable (sampling)
            Assertions.assertNotNull(ctx.get(AppConfigurationVariable.DOMAIN_WORKER_INSTANCES), "Should be retrieved by context from the system current env!"); // from enum
            Assertions.assertNotNull(ctx.get(AppConfigurationVariable.DOMAIN_WORKER_INSTANCES.getName()), "Should be retrieved by context from the system current env!"); // from resource name
        }
    }

}
