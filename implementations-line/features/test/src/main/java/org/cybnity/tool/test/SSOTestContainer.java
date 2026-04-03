package org.cybnity.tool.test;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

/**
 * Utility class providing SSO services over a singleton pattern implementing a Keycloak development container.
 * See <a href="https://java.testcontainers.org/features/creating_container/">Testcontainers documentation</a> for help about class implementation.
 */
public class SSOTestContainer {

    /**
     * Waiting time of Keycloak docker image start.
     */
    static public final Duration KEYCLOAK_STARTUP_TIMEOUT = Duration.ofSeconds(500);

    /**
     * Singleton container.
     */
    static GenericContainer<?> KEYCLOAK_CONTAINER;

    /**
     * See CYBNITY iac-helm-charts repository, about sub-folders named access-control-sso/charts to identify the packaged version of Keycloak into the CYBNITY platform.
     * The version is specified into the charts/keycloak-XX.y.z.tgz unzipped files (see values.yaml file).
     * Current Helm packaged Bitnami version of Keycloak is bitnami/keycloak:24.0.5-debian-12-r0.
     */
    static private final String KEYCLOAK_SERVER_DOCKER_IMAGE = "quay.io/keycloak/keycloak:24.0.5"; // Same Keycloak version but used from Official Quay repository with in-memory mode

    /**
     * Keycloak administration account name.
     */
    static private final String KEYCLOAK_ADMIN_ACCOUNT = "admin";

    /**
     * Keycloak administration account password.
     */
    static private final String KEYCLOAK_ADMIN_PASSWORD = "password";

    /**
     * Exposed port number is from the perspective of the container (equals to default Keycloak image port).
     */
    static public final int KEYCLOAK_CONTAINER_HTTP_PORT = 8080;

    private SSOTestContainer() {
    }

    /**
     * Get an instance of Keycloak container runnable which can be started and/or stopped.
     * The provided instance is a singleton reference that require to take care of its start or stop when shared between multiple users.
     *
     * @param reuseActivation Is the instance shall be reusable. When true, enable the reusable capability of the container prepared. The Reusable feature keeps the containers running and next executions with the same container configuration will reuse it. To use it, start the container manually by calling start() method, do not call stop() method directly or indirectly via try-with-resources or JUnit integration, and enable it manually through an opt-in mechanism per environment. To reuse a container, the container configuration must be the same.
     * @return A singleton instance of prepared Keycloak container which can be ready for start, or which can be already started (e.g by previous user).
     */
    static public GenericContainer<?> getKeycloakContainer(boolean reuseActivation) {
        if (KEYCLOAK_CONTAINER == null) {
            // Define Keycloak server environment and default settings
            String httpBaseRelativePath = "/";
            // Docker image from Docker Hub
            KEYCLOAK_CONTAINER = new GenericContainer<>(DockerImageName.parse(
                    /* specific Docker image and version aligned with Keycloak chart project */
                    KEYCLOAK_SERVER_DOCKER_IMAGE))
                    .withEnv("KEYCLOAK_ADMIN", KEYCLOAK_ADMIN_ACCOUNT)// Admin account definition for Keycloak instance creation
                    .withEnv("KEYCLOAK_ADMIN_PASSWORD", KEYCLOAK_ADMIN_PASSWORD)
                    .withEnv("KEYCLOAK_HTTP_RELATIVE_PATH", httpBaseRelativePath)
                    .withEnv("KEYCLOAK_ENABLE_HTTPS", "false")
                    .withEnv("KC_HEALTH_ENABLED", "true") // expose Keycloak health check endpoints (e.g /health, /health/live, /health/ready)
                    .withEnv("KC_METRICS_ENABLED", "true") // expose Keycloak metrics check endpoint
                    // Default HTTP port internally started by Keycloak Docker image
                    .withExposedPorts(KEYCLOAK_CONTAINER_HTTP_PORT)
                    // see https://java.testcontainers.org/features/networking/#exposing-host-ports-to-the-container
                    .withAccessToHost(true)
                    // Ryuk must be started as a privileged container.
                    // If the environment already implements automatic cleanup of containers after the execution, but does not allow starting privileged containers, it can turn off the Ryuk container by setting
                    .withPrivilegedMode(true)
                    //.withEnv("TESTCONTAINERS_RYUK_DISABLED","true") // see https://java.testcontainers.org/features/configuration/#disabling-ryuk

                    // Determine at runtime whether an image should be pulled or not (and not only retrieved from the local Docker images cache)
                    // See Docker java API doc at https://github.com/docker-java/docker-java/blob/main/docs/getting_started.md
                    //.withImagePullPolicy(PullPolicy.alwaysPull())

                    // Execute the Keycloak start command in development mode, at start of Docker image
                    .withCommand("start-dev")
                    // By default, IsRunningStartupCheckStrategy class just checks if container is running (see https://www.javadoc.io/doc/org.testcontainers/testcontainers/latest/org/testcontainers/containers/startupcheck/IsRunningStartupCheckStrategy.html)
                    // Wait Keycloak start according to the available exposed HTTP port
                    // See definition of each health check endpoints at https://www.keycloak.org/server/health#_keycloak_health_check_endpoints
                    .waitingFor(Wait.forHttp("/health"))
                    // Avoid image registry rate limiting (see https://java.testcontainers.org/features/startup_and_waits/#one-shot-startup-strategy-example)
                    .withStartupTimeout(/* Image build by Quarkus is long */ KEYCLOAK_STARTUP_TIMEOUT)
                    // Enable reusable container (see https://java.testcontainers.org/features/reuse/)
                    .withReuse(reuseActivation);
        }
        return KEYCLOAK_CONTAINER;
    }

