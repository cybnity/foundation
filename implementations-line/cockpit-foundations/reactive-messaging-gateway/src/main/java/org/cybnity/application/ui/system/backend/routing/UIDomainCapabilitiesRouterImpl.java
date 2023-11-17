package org.cybnity.application.ui.system.backend.routing;

import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
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
import org.cybnity.application.ui.system.backend.AppConfigurationVariable;
import org.cybnity.framework.IContext;

/**
 * Router implementation which define the list of routes supported by a UI
 * capabilities supported by the messaging gateway module.
 */
public class UIDomainCapabilitiesRouterImpl extends RouterImpl {

    private final IContext context;

    /**
     * Default constructor.
     *
     * @param vertx Mandatory base vertx context.
     * @param ctx   Mandatory base context providing environment resources.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public UIDomainCapabilitiesRouterImpl(Vertx vertx, IContext ctx) throws IllegalArgumentException {
        super(vertx);
        if (vertx == null) throw new IllegalArgumentException("vertx parameter is required!");
        if (ctx == null) throw new IllegalArgumentException("ctx parameter is required!");
        this.context = ctx;
        createRoutes(vertx);
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
            sendError(404, failure.response());
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
        Set<String> allowedHeaders = getAllowedHeaders();

        Set<HttpMethod> allowedMethods = new HashSet<>();
        allowedMethods.add(HttpMethod.GET);
        allowedMethods.add(HttpMethod.POST);
        allowedMethods.add(HttpMethod.OPTIONS);
        allowedMethods.add(HttpMethod.PUT);

        // Restrict cross calls only for server domain using the event bus channels
        // (e.g frontend server)
        List<String> authorizedWhitelistOrigins = new LinkedList<>();
        String serverURLs = context.get(AppConfigurationVariable.AUTHORIZED_WHITE_LIST_ORIGIN_SERVER_URLS);
        if (serverURLs != null && !serverURLs.isEmpty()) {
            // Identify each server url to authorize from the list
            for (String url : serverURLs.split(",")) {
                if (!"".equals(url)) {
                    authorizedWhitelistOrigins.add(url); // add server url authorization
                }
            }
        }

        // Set route options
        route().handler(CorsHandler.create().addOrigins(authorizedWhitelistOrigins
                /* Allowed origin pattern */
        ).allowCredentials(true /* Allow credentials property on XMLHttpRequest */
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

    /**
     * Add headers to response according to existing requirements from original request.
     *
     * @param toEnhance       Mandatory response to coplete.
     * @param originalRequest Mandatory request.
     */
    private void enhanceResponse(HttpServerResponse toEnhance, HttpServerRequest originalRequest) {
        if (toEnhance != null && originalRequest != null) {
            // Set correlation identifier when existing on the response
            String correlationId = originalRequest.getHeader("Correlation-ID");
            if (!"".equals(correlationId)) {
                /* X-Request-ID, X-Correlation-ID or Correlation-ID common non-standard response fields */
                toEnhance.putHeader("Correlation-ID", correlationId);
            }
        }
    }

    /**
     * Get the list of headers allowed regarding the requests treated by this backend server.
     *
     * @return A list of headers.
     */
    private static Set<String> getAllowedHeaders() {
        Set<String> allowedHeaders = new HashSet<>();
        allowedHeaders.add("x-requested-with");
        allowedHeaders.add("Access-Control-Allow-Origin");// All to consume the content
        allowedHeaders.add("origin");
        allowedHeaders.add("Content-Type");
        allowedHeaders.add("accept");
        allowedHeaders.add("Authorization");
        allowedHeaders.add("X-Requested-With");
        return allowedHeaders;
    }

    /**
     * Send error status code and close response.
     *
     * @param statusCode Status code based on <a href="https://en.wikipedia.org/wiki/List_of_HTTP_status_codes">standard HTTP status codes</a>
     * @param response   Mandatory response not ended and not closed. When already ended or closed, this method do nothing.
     * @throws IllegalArgumentException When required parameter is missing.
     */
    private void sendError(int statusCode, HttpServerResponse response) throws IllegalArgumentException {
        if (response == null) throw new IllegalArgumentException("response parameter is required!");
        if (!response.ended() && !response.closed()) {
            if (statusCode > 99 && statusCode < 600) {
                /*
                 * 1xx informational response – the request was received, continuing process
                 * 2xx successful – the request was successfully received, understood, and accepted
                 * 3xx redirection – further action needs to be taken in order to complete the request
                 * 4xx client error – the request contains bad syntax or cannot be fulfilled
                 * 5xx server error – the server failed to fulfil an apparently valid request
                 */
                response.setStatusCode(statusCode);
            }
            // End the response
            response.end();
        }
    }
}
