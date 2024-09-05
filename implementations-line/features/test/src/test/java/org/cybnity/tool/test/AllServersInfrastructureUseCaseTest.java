package org.cybnity.tool.test;

import org.cybnity.framework.IContext;
import org.cybnity.framework.application.vertx.common.AppConfigurationVariable;
import org.cybnity.framework.domain.infrastructure.ISnapshotRepository;
import org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.WriteModelConfigurationVariable;
import org.cybnity.tool.test.sample.SampleDomainGraphImpl;
import org.cybnity.tool.test.sample.SampleGraphRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import redis.embedded.RedisServer;

/**
 * Unit test validating the usage of the InfrastructureContextualizedTest utility class like could be done by a project reusing this facility.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class AllServersInfrastructureUseCaseTest extends InfrastructureContextualizedTest {

    /**
     * Example of default constructor statically defining the configuration of the super instance.
     */
    public AllServersInfrastructureUseCaseTest() {
        // Define a unit test context requiring started Redis, JanusGraph and Keycloak servers ready for use by the unit tests
        super(true, true, true, false, /* snapshot repo configuration desired */ true);
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
        DefinedEnvVariableVerificationRule.verifyEnvironmentVariablesDefinedForContext(context(), true, true, true, true);

        // Verification of available snapshot repository prepared for test context
        ISnapshotRepository snapRepo = getSnaphotRepository();
        Assertions.assertNotNull(snapRepo, "Shall have been returned like desired during the constructor call!");

        // Attempt creation of a JanusGraph adapter connected to the started server
        SampleGraphRepository graphRepository = SampleGraphRepository.instance(new SampleDomainGraphImpl(context()));
        Assertions.assertNotNull(graphRepository, "Shall have been instantiated with default environment variable defined!");
        // Test usage of a graph method confirming the operational usage of JanusGraph instantiated
        graphRepository.drop();

        // Attempt creation of a Redis adapter connected to the Redis server
        RedisServer redis = getRedisServer();
        Assertions.assertNotNull(redis, "Shall have been prepared and started before any test execution!");
        // Test usage of server
        Assertions.assertTrue(redis.isActive(), "Shall be activated because normally started for the context!");
        Assertions.assertTrue(redis.ports().contains(InfrastructureContextualizedTest.REDIS_SERVER_PORT), "Redis server shall have been started on static contextualized port to be usable over adapter!");

        // Attempt read of started Keycloak server
        GenericContainer<?> keycloak = getKeycloak();
        Assertions.assertNotNull(keycloak, "Shall have been prepared before any test execution!");
        Assertions.assertTrue(keycloak.isRunning(), "Shall have been started and ready for use over an adapter!");
        Assertions.assertFalse(SSOTestContainer.ssoServerBaseURL(keycloak).isEmpty(), "Shall provide server's exposed URL ready for use via Keycloak adapter!");
    }
}
