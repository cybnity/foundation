package org.cybnity.framework.application.vertx.common;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import org.cybnity.framework.Context;
import org.cybnity.framework.IContext;
import org.cybnity.framework.application.vertx.common.routing.HealthControllableRouter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Provider of common services helping to manage worker instances.
 * Capability reusable into a Vert.x module (e.g processing unit) in a generic way.
 */
public class WorkersManagementCapability {

    /**
     * List of identifiers regarding deployed verticles.
     */
    public final List<String> deploymentIDs = new LinkedList<>();

    /**
     * Current context of adapter runtime.
     */
    public final IContext context = new Context();

    /**
     * Optionally started HTTP server.
     */
    private HttpServer httpService;

    /**
     * Default constructor.
     */
    public WorkersManagementCapability() {
    }

    /**
     * Start all workers regarding the verticle.
     *
     * @param deployed              Mandatory set of deployment options.
     * @param vertx                 Mandatory vertx main instance managing workers to start.
     * @param logger                Optional logging system allowing to add traces regarding deployed worker instance or failures.
     * @param loggedProcessUnitName Optional name of the processing unit which can be used in the logs.
     * @throws IllegalArgumentException When required parameter is missing.
     */
    public void startWorkers(Map<String, DeploymentOptions> deployed, Vertx vertx, Logger logger, String loggedProcessUnitName) throws IllegalArgumentException {
        if (deployed == null) throw new IllegalArgumentException("deployed parameter is required!");
        if (vertx == null) throw new IllegalArgumentException("vertx parameter is required!");
        // Start all processing unit workers
        for (Map.Entry<String, DeploymentOptions> entry :
                deployed.entrySet()) {
            vertx.deployVerticle(entry.getKey(), entry.getValue())
                    .onComplete(res -> {
                        if (res.succeeded()) {
                            // Save undeployable verticle identifier
                            this.deploymentIDs.add(res.result());
                            if (logger != null)
                                logger.info(entry.getValue().getInstances() + " " + ((loggedProcessUnitName != null) ? loggedProcessUnitName : "") + " worker instances deployed (type: " + entry.getKey() + ", id: " + res.result() + ")");
                        } else {
                            if (logger != null)
                                logger.info("AC IO worker instances deployment failed!");
                        }
                    });
        }
    }

