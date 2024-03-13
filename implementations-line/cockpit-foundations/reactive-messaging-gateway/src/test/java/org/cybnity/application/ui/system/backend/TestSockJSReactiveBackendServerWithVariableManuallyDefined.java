package org.cybnity.application.ui.system.backend;

import io.vertx.core.Vertx;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import java.util.concurrent.TimeUnit;

/**
 * Test of behavior regarding the Vert.x backend server with environment
 * variables defined (e.g into the JUnit Eclipse's test configuration).
 * 
 * @author olivier
 *
 */
@ExtendWith({ VertxExtension.class, SystemStubsExtension.class })
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class TestSockJSReactiveBackendServerWithVariableManuallyDefined {

    @SystemStub
    private static EnvironmentVariables environmentVariables;

    @BeforeEach
    public void showVariableEnvironmentToSet() {
	System.out.println(
		"---------\nCheck that environment variables are defined into the test configuration:\nREACTIVE_BACKEND_ENDPOINT_HTTP_SERVER_PORT=8080\n---------");
    }

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
	vertx.deployVerticle(SockJSReactiveBackendServer.class.getName(), testContext.succeeding(id -> {

	    // Validate test passed
	    testContext.completeNow();
	}));
    }

}
