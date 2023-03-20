package org.cybnity.application.ui.system.backend;

import org.cybnity.application.ui.system.backend.routing.CapabilityRouter;
import org.cybnity.framework.Context;
import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;

import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class SockJSReactiveBackendServer extends SockJSServer {
    /**
     * Utility class managing the verification of operable instance.
     */
    private ExecutableBackendChecker healthyChecker;

    /**
     * Current context of adapter runtime.
     */
    private final IContext context = new Context();

    /**
     * Default start method regarding the server.
     *
     * @param args None pre-required.
     */
    public static void main(String[] args) {
	Vertx vertx = Vertx.vertx();
	vertx.deployVerticle(new SockJSReactiveBackendServer());
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
	// Check the minimum required data allowing operating
	checkHealthyState();

	// Route event bus's events to spe­cific re­quest han­dlers

	// Create a Router initialized to support capability routes
	Router router = CapabilityRouter.sockjsRouter(vertx);

	// Create the HTTP server
	getVertx().createHttpServer()
		// Handle every request using the router
		.requestHandler(router)
		// Start HTTP listening according to the application settings
		.listen(Integer
			.valueOf(context.get(AppConfigurationVariable.REACTIVE_BACKEND_ENDPOINT_HTTP_SERVER_PORT)))
		// Print the port
		.onSuccess(server -> {
		    System.out.println("SockJS backend server started (port: " + server.actualPort() + ")");
		    startPromise.complete();
		}).onFailure(error -> {
		    System.out.println("SockJS backend server start failure: " + error.getCause());
		    startPromise.fail(error.getCause());
		});
    }

    @Override
    public void checkHealthyState() throws UnoperationalStateException {
	if (healthyChecker == null)
	    healthyChecker = new ExecutableBackendChecker(context);
	// execution the health check
	healthyChecker.checkOperableState();
    }
}
