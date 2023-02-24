package org.cybnity.framework.domain;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Persistence system of events that return the stream of events associated whic
 * an aggregate instance allowing to reply the events to recreate the state of
 * the aggregate.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public interface EventStore {

}
