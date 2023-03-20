package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.cybnity.framework.IContext;
import org.cybnity.framework.IReadableConfiguration;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.immutable.utility.ExecutableComponentChecker;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Implementation class regarding the verification of minimum required
 * configuration and contents allowing runnable adapter.
 * 
 * @author olivier
 *
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
    protected Set<IReadableConfiguration> requiredEnvironmentVariables() {
	// Define the mandatory environment variable for adapter running
	Set<IReadableConfiguration> required = new HashSet<>();

	// - required for write model access
	required.addAll(EnumSet.allOf(WriteModelConfigurationVariable.class));
	// - required for read model access
	required.addAll(EnumSet.allOf(ReadModelConfigurationVariable.class));

	return required;
    }

    @Override
    protected void checkOperatingFiles() throws UnoperationalStateException {
	// None embedded files need to be check regarding the adapter to Redis remote
	// server
    }

    @Override
    protected void checkResourcesPermissions() throws UnoperationalStateException {
	// None embedded resources into the adapter library that need to be checked in
	// terms of permissions
    }

}
