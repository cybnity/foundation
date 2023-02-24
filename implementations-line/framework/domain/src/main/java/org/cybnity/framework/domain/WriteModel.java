package org.cybnity.framework.domain;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Also named Command Model, it's a segragation element of the CQRS pattern.
 * 
 * Event store that publishes events (e.g for refresh by read models) after they
 * have been saved. It's a component of the write side of an application.
 * 
 * Typically receive command events from other systems (e.g application user
 * interface).
 * 
 * The implementation system of this role (e.g database) is optimized for writes
 * by being fully normalized.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public interface WriteModel {

}
