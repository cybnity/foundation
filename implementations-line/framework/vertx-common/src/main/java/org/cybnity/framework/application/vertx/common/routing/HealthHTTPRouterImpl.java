package org.cybnity.framework.application.vertx.common.routing;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.impl.RouterImpl;

/**
 * Router implementation which define the list of resources supported by a component accessible for health supervision.
 */
public class HealthHTTPRouterImpl extends RouterImpl {

    /**
     * Default constructor.
     *
     * @param vertx Mandatory base vertx context.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public HealthHTTPRouterImpl(Vertx vertx) throws IllegalArgumentException {
        super(vertx);
        if (vertx == null) throw new IllegalArgumentException("vertx parameter is required!");
        createRoutes();
    }

    /**
     * Define input/outputs permitted resources.
     */
    private void createRoutes() {
        // Some public routes are provided regarding resources exposed without mandatory logged credential check

        // Add the UI static contents route supported by the HTTP layer about url path
        // and static contents directory eventually provided by this module
        StaticHandler staticWebContentsHandler = StaticHandler.create("static");
        // Configure the static files delivery
        staticWebContentsHandler.setCachingEnabled(false);
        staticWebContentsHandler.setDefaultContentEncoding("UTF-8");
        staticWebContentsHandler.setIncludeHidden(false);
        staticWebContentsHandler.setDirectoryListing(false);
        // Handle static resources
        route("/static/*").handler(staticWebContentsHandler).failureHandler(failure -> {
            sendError(404, failure.response());
        });
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