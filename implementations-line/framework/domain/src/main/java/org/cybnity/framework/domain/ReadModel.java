package org.cybnity.framework.domain;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a denormalized read store (also named Query Model) which subscribe
 * to events on the write model. It's a component of the read side of an
 * application. Segragation elements of the CQRS pattern, this component is
 * responsible of exposing readeable different sets of classes to other systems
 * (e.g application user interface).
 * 
 * Typically, return data over Data Transfer Objects (DTO).
 * 
 * The implementation system (e.g database) is optimized for reads by being
 * denormalized to suit the specific queries that an application should support
 * on the read side.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public interface ReadModel {

}