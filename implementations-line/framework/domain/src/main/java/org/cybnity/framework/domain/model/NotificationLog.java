package org.cybnity.framework.domain.model;

import java.io.Serializable;
import java.util.Collection;

import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Log event regarding an identifiable domain event (e.g domain fact which is
 * subject to log tracking).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public class NotificationLog extends CommonChildFactImpl {

	private static final long serialVersionUID = new VersionConcreteStrategy()
			.composeCanonicalVersionHash(NotificationLog.class).hashCode();

	/**
	 * Default constructor of log regarding a fact that was observed (e.g a stored
	 * event).
	 * 
	 * @param loggedEvent Mandatory subject (e.g change state event, archiving
	 *                    event, storing event) which is logged.
	 * @param logEventId  Unique and optional identifier of this log event.
	 * @throws IllegalArgumentException When predecessor mandatory parameter is not
	 *                                  defined or without defined identifier. When
	 *                                  logEventId is using an identifier name that
	 *                                  is not equals to
	 *                                  BaseConstants.IDENTIFIER_ID.name().
	 */
	public NotificationLog(Entity loggedEvent, Identifier logEventId) throws IllegalArgumentException {
		super(loggedEvent, logEventId);
		if (!BaseConstants.IDENTIFIER_ID.name().equals(logEventId.name()))
			throw new IllegalArgumentException(
					"The identifier name of the logEventId parameter is not valid! Should be equals to NotificationLog.IDENTIFIER_NAME value");
	}

	@Override
	public Serializable immutable() throws ImmutabilityException {
		return new NotificationLog(this.parent(), this.identified());
	}

	/**
	 * Default implementation without any dependency to logged subject's identifying
	 * information. This method only return the original identifier of this log
	 * without merge of any external additional information.
	 */
	@Override
	protected Identifier generateIdentifierPredecessorBased(Entity predecessor, Identifier childOriginalId)
			throws IllegalArgumentException {
		if (childOriginalId == null)
			throw new IllegalArgumentException("Log original identification parameter si required!");
		// By default, return the basic identifying information regarding this log
		// without enhancing/dependency with the logged subject
		return childOriginalId;
	}

	/**
	 * This method is not implemented because identifier of a log is onlyt based on
	 * unique identifier value without merging of information relative to logged
	 * subject's identifier.
	 */
	@Override
	protected Identifier generateIdentifierPredecessorBased(Entity predecessor, Collection<Identifier> childOriginalIds)
			throws IllegalArgumentException {
		throw new IllegalArgumentException(
				"The NotificationLog is not supporting an identifier with dependency regarding the subjects logged! You should only use the default constructor based on unique identifying information.");
	}

}
