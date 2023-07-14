package org.cybnity.feature.defense_template.service;

import java.util.Collection;
import java.util.List;

import org.cybnity.framework.domain.Attribute;

/**
 * Specification contents regarding a definition of process step. Container of
 * informations allowing to collect values from a specification file (e.g XML
 * document) and that can be extract for build of domain object instances.
 * 
 * @author olivier
 *
 */
public class StepSpecification {

	private String name;
	private Collection<Attribute> properties;
	private List<StepSpecification> subStates;
	private Boolean activationState;
	private String completionName;
	private Float currentPercentageOfCompletion;

	public StepSpecification() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<Attribute> getProperties() {
		return properties;
	}

	public void setProperties(Collection<Attribute> properties) {
		this.properties = properties;
	}

	public List<StepSpecification> getSubStates() {
		return subStates;
	}

	public void setSubStates(List<StepSpecification> subStates) {
		this.subStates = subStates;
	}

	public Boolean getActivationState() {
		return activationState;
	}

	public void setActivationState(Boolean activationState) {
		this.activationState = activationState;
	}

	public String getCompletionName() {
		return completionName;
	}

	public void setCompletionName(String completionName) {
		this.completionName = completionName;
	}

	public Float getCurrentPercentageOfCompletion() {
		return currentPercentageOfCompletion;
	}

	public void setCurrentPercentageOfCompletion(Float currentPercentageOfCompletion) {
		this.currentPercentageOfCompletion = currentPercentageOfCompletion;
	}

}
