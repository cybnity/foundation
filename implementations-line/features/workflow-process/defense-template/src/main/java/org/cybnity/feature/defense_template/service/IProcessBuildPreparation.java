package org.cybnity.feature.defense_template.service;

import java.util.Collection;
import java.util.List;

import org.cybnity.framework.domain.Attribute;

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
	public void processNamedAs(String name);

	/**
	 * Set attributes regarding the process description.
	 * 
	 * @param attributes A collection of attributes.
	 */
	public void processDescriptionProperties(Collection<Attribute> attributes);

	/**
	 * Set status of process activation.
	 * 
	 * @param isActive An activation state.
	 */
	public void processActivation(Boolean isActive);

	/**
	 * Set status of process completion.
	 * 
	 * @param completionName                A named completion.
	 * @param currentPercentageOfCompletion A current percentage of completion.
	 */
	public void processCompletion(String completionName, Float currentPercentageOfCompletion);

	/**
	 * Set staging of process with possible included sub-steps.
	 * 
	 * @param steps A list of steps.
	 */
	public void processStaging(List<StepSpecification> steps);
}
