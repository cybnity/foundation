package org.cybnity.framework.immutable.utility;

import org.cybnity.framework.*;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.util.Set;

/**
 * Concrete class that verify the configuration data regarding a component
 * validating its capability to be runned into a safe way (e.g all the minimum
 * required environment variable defined with valid values).
 * <p>
 * As defined by REQ_SEC_8370_CM6 security requirement, a started component
 * shall automatically manage, apply and verify its configuration settings in an
 * isolated way (e.g ui module, application module, infrastructure module
 * implementing a concrete sub-class of this one) using automated mechanisms
 * (abstract methods redefined by concrete class) for read (e.g format,
 * authorized access and read permissions), verification of validity (e.g
 * mandatory and authorized values) and operability (e.g status of applicability
 * confirmed by compliant interpretation and application on the system for
 * runtime).
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Security, reqId = "REQ_SEC_8370_CM6")
public abstract class ExecutableComponentChecker extends HealthyOperableComponentChecker {

    /**
     * Optional dedicated context to use during the specific check operations.
     */
    private IContext context;

    /**
     * Constructor with dedicated context to use by this checker.
     *
     * @param ctx A context or null.
     */
    public ExecutableComponentChecker(IContext ctx) {
        super();
        this.context = ctx;
    }

    /**
     * Default constructor.
     */
    public ExecutableComponentChecker() {
        super();
    }

    /**
     * Get the context currently used by this adapter if defined during its
     * initialization.
     *
     * @return A context instance or null.
     */
    protected IContext getContext() {
        return this.context;
    }

    /**
     * Execute the verification of configuration settings regarding existent and
     * valued environment variables required by the module.
     *
     * @throws UnoperationalStateException When any required environment variable is
     *                                     not defined or have not value ready for
     *                                     use.
     */
    @Override
    protected void checkConfigurationVariables() throws UnoperationalStateException {
        // --- IS ENVIRONMENT VARIABLES ARE DEFINED ? ---
        Set<IReadableConfiguration> envVar = requiredEnvironmentVariables();
        if (envVar != null) {
            // Check each environment variable definition into the current running context
            IContext ctx = getContext();
            if (ctx == null)
                ctx = new Context();
            String value;
            for (IReadableConfiguration aVar : envVar) {
                if (aVar != null && aVar.getName() != null && !aVar.getName().isBlank()) {
                    // Verify the existent defined value for this environment variable into the
                    // current runtime process
                    value = ctx.get(aVar);
                    if (value == null || "".equals(value)) {
                        throw new MissingConfigurationException("Required environment variable (" + aVar.getName()
                                + ") value is not defined by the system!");
                    }
                }
            }
        }
    }

    /**
     * Get the list of environment variables required by the component to be
     * considered as runnable. The returned variables set is verified on the current
     * system process allowing to detected possible missing variable (e.g forgotten
     * definition during an installation/deployment process).
     *
     * @return Set of common variables (e.g regarding container) and/or specific
     * environment variables (e.g specifically used by this component for
     * its configuration, service exposure...) that are required for
     * component running in a safe way.
     */
    public abstract Set<IReadableConfiguration> requiredEnvironmentVariables();

}
