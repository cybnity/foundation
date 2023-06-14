package org.cybnity.feature.security_activity_orchestration;

import org.cybnity.framework.IContext;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Factory of handler.
 * 
 * Can be based on a template file (e.g JSON, XML) of standard (e.g NIST,
 * ISO27001).
 * 
 * For example, factory is usable to define cyber-security framework including
 * RMF process steps (and optional sub-tasks definitions) as ConcreteHandler
 * definitions.
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public abstract class WorkflowCommandHandlerFactory {

	/**
	 * Create an instance of handler based on a template definition.
	 * 
	 * @param template Mandatory template defining a command handler (e.g processed
	 *                 risk management steps).
	 * @return New instance of command handler.
	 * @throws IllegalArgumentException When mandatory parameter is not defined.
	 */
	public abstract IWorkflowCommandHandler create(ITemplate template) throws IllegalArgumentException;

	/**
	 * Create an instance of handler based on the read of a context.
	 * 
	 * @param context Mandatory context (e.g including a ITemplate reusable by the
	 *                factory to build instances of handlers).
	 * @return New instance of command handler.
	 * @throws IllegalArgumentException When mandatory parameter is not defined.
	 */
	public abstract IWorkflowCommandHandler create(IContext context) throws IllegalArgumentException;
}
