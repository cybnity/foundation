package org.cybnity.feature.defense_template.service;

import org.cybnity.framework.domain.Attribute;

import java.util.Collection;
import java.util.List;

/**
 * Represent a observation contract of a template resource read, allowing to
 * received found resource's contents as prepared value.
 * 
 * @author olivier
 *
 */
public interface IProcessBuildPreparation {

	/**
	 * Set preparation value about the process name.
	 * 
	 * @param name A name regarding a process.
	 */
    void processNamedAs(String name);

	/**
	 * Set attributes regarding the process description.
	 * 
	 * @param attributes A collection of attributes.
	 */
    void processDescriptionProperties(Collection<Attribute> attributes);

	/**
	 * Set status of process activation.
	 * 
	 * @param isActive An activation state.
	 */
    void processActivation(Boolean isActive);

	/**
	 * Set status of process completion.
	 * 
	 * @param completionName                A named completion.
	 * @param currentPercentageOfCompletion A current percentage of completion.
	 */
    void processCompletion(String completionName, Float currentPercentageOfCompletion);

	/**
	 * Set staging of process with possible included sub-steps.
	 * 
	 * @param steps A list of steps.
	 */
    void processStaging(List<StepSpecification> steps);
}
