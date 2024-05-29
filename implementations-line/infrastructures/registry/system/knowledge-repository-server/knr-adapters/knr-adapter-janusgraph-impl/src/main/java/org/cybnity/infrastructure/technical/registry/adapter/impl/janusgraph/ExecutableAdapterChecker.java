package org.cybnity.infrastructure.technical.registry.adapter.impl.janusgraph;

import org.cybnity.framework.IContext;
import org.cybnity.framework.IReadableConfiguration;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.immutable.utility.ExecutableComponentChecker;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation class regarding the verification of minimum required
 * configuration and contents allowing runnable adapter.
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Security, reqId = "REQ_SEC_8370_CM6")
public class ExecutableAdapterChecker extends ExecutableComponentChecker {

    /**
     * Constructor with dedicated context to use by this checker.
     *
     * @param ctx A context or null.
     */
    public ExecutableAdapterChecker(IContext ctx) {
        super(ctx);
    }

    /**
     * Default constructor.
     */
    public ExecutableAdapterChecker() {
        super();
    }

    @Override
    public Set<IReadableConfiguration> requiredEnvironmentVariables() {
        // Define the mandatory environment variable for adapter running
        Set<IReadableConfiguration> required = new HashSet<>();

        // - required
        required.add(ConfigurationVariable.JANUSGRAPH_STORAGE_BACKEND_TYPE);

        return required;
    }

    @Override
    protected void checkOperatingFiles() throws UnoperationalStateException {
        // None embedded files need to be check regarding the adapter to remote
        // server
    }

    @Override
    protected void checkResourcesPermissions() throws UnoperationalStateException {
        // None embedded resources into the adapter library that need to be checked in
        // terms of permissions
    }

}
