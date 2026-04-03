package org.cybnity.framework.application.vertx.common.module;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.application.vertx.common.WorkersManagementCapability;
import org.cybnity.framework.domain.ICleanup;
import org.cybnity.framework.domain.IHealthControl;
import org.cybnity.framework.immutable.utility.ExecutableComponentChecker;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Common process module implementation class supporting all standard functions relative to operational functions
 * and/or common behaviors.
 * Composition module supporting the optional security services provided by a processing unit.
 */
@Requirement(reqType = RequirementCategory.Security, reqId = "REQ_SEC_8310_AC2")
public abstract class AbstractProcessModuleImpl extends AbstractVerticle implements ConfigurableModule, ICleanup, IHealthControl {

    /**
     * Generic helper providing basic reusable services regarding workers management.
     */
    private final WorkersManagementCapability workersCapability = new WorkersManagementCapability();

    /**
     * Utility class managing the verification of operable instance.
     */
    private ExecutableComponentChecker healthyChecker;

    /**
     * Get the utility class allowing management of workers.
     *
     * @return An instance of capability provider.
     */
    protected WorkersManagementCapability workersCapability() {
        return this.workersCapability;
    }

    /**
     * Start of workers and HTTP service.
     */
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        // Check the minimum required data allowing operating
        checkHealthyState();
        // Define the deployment options per feature worker type to start
        Map<String, DeploymentOptions> deployed = managedFeaturesWorkers();

        // Start all process module workers
        workersCapability.startWorkers(deployed, vertx, logger(), featuresDomainName());

        // Create the HTTP server supporting supervision
        workersCapability.createHttpServer(getVertx(), logger(), startPromise, processUnitLogicalName());
    }

    /**
     * Resource freedom (e.g undeploy all worker instances and stop of HTTP service).
     */
    @Override
    public void stop() {
        // Stop HTTP server
        workersCapability.stopHttpServer(logger(), null, processUnitLogicalName());

        // Undeploy each worker instance
        workersCapability.undeployWorkers(vertx);
    }

    /**
     * Get the type of checker applicable on this module.
     *
     * @return A checker or null.
     */
    protected abstract ExecutableComponentChecker healthyChecker();

    /**
     * Verify the status of health regarding this instance.
     *
     * @throws UnoperationalStateException When an issue is detected as cause of potential non stability source (e.g missing environment variable required during the runtime).
     */
    @Override
    public void checkHealthyState() throws UnoperationalStateException {
        if (healthyChecker == null)
            healthyChecker = healthyChecker();
        if (this.healthyChecker != null)
            // Execute the health check
            healthyChecker.checkOperableState();
    }

    /**
     * Get technical logging supporting this module.
     * @return A dedicated and customized logger instance singleton.
     */
    protected abstract Logger logger();

    /**
     * Get a set of the domain feature worker types managed by this processing unit.
     *
     * @return Collection of Feature worker types that shall be deployed, started and managed by this module.
     */
    protected abstract Collection<Class<?>> deployedWorkers();

    /**
     * Prepare and get the set of domain feature workers managed by this processing unit.
     * This process module is hosting all the Access Control domain UI capability features as an integrated Processing Module.
     *
     * @return Map of workers providing features, or empty map.
     */
    private Map<String, DeploymentOptions> managedFeaturesWorkers() {
        Map<String, DeploymentOptions> deployedWorkers = new HashMap<>();

        // Set each feature as workers pool member
        DeploymentOptions options = workersCapability().baseDeploymentOptions(poolName());

        // Define worker instances quantity per feature type managed by this module
        workersCapability().configureWorkerInstances(options);

        // Define threads pool size per feature type
        workersCapability().configureWorkerThreadsPoolSize(options);

        // Add each feature worker to the set of workers providing services
        for (Class<?> workerType : deployedWorkers()) {
            if (workerType != null)
                // --- EMBEDDED DOMAIN FEATURES DEFINITION ---
                deployedWorkers.put(workerType.getName(), options);
        }
        return deployedWorkers;
    }
}
