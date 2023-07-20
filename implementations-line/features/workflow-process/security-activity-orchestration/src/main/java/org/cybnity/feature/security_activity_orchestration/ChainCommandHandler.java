package org.cybnity.feature.security_activity_orchestration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.cybnity.framework.IContext;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Contract of command handling implementing the responsibility chain pattern.
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public abstract class ChainCommandHandler {

	/**
	 * Logical name of this handler.
	 */
	private String label;

	/**
	 * Optional context usable during a command handling execution.
	 */
	private IContext context;

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
	 * @param next     Optional set of next handler unique instance (sequential
	 *                 chain) or multiple (parallel chain) of the responsibility
	 *                 chain actors.
	 * @param subTasks Optional list of ordered sub-tasks realized by this chain
	 *                 command.
	 */
	public ChainCommandHandler(Collection<ChainCommandHandler> next, List<ChainCommandHandler> subTasks) {
		this.next = next;
		this.subTasks = subTasks;
	}

	/**
	 * Generally only one next handler (defined during this handling constructor
	 * call) is optionally defined into this chain command handling service. But
	 * it's possible to require parallel handlers simultaneous (and not ordered)
	 * called by this handler managing command received. In this case, this method
	 * allow to add parallel handlers to the current handler.
	 * 
	 * @param next Handler to add into the next parallel handlers which shall be
	 *             called for each handled command. Ignored when null parameter.
	 */
	public void addParallelNextHandler(ChainCommandHandler next) {
		if (next != null) {
			if (this.next == null)
				// Initialize the container of chain command handling services
				this.next = new ArrayList<>();
			// Add the new handler as parallel command receiver
			this.next.add(next);
		}
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
	 * @return A logical name when defined. Else return this.getClass().getName().
	 */
	public String label() {
		if (this.label == null || "".equals(this.label))
			return this.getClass().getName();
		return this.label;
	}

	/**
	 * Define a logical name for this handler.
	 * 
	 * @param aName A name.
	 */
	protected void setLabel(String aName) {
		this.label = aName;
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

	/**
	 * Get defined context.
	 * 
	 * @return A context or null.
	 */
	protected IContext context() {
		return this.context;
	}

	/**
	 * Change the context that is usable by the handler.
	 * 
	 * @param ctx A context which can be used dynamically (e.g at the runtime)
	 *            during the command treatment, by the canHandle(Command command)
	 *            method.
	 */
	public void changeContext(IContext ctx) {
		this.context = ctx;
	}

	/**
	 * Get the version number regarding the types of Command which are supported by
	 * this handler.
	 * 
	 * @return A set of versions that this handler is capable to handle (e.g
	 *         specific versions of a same type of command). Null or empty set when
	 *         any type of Command can be treated by this handler.
	 */
	public abstract Set<String> handledCommandTypeVersions();

}
