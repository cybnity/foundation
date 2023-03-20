package org.cybnity.application.ui.system.backend;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.cybnity.framework.MissingConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Vertx;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

/**
 * Test of behavior regarding the Vert.x backend server.
 * 
 * @author olivier
 *
 */
@ExtendWith({ VertxExtension.class, SystemStubsExtension.class })
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class SockJSReactiveBackendServerTest {

    @SystemStub
    private static EnvironmentVariables environmentVariables;

    /**
     * Test the start of backend Verticle rejected for cause of missing environment
     * variable (e.g http port).
     * 
     * @param vertx
     * @param testContext
     */
    @Test
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    public void givenUndefinedHttpPortEnvironmentVariable_whenHealthyStateChecked_thenMissingConfigurationException(
	    Vertx vertx, VertxTestContext testContext) {
	// None http port defined in environment variable

	// Start backend module (Verticle deployment)
	vertx.deployVerticle(SockJSReactiveBackendServer.class.getName(), testContext.failing(exception -> {
	    // Verify that UnoperationalStateException was thrown by the checkHealthyState()
	    // executed at start of the Verticle
	    assertTrue(exception instanceof MissingConfigurationException,
		    "Start process should have been interrupted for cause of missing configuration!");
	    // Validate test passed
	    testContext.completeNow();
	}));
    }

}