    /**
     * Create the HTTP server supporting health routes supervision.
     *
     * @param vertx                  Mandatory owner of the HTTP server exposure.
     * @param logger                 Optional log system for traces creation about success or failed HTTP listening start.
     * @param notifiableStartPromise Optional promise to update (complete or fail call) according to the HTTP server start state.
     * @param loggedProcessUnitName  Optional name of the processing unit which can be used in the logs.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public void createHttpServer(Vertx vertx, Logger logger, Promise<Void> notifiableStartPromise, String loggedProcessUnitName) throws IllegalArgumentException {
        if (vertx == null) throw new IllegalArgumentException("vertx parameter is required!");
        // Create an HTTP server supporting supervision
        vertx.createHttpServer()
                // Handle every request using the router
                .requestHandler(/* Create a Router initialized to health supervision routes as HTTP specific request handler */HealthControllableRouter.httpHealthRouter(vertx))
                // Start HTTP listening according to the application settings
                .listen(Integer
                        .parseInt(context.get(AppConfigurationVariable.ENDPOINT_HTTP_SERVER_PORT)))
                // Print the port
                .onSuccess(server -> {
                    if (logger != null)
                        logger.info(((loggedProcessUnitName != null) ? loggedProcessUnitName : "") + " server started (port: " + server.actualPort() + ")");
                    // Keep reference to started server allowing future stop
                    httpService = server;
                    if (notifiableStartPromise != null)
                        notifiableStartPromise.complete();
                }).onFailure(error -> {
                    if (logger != null)
                        logger.severe(((loggedProcessUnitName != null) ? loggedProcessUnitName : "") + " server start failure: " + error.toString());
                    if (notifiableStartPromise != null)
                        notifiableStartPromise.fail(error);
                });
    }

    /**
     * Stop HTTP server if existing.
     *
     * @param logger                Optional log system for traces creation about success or failed HTTP listening stop.
     * @param notifiableStopPromise Optional promise to update (complete or fail call) according to the HTTP server stop state.
     * @param loggedProcessUnitName Optional name of the processing unit which can be used in the logs.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public void stopHttpServer(Logger logger, Promise<Void> notifiableStopPromise, String loggedProcessUnitName) throws IllegalArgumentException {
        if (this.httpService != null) {
            // Stop the http service
            int existingListenedPort = this.httpService.actualPort();
            this.httpService.close().onSuccess(res -> {
                if (logger != null)
                    logger.info(((loggedProcessUnitName != null) ? loggedProcessUnitName : "") + " server stopped (port: " + existingListenedPort + ")");
                if (notifiableStopPromise != null)
                    notifiableStopPromise.complete();
            }).onFailure(error -> {
                if (logger != null)
                    logger.severe(((loggedProcessUnitName != null) ? loggedProcessUnitName : "") + " server stop failure: " + error.toString());
                if (notifiableStopPromise != null)
                    notifiableStopPromise.fail(error);
            });
        }
    }

    /**
     * Define instances quantity for a worker type according to existing AppConfigurationVariable.DOMAIN_WORKER_THREAD_POOL_SIZE environment variable, and add configuration to the deployment options set.
     * When environment variable is not defined, none configuration about instances quantity is configured.
     *
     * @param options Mandatory options to enhance. This method make nothing if null parameter.
     */
    public void configureWorkerThreadsPoolSize(DeploymentOptions options) {
        if (options != null) {
            // Define worker threads pool size
            String workersPoolSize = context.get(AppConfigurationVariable.DOMAIN_WORKER_THREAD_POOL_SIZE);
            if (!"".equalsIgnoreCase(workersPoolSize))
                options.setInstances(Integer.parseInt(workersPoolSize));
        }
    }

    /**
     * Prepare and return a basic deployment options including common parameters defined (e.g capability domain pool name).
     *
     * @param poolName Mandatory pool name.
     * @return A option set.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public DeploymentOptions baseDeploymentOptions(String poolName) throws IllegalArgumentException {
        if (poolName == null || poolName.isEmpty())
            throw new IllegalArgumentException("poolName parameter shall be defined and not empty!");
        // A worker is just like a standard Verticle, but it’s executed using a thread from the Vert.x worker thread pool, rather than using an event loop.
        // Workers are designed for calling blocking code, as they won’t block any event loops
        DeploymentOptions options = new DeploymentOptions().setWorker(true);
        options.setWorkerPoolName(poolName);
        return options;
    }

    /**
     * Define instances quantity for a worker type according to existing AppConfigurationVariable.DOMAIN_WORKER_INSTANCES environment variable, and add configuration to the deployment options set.
     * When environment variable is not defined, none configuration about instances quantity is configured.
     *
     * @param options Mandatory options to enhance. This method make nothing if null parameter.
     */
    public void configureWorkerInstances(DeploymentOptions options) {
        if (options != null) {
            // Define instances quantity per worker type
            String workerInstances = context.get(AppConfigurationVariable.DOMAIN_WORKER_INSTANCES);
            if (!"".equalsIgnoreCase(workerInstances))
                options.setInstances(Integer.parseInt(workerInstances));
        }
    }

    /**
     * Undeploy each worker.
     *
     * @param vertx Mandatory vertx main instance managing workers to start.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public void undeployWorkers(Vertx vertx) throws IllegalArgumentException {
        if (vertx == null) throw new IllegalArgumentException("vertx parameter is required!");
        for (String deploymentId : this.deploymentIDs) {
            vertx.undeploy(deploymentId);
        }
    }
}
