package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis;

import org.cybnity.framework.IReadableConfiguration;
import org.cybnity.framework.MissingConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests regarding the utility class that check the healthy and operational
 * state of an adapter runnable by a client-side.
 *
 * @author olivier
 */
@ExtendWith(SystemStubsExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ExecutableAdapterCheckerUseCaseTest {

    @SystemStub
    private EnvironmentVariables environmentVariables;

    public void initAdapterMinimumRequiredEnvVariables() {
        // Define environment variables

        // All environment variables required for write model access
        for (WriteModelConfigurationVariable aReq : EnumSet.allOf(WriteModelConfigurationVariable.class)) {
            environmentVariables.set(aReq.getName(),
                    /* Insert random value as variable value */ UUID.randomUUID().toString());
        }
        // All environment variables required for read model access
        for (ReadModelConfigurationVariable aReq : EnumSet.allOf(ReadModelConfigurationVariable.class)) {
            environmentVariables.set(aReq.getName(),
                    /* Insert random value as variable value */ UUID.randomUUID().toString());
        }
    }

    /**
     * Test that when minimum required environment variables are not existent in the
     * current runtime process, the checker detect the problem and notify an
     * exception for cause of missing configuration.
     *
     * @throws Exception
     */
    @Test
    public void givenNoneDefineEnvironmentVariable_whenCheckConfigurationVariables_thenMissingConfigurationException()
            throws Exception {
        assertThrows(MissingConfigurationException.class, () -> {

            // None defined environment variable

            // Execute the checker process (that should not found any of minimum required
            // env variable because undefined in this runtime)
            ExecutableAdapterChecker checker = new ExecutableAdapterChecker();
            checker.checkOperableState();
        });
    }

    /**
     * Test that an adapter checker which is executed on a client-side and that
     * require some specific environment variables (e.g defined by the context where
     * the adapter is executed) are found and validate the healthy and operable
     * state.
     *
     * @throws Exception
     */
    @Test
    public void givenValidSystemEnvironmentVariables_whenCheckConfigurationVariables_thenHealthyAndOperableStateConfirmed()
            throws Exception {
        // Simulate existent environment variables on an client-side context where
        // operability checker could be used by an adapter
        initAdapterMinimumRequiredEnvVariables();

        // Execute the checker process
        ExecutableAdapterChecker checker = new ExecutableAdapterChecker();
        checker.checkOperableState();
        // Valid that healthy state is delivered because none exception thrown
        assertTrue(checker.isOperableStateChecked());
    }

    /**
     * Test that all minimum environment variables needs by an adapter are provided
     * by the checker as catalog of mandatory variables to verify.
     *
     * @throws Exception
     */
    @Test
    public void givenMinimumRequiredVariable_whenReadVariableToCheck_thenAllProvidedByChecker() throws Exception {
        // Verify that all required variables have been found
        Set<IReadableConfiguration> minimumEnvVariablesRequired = new ExecutableAdapterChecker()
                .requiredEnvironmentVariables();

        // verify provided quantity of minimum environment variables supported by an
        // adapter
        assertNotNull(minimumEnvVariablesRequired);
        assertFalse(minimumEnvVariablesRequired.isEmpty());

        // All environment variables required for write model access
        for (WriteModelConfigurationVariable aReq : EnumSet.allOf(WriteModelConfigurationVariable.class)) {
            // Verify if treated by checker
            assertTrue(minimumEnvVariablesRequired.contains(aReq), "Variable not verified by the checker!");
        }
        // All environment variables required for read model access
        for (ReadModelConfigurationVariable aReq : EnumSet.allOf(ReadModelConfigurationVariable.class)) {
            // Verify if treated by checker
            assertTrue(minimumEnvVariablesRequired.contains(aReq), "Variable not verified by the checker!");
        }
    }
}
