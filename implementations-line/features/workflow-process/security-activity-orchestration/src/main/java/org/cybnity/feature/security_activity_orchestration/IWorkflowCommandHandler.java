package org.cybnity.feature.security_activity_orchestration;

import org.cybnity.framework.domain.Command;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Chain of responsibility pattern implementation regarding the handling of
 * workflow command events.
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public interface IWorkflowCommandHandler {

	/**
	 * Define a chain parallel command handler. Generally only one next handler
	 * (defined during this handling constructor call) is optionally defined into
	 * this chain command handling service. But it's possible to require parallel
	 * handlers simultaneous (and not ordered) called by this handler managing
	 * command received. In this case, this method allow to add parallel handlers to
	 * the current handler.
	 * 
	 * @param next Handler to add into the next parallel handlers which shall be
	 *             called for each handled command. Ignored when null parameter.
	 */
    void addParallelNextHandler(ChainCommandHandler next);

	/**
	 * Handle and execute a command.
	 * 
	 * @param request The command to execute.
	 */
    void handle(Command request);
}
