package org.cybnity.feature.security_activity_orchestration;

import java.util.Collection;

import org.cybnity.framework.domain.Command;

/**
 * Basic implementation class.
 */
public class ConcreteCommandHandler extends ChainCommandHandler {

	public ConcreteCommandHandler(Collection<ChainCommandHandler> next) {
		super(next);
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
