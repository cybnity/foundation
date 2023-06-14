package org.cybnity.feature.security_activity_orchestration;

import java.util.Collection;
import java.util.List;

import org.cybnity.framework.domain.Command;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Basic implementation class of handler regarding a command of responsibility
 * chain.
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public class ConcreteCommandHandler extends ChainCommandHandler {

	/**
	 * Default constructor.
	 * 
	 * @param next     Optional set of next handler unique instance (sequential
	 *                 chain) or multiple (parallel chain) of the responsibility
	 *                 chain actors.
	 * @param subTasks Optional list of ordered sub-tasks realized by this chain
	 *                 command.
	 */
	public ConcreteCommandHandler(Collection<ChainCommandHandler> next, List<ChainCommandHandler> subTasks) {
		super(next, subTasks);
	}

	/**
	 * This method do nothing in terms of handling.
	 */
	@Override
	protected boolean canHandle(Command request) {
		// Verify if this handler support and can treat the requested command
		// process the request parameters and execute actions...
		// and return true

		// Else return false
		return false;
	}

}
