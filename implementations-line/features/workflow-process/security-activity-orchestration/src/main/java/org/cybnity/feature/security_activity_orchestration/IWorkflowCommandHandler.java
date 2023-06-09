package org.cybnity.feature.security_activity_orchestration;

import java.util.Collection;

import org.cybnity.framework.domain.Command;

/**
 * Chain of responsibility pattern implementation regarding the handling of
 * workflow command events.
 */
public interface IWorkflowCommandHandler {

	/**
	 * Define a chain of next handler
	 * 
	 * @param next A next handler executable.
	 */
	public void setNext(Collection<IWorkflowCommandHandler> next);

	/**
	 * Handle and execute a command.
	 * 
	 * @param request The command to execute.
	 */
	public void handle(Command request);
}
