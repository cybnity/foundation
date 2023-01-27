package org.cybnity.application.ui.system.backend.routing;

import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.RouterImpl;

/**
 * Router implementation which define the list of routes supported by a UI
 * capability perimeters accessible via the messaging gateway (backend endpoint).
 */
public class UICapabilitiesHTTPRouterImpl extends RouterImpl {

	public UICapabilitiesHTTPRouterImpl(Vertx vertx) {
		super(vertx);
		initRoutes(this);
	}

	/**
	 * Initialize all the routes supported by this routing service.
	 */
	static public void initRoutes(Router router) {
		// Mount the handlers for all incoming requests at every path and HTTP method
		// via creation of the several routes supported
		router.get("/acsc/").produces("application/json").handler(context -> {
			// This handler is called for any GET request to a path starting with
			// capability domain name
			sendJSONUICapabilityResponse(context, "acsc");
		}).failureHandler(failure -> {
			System.out.println("http route failure: " + failure.toString());
		});
	}

	/**
	 * Simulate a JSON answer provided on HTTP protocol by an API service.
	 *
	 * @param context
	 * @param calledResourceName
	 */
	static public void sendJSONUICapabilityResponse(RoutingContext context, String calledResourceName) {
		// Get the address of the request
		String address = context.request().connection().remoteAddress().toString();
		// Get the query parameter "name"
		MultiMap queryParams = context.queryParams();
		String name = queryParams.contains("name") ? queryParams.get("name") : "unknown";
		// Write a json response (re­turns a JSON ob­ject con­tain­ing the ad­dress of
		// the re­quest, the query pa­ra­me­ter name, and a greet­ing mes­sage)
		String json = new JsonObject().put("name", name).put("address", address).put("message",
				"Hello " + name + " (connected from " + address) + "), welcome on the called resource ("
				+ calledResourceName + ")";
		HttpServerResponse response = context.response();
		response.putHeader("content-type", "application/json");
		response.end(json);
	}
}