    /**
     * Execute the start of a container if not already in running status.
     *
     * @param container Mandatory container to start.
     * @return True when start executed. False when container was already running.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    static public boolean start(GenericContainer<?> container) throws IllegalArgumentException {
        if (container == null) throw new IllegalArgumentException("Container parameter is required!");
        // Check current status of container to decide if start need to be executed
        if (!container.isRunning()) {
            // The instance is not already started and can be started
            container.start();
            return true; // confirm started state
        }
        return false; // Not need to be started
    }

    /**
     * Execute the stop of a container if in running status.
     *
     * @param container Container to stop.
     * @return True when stop executed (if equals container to the current singleton instance to stopped container as parameter, the current singleton is set to null). False when container was already stopped, or container parameter was null.
     */
    static public boolean stop(GenericContainer<?> container) {
        if (container != null) {
            // Check current status of container to decide if stop need to be executed
            if (container.isRunning()) {
                // The instance can be stopped
                container.stop();

                if (container.equals(KEYCLOAK_CONTAINER))
                    KEYCLOAK_CONTAINER = null;

                return true; // confirm stopped state
            }
        }
        return false; // Not need to be stopped
    }

    /**
     * Read server configuration and return a built HTTP url relative to the server.
     *
     * @param server Mandatory container to read about its current host (ip address discovery), and mapper port (exposed externally of the Docker image).
     * @return A base URL (e.g http://192.168.3.86:8080/) when server is running.
     * @throws IllegalArgumentException      When mandatory parameter is missing.
     * @throws UnsupportedOperationException When server is stopped and the read of ip address or exposed port is not possible.
     */
    static public String ssoServerBaseURL(GenericContainer<?> server) throws IllegalArgumentException, UnsupportedOperationException {
        if (server == null) throw new IllegalArgumentException("Server parameter is required!");
        if (server.isRunning()) {
            // Exposed port number is from the perspective of the container. From the host's perspective Testcontainers actually exposes this on a random free port. This is by design, to avoid port collisions that may arise with locally running software or in between parallel test runs.
            // Because there is this layer of indirection, it is necessary to ask Testcontainers for the actual mapped port at runtime.
            // Build and return Keycloak dynamic URL
            return String.format("http://%s:%d", server.getHost(), server.getMappedPort(SSOTestContainer.KEYCLOAK_CONTAINER_HTTP_PORT)); // Build Keycloak dynamic URL
        }
        throw new UnsupportedOperationException("Impossible URL build from server that is not running!");
    }

    /**
     * Read server configuration and return a built HTTP url relative to the server's Admin REST API.
     * See Keycloak <a href="https://www.keycloak.org/docs-api/21.1.1/rest-api/#_uri_scheme">API URI scheme documentation</a>.
     *
     * @param server Mandatory container to read about its current host (ip address discovery), and mapper port (exposed externally of the Docker image).
     * @return A base URL (e.g {server base url}/admin/realms) when server is running.
     * @throws IllegalArgumentException      When mandatory parameter is missing.
     * @throws UnsupportedOperationException When server is stopped and the read of ip address or exposed port is not possible.
     */
    static public String adminRESTApiBaseURL(GenericContainer<?> server) throws IllegalArgumentException, UnsupportedOperationException {
        return ssoServerBaseURL(server) + "/admin/realms"; // See https://www.keycloak.org/docs-api/latest/rest-api/index.html#_version_information
    }
}
