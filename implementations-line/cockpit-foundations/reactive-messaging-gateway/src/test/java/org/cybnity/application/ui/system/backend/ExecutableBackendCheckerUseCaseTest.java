package org.cybnity.application.ui.system.backend;

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
 * state of a backend runnable.
 * 
 * @author olivier
 *
 */
@ExtendWith(SystemStubsExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ExecutableBackendCheckerUseCaseTest {

    @SystemStub
    private EnvironmentVariables environmentVariables;

    public void initMinimumRequiredEnvVariables() {
	// Define environment variables

	// All environment variables
	for (AppConfigurationVariable aReq : EnumSet.allOf(AppConfigurationVariable.class)) {
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
	    ExecutableBackendChecker checker = new ExecutableBackendChecker();
	    checker.checkOperableState();
	});
    }

    /**
     * Test that a backend checker which is executed and that require some specific
     * environment variables (e.g defined by the context) are found and validate the
     * healthy and operable state.
     * 
     * @throws Exception
     */
    @Test
    public void givenValidSystemEnvironmentVariables_whenCheckConfigurationVariables_thenHealthyAndOperableStateConfirmed()
	    throws Exception {
	// Simulate existent environment variables on a context where
	// operability checker could be used
	initMinimumRequiredEnvVariables();

	// Execute the checker process
	ExecutableBackendChecker checker = new ExecutableBackendChecker();
	checker.checkOperableState();
	// Valid that healthy state is delivered because none exception thrown
	assertTrue(checker.isOperableStateChecked());
    }

    /**
     * Test that all minimum environment variables needs by a backend are provided
     * by the checker as catalog of mandatory variables to verify.
     * 
     * @throws Exception
     */
    @Test
    public void givenMinimumRequiredVariable_whenReadVariableToCheck_thenAllProvidedByChecker() throws Exception {
	// Verify that all required variables have been found
	Set<IReadableConfiguration> minimumEnvVariablesRequired = new ExecutableBackendChecker()
		.requiredEnvironmentVariables();

	// verify provided quantity of minimum environment variables supported by
	// backend server
	assertNotNull(minimumEnvVariablesRequired);
	assertFalse(minimumEnvVariablesRequired.isEmpty());

	// All environment variables required for write model access
	for (AppConfigurationVariable aReq : EnumSet.allOf(AppConfigurationVariable.class)) {
	    // Verify if treated by checker
	    assertTrue(minimumEnvVariablesRequired.contains(aReq), "Variable not verified by the checker!");
	}
    }
}
