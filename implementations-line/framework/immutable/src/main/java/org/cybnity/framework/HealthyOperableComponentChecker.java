package org.cybnity.framework;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a verification capability regarding several data (e.g configuration
 * variables, files, permissions) required by a component to be considered as
 * operational in a safe status.
 * <p>
 * This is a Method Pattern implementation which define the algorithm squeletor
 * into a configurationHealthCheck() method that delegate each type of data
 * verification to sub-classes (e.g provided by the component which is subject
 * of the health control).
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Security, reqId = "REQ_SEC_8370_CM6")
public abstract class HealthyOperableComponentChecker {

    /**
     * Tag identifying if the check process was performed. False by default.
     */
    private boolean checkedStatus = false;

    /**
     * Process managing the verification of each type of required content (e.g
     * configuration variables, files, permissions) and runtime conditions that
     * allow to identify the current health and operational status of this
     * component.
     *
     * @throws UnoperationalStateException When impossible check execution.
     */
    public void checkOperableState() throws UnoperationalStateException {
        checkConfigurationVariables();
        checkOperatingFiles();
        checkResourcesPermissions();
        this.checkedStatus = true;
    }

    /**
     * Confirm if the checkOperableState() method was performed and that healthy was
     * already be checked by this process.
     *
     * @return False by default.
     */
    public boolean isOperableStateChecked() {
        return this.checkedStatus;
    }

    /**
     * Execute the verification of configuration variables required by the module.
     *
     * @throws UnoperationalStateException When any required configuration setting
     *                                     is not defined or have not value ready
     *                                     for use.
     */
    protected abstract void checkConfigurationVariables() throws UnoperationalStateException;

    /**
     * Execute the verification of operating files states (e.g start files, logging
     * file initialized...) as usable.
     *
     * @throws UnoperationalStateException When any required operation files is
     *                                     missing or have a problem to be used by
     *                                     this component.
     */
    protected abstract void checkOperatingFiles() throws UnoperationalStateException;

    /**
     * Execute the verification of valid permissions regarding the resources (e.g
     * file folders created, valid permissions allowed on directory/files, valid
     * required system account configuration) used by this component.
     *
     * @throws UnoperationalStateException When any required permissions is missing
     *                                     or is not valid regarding a resource
     *                                     accessible to the component.
     */
    protected abstract void checkResourcesPermissions() throws UnoperationalStateException;
}
