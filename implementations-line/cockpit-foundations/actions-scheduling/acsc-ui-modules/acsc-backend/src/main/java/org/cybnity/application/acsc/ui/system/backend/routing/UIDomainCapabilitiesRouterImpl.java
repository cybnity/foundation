package org.cybnity.application.acsc.ui.system.backend.routing;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.AllowForwardHeaders;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.ext.web.impl.RouterImpl;

/**
 * Router implementation which define the list of routes supported by a UI
 * domain regarding its capabilities.
 */
public class UIDomainCapabilitiesRouterImpl extends RouterImpl {

	public UIDomainCapabilitiesRouterImpl(Vertx vertx) {
		super(vertx);
		initRoutes(vertx);
	}

	/**
	 * Initialize all the routes supported by this routing service.
	 */
	private void initRoutes(Vertx vertx) {
		// Mount the bridge for all incoming requests
		// according to the channels permitted and authorizations
		createRoutes(vertx);
	}

	/**
	 * Define input/outputs permitted channels (addresses) and event types rules
	 * regarding the event bus bridge.
	 *
	 * @param vertx
	 * @return
	 */
	private void createRoutes(Vertx vertx) {
		// --- DEFINE PERMITTED INPUT EVENT TYPES (will look through inbound permitted
		// matches when it is received from client side Javascript to the server) ---
		// Let through any message sent directly to a capability service from the client
		// side

		// Allow calls to the address domain channels from the client as long as the
		// messages have an action filed with value (e.g CQRS type as 'query, command')

		// But only if the user is logged in and has the authority "place_cqrs" (the
		// user must be first logged in and secondly have the required authority)

		// Add the UI static contents route supported by the HTTP layer about url path
		// and static contents directory
		StaticHandler staticWebContentsHandler = StaticHandler.create("static");
		// Configure the static files delivery
		staticWebContentsHandler.setCachingEnabled(false);
		staticWebContentsHandler.setDefaultContentEncoding("UTF-8");
		staticWebContentsHandler.setIndexPage("index.html");
		staticWebContentsHandler.setIncludeHidden(false);
		staticWebContentsHandler.setDirectoryListing(false);
		// Handle static resources
		route("/static/*").handler(staticWebContentsHandler).failureHandler(failure -> {
			System.out.println("static route failure: " + failure.toString());
		});

		// Add possible API routes supported by the HTTP layer as a JSON api
		UICapabilitiesHTTPRouterImpl.initRoutes(this);

		// --- DEFINE PERMITTED OUTPUT EVENT TYPES (will look through outbound permitted
		// matches before it is sent to the client Vert.x-Web) ---

		// Define what we're going to allow from server -> client
		// Let through any message coming from address

		// Let through any messages from addresses starting with
		// 'acsc.' (e.g acsc.info, acsc.news, etc)

		// --- DEFINE WHAT WE'RE GOING TO ALLOW FROM CLIENT -> SERVER
		SockJSBridgeOptions options = new SockJSBridgeOptions().setPingTimeout(5000);

		// Create Vert.x application data store
		SharedData sessionStore = vertx.sharedData();

		// Add the several control capabilities supported by the bridge on the router's
		// routes about event bus
		String currentOriginDomainHost = "localhost";
		SockJSHandlerOptions sockJSHandlerOpts = new SockJSHandlerOptions().setRegisterWriteHandler(true)
				.setLocalWriteHandler(false)// configure the sockJS instance build, that can be retrieved and stored in
				// local map
				.setRegisterWriteHandler(true);
		// Define protocol handler
		SockJSHandler sockJSHandler = SockJSHandler.create(vertx, sockJSHandlerOpts);

		// DEFINE FORWARD SUPPORT (cross origin from frontend server)
		this.allowForward(AllowForwardHeaders.FORWARD);
		// we can now allow forward header parsing
		// and in this case only the "X-Forward" headers will be considered
		this.allowForward(AllowForwardHeaders.X_FORWARD);
		// we can now allow forward header parsing
		// and in this case both the "Forward" header and "X-Forward" headers
		// will be considered, yet the values from "Forward" take precedence
		// this means if case of a conflict (2 headers for the same value)
		// the "Forward" value will be taken and the "X-Forward" ignored.
		this.allowForward(AllowForwardHeaders.ALL);

		// Define safe mechanism for allowing resources to be requested from one domain
		// (e.g ReactJS frontend server) and served from another (e.g this event bus
		// provider)
		// USE VERT.X-WEB CorsHandler handling the CORS protocol
		Set<String> allowedHeaders = new HashSet<>();
		allowedHeaders.add("x-requested-with");
		allowedHeaders.add("Access-Control-Allow-Origin");// All to consume the content
		allowedHeaders.add("origin");
		allowedHeaders.add("Content-Type");
		allowedHeaders.add("accept");
		allowedHeaders.add("Authorization");
		allowedHeaders.add("X-Requested-With");

		Set<HttpMethod> allowedMethods = new HashSet<>();
		allowedMethods.add(HttpMethod.GET);
		allowedMethods.add(HttpMethod.POST);
		allowedMethods.add(HttpMethod.OPTIONS);
		allowedMethods.add(HttpMethod.PUT);
		// Restrict cross calls only for server domains using the on event bus channels
		// (e.g frontend server)
		List<String> authorizedWhitelistOrigins = new LinkedList<>();
		authorizedWhitelistOrigins.add("http://" + currentOriginDomainHost + ":8080"); // backend server
		authorizedWhitelistOrigins.add("http://" + currentOriginDomainHost + ":3000"); // frontend server

		route().handler(CorsHandler.create().addOrigins(authorizedWhitelistOrigins/**
																					 * Allowed origin pattern
																					 **/
		).allowCredentials(true /** Allow credentials property on XMLHttpRequest **/
		).allowedHeaders(allowedHeaders).allowedMethods(allowedMethods));

		// Add BodyHandler before the SockJS handler which is required to process POST
		// requests by sub-router
		this.post().handler(BodyHandler.create());

		// Create a sub-route not under Access Control Layer (ACL)
		/*route("/eventbus/*").subRouter(sockJSHandler.bridge(options,
				new UICapabilityContextBoundaryHandler(vertx.eventBus(), sessionStore, cqrsResponseChannel, vertx)));*/

		// ------- SSO integration with Keycloak ----------

		// Add other domain endpoints routers (per cockpit capability boundary)

	}

}
