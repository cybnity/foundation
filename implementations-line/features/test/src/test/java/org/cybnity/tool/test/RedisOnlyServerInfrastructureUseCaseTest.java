package org.cybnity.tool.test;

import org.cybnity.framework.domain.infrastructure.ISnapshotRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import redis.embedded.RedisServer;

/**
 * Unit test validating the usage of the InfrastructureContextualizedTest utility class like could be done by a project reusing this facility.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class RedisOnlyServerInfrastructureUseCaseTest extends InfrastructureContextualizedTest {

    /**
     * Example of default constructor statically defining the configuration of the super instance.
     */
    public RedisOnlyServerInfrastructureUseCaseTest() {
        // Define a unit test context requiring started Redis only
        super(true, false, false, false, /* snapshot repo configuration desired */ false);
    }

    /**
     * Unit test that get each contextualized server prepared instance, and try an operation for check of operational status.
     *
     * @throws Exception When any server access or use problem is occurred.
     */
    @Test
    public void givenStartedServers_whenUsed_thenOperationalStateConfirmed() throws Exception {
        // Verification that context instance have been prepared
        Assertions.assertNotNull(context(), "Shall have been prepared before any test execution!");
        // Verify from sample of environment variables that super class defined them as expected
        DefinedEnvVariableVerificationRule.verifyEnvironmentVariablesDefinedForContext(context(), true, false, false, false);

        // Verification of unavailable snapshot repository prepared for test context
        ISnapshotRepository snapRepo = getSnaphotRepository();
        Assertions.assertNull(snapRepo, "Shall have not been prepared!");

        // Attempt creation of a Redis adapter connected to the Redis server
        RedisServer redis = getRedisServer();
        Assertions.assertNotNull(redis, "Shall have been prepared and started before any test execution!");
        // Test usage of server
        Assertions.assertTrue(redis.isActive(), "Shall be activated because normally started for the context!");
        Assertions.assertTrue(redis.ports().contains(InfrastructureContextualizedTest.REDIS_SERVER_PORT), "Redis server shall have been started on static contextualized port to be usable over adapter!");

        // Attempt read of started Keycloak server
        GenericContainer<?> keycloak = getKeycloak();
        Assertions.assertNull(keycloak, "Shall have not been prepared!");
    }
}
