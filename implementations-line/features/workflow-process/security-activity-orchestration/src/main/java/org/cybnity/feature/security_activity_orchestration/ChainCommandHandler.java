package org.cybnity.feature.security_activity_orchestration;

import java.util.Collection;
import java.util.List;

import org.cybnity.framework.domain.Command;

/**
 * Contract of command handling implementing the chain of responsibility chain
 * pattern.
 */
public abstract class ChainCommandHandler {
	private String label;
	/**
	 * In sequence approach, only one next handler can be defined. In parallel
	 * approach of possible next handler, a set of unordered next handlers can be
	 * defined.
	 */
	private Collection<ChainCommandHandler> next;
	private List<ChainCommandHandler> subTasks;

	/**
	 * Default constructor.
	 * 
	 * @param next Optional set of next handler unique instance (sequential chain)
	 *             or multiple (parallel chain) of the responsibility chain actors.
	 */
	public ChainCommandHandler(Collection<ChainCommandHandler> next) {
		this.next = next;
	}

	/**
	 * Get the next handler of eligible command.
	 * 
	 * @return A set of handlers or null. In sequence approach, only one next
	 *         handler can be defined. In parallel approach of possible next
	 *         handler, a set of unordered next handlers can be defined.
	 */
	protected Collection<ChainCommandHandler> next() {
		return this.next;
	}

	/**
	 * Implementation of responsibility chain with execution of next handler if
	 * existing and supported request.
	 * 
	 * In case of not supported request by this handler, and existing sub-tasks
	 * defined as contributor to it, the method try to delegate the request handling
	 * on each found sub-task.
	 * 
	 * @param request Mandatory command to execute via the next handler when
	 *                supported.
	 * @throws IllegalArgumentException When request parameter is not defined.
	 */
	public final void handle(Command request) throws IllegalArgumentException {
		if (request == null)
			throw new IllegalArgumentException("Request parameter is missing!");

		if (!this.canHandle(request)) {
			// Command is not handled by this concrete class
			boolean isHandled = false;
			// Check if there is any sub-task which could support the request handling
			if (this.subTasks() != null) {
				for (ChainCommandHandler subTaskHandler : this.subTasks()) {
					// Identity and execute the possible unique or parallel sub-handlers
					// which could support the request
					if (subTaskHandler != null) {
						if (subTaskHandler.canHandle(request)) {
							isHandled = true;
							// Detect that a handler managed the request justifying the stop of the
							// other handlers search
							break;
						}
					}
				}
			}
			// Check if there is a next handler in the responsibility chain which
			// is supporting the request to be handled when it was not already handled by
			// this handling instance
			if (!isHandled && this.next() != null) {
				for (ChainCommandHandler nextHandler : this.next) {
					// Identify and execute the possible unique or parallel next handlers
					// which could support the request
					if (nextHandler != null)
						nextHandler.canHandle(request);
				}
			}
		} else {
			// The request have been processed by concrete class
		}
	}

	/**
	 * Verify if the request is supported. When supported, process the request
	 * parameters and execute actions.
	 * 
	 * @param request The command eligible to execution that should be executed.
	 * @return True when request is supported and actions have been executed. Else,
	 *         when a next handler is defined, execute and return the output of
	 *         next.canHandle(request). When request is not supported and none next
	 *         handler is defined, return false.
	 */
	protected abstract boolean canHandle(Command request);

	/**
	 * Get the name of this handler.
	 * 
	 * @return A name.
	 */
	public String label() {
		return this.label;
	}

	/**
	 * Get the list of sub-tasks ensuring handling of additional and specific
	 * command types.
	 * 
	 * @return A list of sub-tasks or null.
	 */
	protected List<ChainCommandHandler> subTasks() {
		return this.subTasks;
	}

}
