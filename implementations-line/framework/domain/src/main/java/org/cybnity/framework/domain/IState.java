package org.cybnity.framework.domain;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.util.Collection;
import java.util.List;

/**
 * Represent a providing contract regarding the description of a state (e.g a
 * process step) based on a collection of attributes.
 * 
 * A state can include sub-state into its life cycle.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public interface IState {

	/**
	 * Get the properties regarding this object.
	 * 
	 * @return A set of properties or null.
	 */
    Collection<Attribute> properties();

	/**
	 * Get existing sub-states defining a more detailed life cycle of this state.
	 * 
	 * @return A list of ordered sub-states included into this state life cycle. Or
	 *         null by default.
	 */
    List<IState> subStates();
}
