package org.cybnity.feature.security_activity_orchestration.domain.model;

import org.cybnity.feature.security_activity_orchestration.IProcessBuilder;
import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.model.ActivityState;
import org.cybnity.framework.domain.model.CompletionState;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.immutable.*;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * Factory class implementing the builder design pattern to create a process.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public class ProcessBuilder implements IProcessBuilder {

	private final LinkedHashSet<Identifier> processIdentifiers;
	private final Entity processParent;
	protected Boolean activation;
	protected String completionName;
	protected Float currentPercentageOfCompletion;
	protected Collection<Attribute> description;
	protected String processName;
	private EntityReference templateEntityRef = null;
	private Process instance;

	/**
	 * Constructor of builder from unique or multiple identifiers to use for the
	 * process identity with translation managed.
	 * 
	 * @param processIdentifiers Mandatory identity of the process to build.
	 * @param processParent      Mandatory predecessor of the process to build.
	 * @throws IllegalArgumentException When missing mandatory parameter.
	 */
	protected ProcessBuilder(LinkedHashSet<Identifier> processIdentifiers, Entity processParent)
			throws IllegalArgumentException {
		if (processIdentifiers == null || processIdentifiers.isEmpty())
			throw new IllegalArgumentException("Process identity is required!");
		if (processParent == null)
			throw new IllegalArgumentException("Process parent is required!");
		this.processIdentifiers = processIdentifiers;
		this.processParent = processParent;
	}

	/**
	 * Get a builder instance allowing preparation of a process instantiation.
	 * 
	 * @param processIdentity Mandatory identity of the process to build.
	 * @param processParent   Mandatory predecessor of the process to build.
	 * @throws IllegalArgumentException When missing mandatory parameter.
	 */
	public static ProcessBuilder instance(LinkedHashSet<Identifier> processIdentifiers, Entity processParent)
			throws IllegalArgumentException {
		return new ProcessBuilder(processIdentifiers, processParent);
	}

	/**
	 * Default Constructor of process from unique or multiple identifiers to use for
	 * the process identity with translation managed.
	 * 
	 * @param processIdentifiers Mandatory identity of the process to build.
	 * @param processParent      Mandatory predecessor of the process to build.
	 * @param processName        Mandatory name of the process to build.
	 * @throws IllegalArgumentException When missing mandatory parameter.
	 */
	protected ProcessBuilder(LinkedHashSet<Identifier> processIdentifiers, Entity processParent, String processName)
			throws IllegalArgumentException {
		if (processIdentifiers == null || processIdentifiers.isEmpty())
			throw new IllegalArgumentException("Process identity is required!");
		if (processParent == null)
			throw new IllegalArgumentException("Process parent is required!");
		if (processName == null || "".equals(processName))
			throw new IllegalArgumentException("Process name is required!");
		this.processIdentifiers = processIdentifiers;
		this.processParent = processParent;
		this.processName = processName;
	}

	/**
	 * Get a builder instance allowing preparation of a process instantiation.
	 * 
	 * @param processIdentity Mandatory identity of the process to build.
	 * @param processParent   Mandatory predecessor of the process to build.
	 * @param processName     Mandatory name of the process to build. It can be
	 *                        valued as the name property key when a i18N locale is
	 *                        defined.
	 * @throws IllegalArgumentException When missing mandatory parameter.
	 */
	public static ProcessBuilder instance(LinkedHashSet<Identifier> processIdentifiers, Entity processParent,
			String processName) throws IllegalArgumentException {
		return new ProcessBuilder(processIdentifiers, processParent, processName);
	}

	/**
	 * Creation of a basis process instance according to the attributes defined into
	 * this builder component. It include description but that does not include any
	 * defined staging.
	 * 
	 * @throws ImmutabilityException    When impossible use of immutable version of
	 *                                  a build content.
	 * @throws IllegalArgumentException when mandatory content is missing to execute
	 *                                  the build process and shall be completed
	 *                                  before to call this method.
	 */
	public void build() throws ImmutabilityException, IllegalArgumentException {
		if (processName == null || "".equals(processName))
			throw new IllegalArgumentException("Process name is required!");
		// Build process identity
		DomainEntity processIdentity = new DomainEntity(this.processIdentifiers);

		// Build process description including a name attribute is required as minimum
		// description attribute defined
		HashMap<String, Object> descriptorAttributes = new HashMap<>();

		// Required name adding in translated version or original value
		descriptorAttributes.put(ProcessDescriptor.PropertyAttributeKey.Name.name(), this.processName);

		if (this.description != null) {
			// Add description properties
			descriptorAttributes.put(ProcessDescriptor.PropertyAttributeKey.Properties.name(), this.description);
		}
		if (this.templateEntityRef != null) {
			// Add template domain reference
			descriptorAttributes.put(ProcessDescriptor.PropertyAttributeKey.TemplateEntityRef.name(),
					this.templateEntityRef);
		}
		ProcessDescriptor processDescription = new ProcessDescriptor(processIdentity, descriptorAttributes,
				HistoryState.COMMITTED);

		// Initialize default process instance
		Process instance = new Process(this.processParent, this.processIdentifiers,
				new HashMap<>(processDescription.currentValue()));

		if (this.activation != null) {
			// Change the default activation state
			instance.changeActivation(new ActivityState(instance.root(), this.activation));
		}
		if (this.completionName != null && !this.completionName.equals(instance.completion().stateName())) {
			// Change the default completion state
			instance.changeCompletion(
					new CompletionState(instance.root(), this.completionName, this.currentPercentageOfCompletion));
		}
		// Set the built instance
		setResult(instance);
	}

	/**
	 * Update the pre-built instance.
	 * 
	 * @param instance An instance defining a result of build.
	 */
	protected void setResult(Process instance) {
		this.instance = instance;
	}

	@Override
	public Process getResult() {
		return this.instance;
	}

	/**
	 * Verify the conformity of an instance of process.
	 * 
	 * @param instance Mandatory instance to validate.
	 * @throws ImmutabilityException    WHen impossible use of immutable version of
	 *                                  process instance internal content.
	 * @throws IllegalArgumentException When any instance content is not valid. When
	 *                                  instance parameter is null.
	 */
	public static void validateConformity(Process instance) throws ImmutabilityException, IllegalArgumentException {
		if (instance == null)
			throw new IllegalArgumentException("Instance parameter is required to validate its conformity!");
		// Verify description contents
		instance.checkDescriptionConformity(instance.description(), instance.root().getEntity());
		// Verify activation conformity
		instance.checkActivationConformity(instance.activation(), instance.root().getEntity());
		// Verify completion conformity
		instance.checkCompletionConformity(instance.completion(), instance.root().getEntity());
		// Verify staging contents
		instance.checkStagingConformity(instance.staging(), instance.root().getEntity());
	}

	public ProcessBuilder withActivation(Boolean isActiveStatus) {
		if (isActiveStatus != null)
			this.activation = isActiveStatus;
		return this;
	}

	public ProcessBuilder withCompletion(String named, Float currentPercentageOfCompletion)
			throws IllegalArgumentException {
		if (named == null || "".equals(named))
			throw new IllegalArgumentException("Name parameter is required!");
		this.completionName = named;
		this.currentPercentageOfCompletion = currentPercentageOfCompletion;
		return this;
	}

	public ProcessBuilder withDescription(Collection<Attribute> properties) {
		this.description = properties;
		return this;
	}

	public ProcessBuilder withTemplateEntityReference(EntityReference templateRef) {
		this.templateEntityRef = templateRef;
		return this;
	}

}
