package org.cybnity.feature.defense_template.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;

import org.cybnity.feature.security_activity_orchestration.domain.model.Process;
import org.cybnity.feature.security_activity_orchestration.domain.model.ProcessBuilder;
import org.cybnity.feature.security_activity_orchestration.domain.model.Staging;
import org.cybnity.feature.security_activity_orchestration.domain.model.Staging.PropertyAttributeKey;
import org.cybnity.feature.security_activity_orchestration.domain.model.Step;
import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.model.ActivityState;
import org.cybnity.framework.domain.model.CompletionState;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Builder implementation class creating a process instance that is based on an
 * XML template. Builder design pattern implementation specialized for
 * preparation via XML document specifying a Process template.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public class XMLProcessProcessBuilder extends ProcessBuilder implements IProcessBuildPreparation {

	/**
	 * Specifications of steps defining a staging allowable to process.
	 */
	private List<StepSpecification> staging;

	/**
	 * Default constructor from unique or multiple identifiers to use for the
	 * process identity.
	 * 
	 * @param processIdentifiers Mandatory identity of the process to build.
	 * @param processParent      Mandatory predecessor of the process to build.
	 * @throws IllegalArgumentException When missing mandatory parameter.
	 */
	private XMLProcessProcessBuilder(LinkedHashSet<Identifier> processIdentifiers, Entity processParent)
			throws IllegalArgumentException {
		super(processIdentifiers, processParent);
	}

	/**
	 * Get a builder instance allowing preparation of a process instantiation.
	 * 
	 * @param processIdentity Mandatory identity of the process to build.
	 * @param processParent   Mandatory predecessor of the process to build.
	 * @throws IllegalArgumentException When missing mandatory parameter.
	 */
	public static XMLProcessProcessBuilder instance(LinkedHashSet<Identifier> processIdentifiers, Entity processParent)
			throws IllegalArgumentException {
		return new XMLProcessProcessBuilder(processIdentifiers, processParent);
	}

	/**
	 * Creation of the process instance according to the attributes defined into
	 * this builder component.
	 * 
	 * @throws ImmutabilityException    When impossible use of immutable version of
	 *                                  a build content.
	 * @throws IllegalArgumentException when mandatory content is missing to execute
	 *                                  the build process and shall be completed
	 *                                  before to call this method.
	 */
	public void build() throws ImmutabilityException, IllegalArgumentException {
		// Start the parsing of the XML document automatically feeding prepared value
		// into the builder

		// Create standard basis instance of process
		super.build();

		// Get the built instance to be customized
		Process instance = super.getResult();

		// Customize the instance regarding its structure (e.g step)
		defineStaging(instance);
		// Prepare the process commands handlers behavior (e.g
		// states machine and change events promoted)
		defineCommandsHandling(instance);

		// Update the built instance as ready to return
		setResult(instance);
	}

	/**
	 * Create and set the process with the RMF standard steps defining its staging.
	 * 
	 * @param instance To update.
	 * @return The updated instance referencing the process as owner.
	 * @throws ImmutabilityException    When problem of instantiation regarding
	 *                                  process instance update.
	 * @throws MissingResourceException When bundle of value translation is not
	 *                                  found, or missing required value is
	 *                                  detected.
	 */
	private void defineStaging(Process instance) throws ImmutabilityException, MissingResourceException {
		HashMap<String, Object> propertyCurrentValue = new HashMap<>();
		// Who is owner of properties to create?
		EntityReference propertyOwner = instance.root();

		if (this.staging != null) {
			// Create staging steps without handling with possible included sub-steps
			propertyCurrentValue.put(PropertyAttributeKey.Steps.name(),
					/* Create the steps including tasks */ defineSteps(propertyOwner, this.staging));

			// Create the staging including the steps and update the process
			instance.changeStaging(
					new Staging(propertyOwner.getEntity(), propertyCurrentValue, HistoryState.COMMITTED));
		}
	}

	/**
	 * Create ordered steps based on specifications.
	 * 
	 * @param propertyOwner Mandatory owner of each step to create.
	 * @param stagingDef    Mandatory list of ordered step specifications.
	 * @return A list of prepared steps.
	 * @throws ImmutabilityException    When bundle of value translation is not
	 *                                  found, or missing required is detected.
	 * @throws IllegalArgumentException When mandatory specification information is
	 *                                  missing.
	 */
	private List<Step> defineSteps(EntityReference propertyOwner, List<StepSpecification> stagingDef)
			throws ImmutabilityException, IllegalArgumentException {
		if (stagingDef == null)
			throw new IllegalArgumentException("StagingDef parameter is required!");
		// Create the staging including the RMF standard steps
		List<Step> steps = new LinkedList<>();
		Step aStep;
		HashMap<String, Object> stepCurrentProperties;
		HistoryState stepStatus = HistoryState.COMMITTED;
		for (StepSpecification stepDef : stagingDef) {
			stepCurrentProperties = new HashMap<>();

			// Define mandatory step name
			if (stepDef.getName() == null || "".equals(stepDef.getName()))
				throw new IllegalArgumentException("A mandatory name of step specification is missing!");
			stepCurrentProperties
					.put(org.cybnity.feature.security_activity_orchestration.domain.model.Step.PropertyAttributeKey.Name
							.name(), stepDef.getName());

			// Define activation state
			if (stepDef.getActivationState() != null) {
				stepCurrentProperties.put(
						org.cybnity.feature.security_activity_orchestration.domain.model.Step.PropertyAttributeKey.ActivityState
								.name(),
						new ActivityState(propertyOwner, stepDef.getActivationState()));
			}

			// Define completion state
			if (stepDef.getCompletionName() != null && !"".equals(stepDef.getCompletionName())) {
				stepCurrentProperties.put(
						org.cybnity.feature.security_activity_orchestration.domain.model.Step.PropertyAttributeKey.CompletionState
								.name(),
						new CompletionState(propertyOwner, stepDef.getCompletionName(),
								stepDef.getCurrentPercentageOfCompletion()));
			}

			// Define properties when step's attributes specified
			if (stepDef.getProperties() != null && !stepDef.getProperties().isEmpty()) {
				stepCurrentProperties.put(
						org.cybnity.feature.security_activity_orchestration.domain.model.Step.PropertyAttributeKey.Properties
								.name(),
						stepDef.getProperties());
			}

			// Define sub-states when specified
			if (stepDef.getSubStates() != null && !stepDef.getSubStates().isEmpty()) {
				// Define tasks (as sub-steps) supported by the step (recursive call)
				stepCurrentProperties.put(
						org.cybnity.feature.security_activity_orchestration.domain.model.Step.PropertyAttributeKey.SubSteps
								.name(),
						defineSteps(propertyOwner, stepDef.getSubStates()));
			}

			// Create instance of process step as sequential phase
			aStep = new Step(propertyOwner.getEntity(), stepCurrentProperties, stepStatus,
					/*
					 * processor allowing the step to interpret the commands handling relative to
					 * the step and/or to its subtasks
					 */ null);
			// Add tasks as sub-steps
			steps.add(aStep);
		}
		return steps;
	}

	/**
	 * Create and set the handling service which manage the supported commands (e.g
	 * process attributes change) and handling service.
	 * 
	 * @param instance To update.
	 * @return The updated instance.
	 */
	private void defineCommandsHandling(Process instance) {
		// TODO handledCommandTypeVersions ()

		// TODO handle(Command change, IContext ctx)
		// Utiliser un delegate de type CommandHandlingService pour cet aggregate et le
		// setter

	}

	@Override
	public void processNamedAs(String name) {
		this.processName = name;
	}

	@Override
	public void processDescriptionProperties(Collection<Attribute> attributes) {
		this.description = attributes;
	}

	@Override
	public void processActivation(Boolean isActive) {
		this.activation = isActive;
	}

	@Override
	public void processCompletion(String completionName, Float currentPercentageOfCompletion) {
		this.completionName = completionName;
		this.currentPercentageOfCompletion = currentPercentageOfCompletion;
	}

	@Override
	public void processStaging(List<StepSpecification> steps) {
		this.staging = steps;
	}
}
