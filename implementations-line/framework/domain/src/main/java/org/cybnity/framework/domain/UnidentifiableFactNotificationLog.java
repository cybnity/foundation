package org.cybnity.framework.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.IHistoricalFact;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Log event regarding a fact not previously identified (e.g system or context
 * fact which is subject to log traceability but that is not previously uniquely
 * identified). For example, the origin of this logged event can be suspect or
 * can come from unknown source (e.g threat agent, system in failure...)
 * requiring attention.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public class UnidentifiableFactNotificationLog extends Entity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of this type of identifier.
     */
    public static String IDENTIFIER_NAME = "log_uid";

    /**
     * Set of original facts that were origins of this log.
     */
    private List<IHistoricalFact> originFacts;

    /**
     * Default constructor of log regarding a fact that was observed (e.g a stored
     * event).
     * 
     * @param logEventId  Unique and optional identifier of this log event.
     * @param loggedFacts Optional facts observed that are original sources of this
     *                    log.
     * @throws IllegalArgumentException When predecessor mandatory parameter is not
     *                                  defined or without defined identifier. When
     *                                  logEventId is using an identifier name that
     *                                  is not equals to
     *                                  NotificationLog.IDENTIFIER_NAME.
     */
    public UnidentifiableFactNotificationLog(Identifier logEventId, IHistoricalFact... loggedFacts)
	    throws IllegalArgumentException {
	super(logEventId);
	if (!IDENTIFIER_NAME.equals(logEventId.name()))
	    throw new IllegalArgumentException(
		    "The identifier name of the logEventId parameter is not valid! Should be equals to NotificationLog.IDENTIFIER_NAME value");
	if (loggedFacts != null && loggedFacts.length > 0)
	    // save optional known origin facts
	    originFacts = Arrays.asList(loggedFacts);
    }

    /**
     * Get the list of origin facts that were loggued by this notification.
     * 
     * @return A set of facts immutable versions or empty list.
     * @throw ImmutabilityException When an immutable version of an origin fact
     *        can't be returned.
     */
    public List<IHistoricalFact> originFacts() throws ImmutabilityException {
	List<IHistoricalFact> origins = new ArrayList<>();
	if (this.originFacts != null) {
	    // Get an immutable version of facts
	    for (IHistoricalFact historicalFact : this.originFacts) {
		if (historicalFact != null)
		    origins.add((IHistoricalFact) historicalFact.immutable());
	    }
	}
	return origins;
    }

    /**
     * Implement the generation of version hash regarding this class type according
     * to a concrete strategy utility service.
     */
    @Override
    public String versionHash() {
	return new VersionConcreteStrategy().composeCanonicalVersionHash(getClass());
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	// Get a copy of facts
	List<IHistoricalFact> facts = originFacts();
	return new UnidentifiableFactNotificationLog(this.identified(),
		facts.toArray(new IHistoricalFact[facts.size()]));
    }

    @Override
    public Identifier identified() {
	StringBuffer combinedId = new StringBuffer();
	for (Identifier id : this.identifiers()) {
	    combinedId.append(id.value());
	}
	// Return combined identifier normally only based on unique value found in
	// identifiers list
	return new IdentifierStringBased(IDENTIFIER_NAME, combinedId.toString());
    }

}
