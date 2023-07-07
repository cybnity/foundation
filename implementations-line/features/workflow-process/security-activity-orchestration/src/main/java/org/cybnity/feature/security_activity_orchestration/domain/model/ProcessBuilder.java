package org.cybnity.feature.security_activity_orchestration.domain.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.cybnity.feature.security_activity_orchestration.IProcessBuilder;
import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.model.ActivityState;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

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
	private Boolean activation;
	private String completionName;
	private Float currentPercentageOfCompletion;
	private Collection<Attribute> description;
	private String processName;
	private EntityReference templateEntityRef = null;
	private Process instance;
	protected Locale i18nTranslation;
	private String resourcesBaseName;

	/**
	 * Default Constructor of process from unique or multiple identifiers to use for
	 * the process identity with translation managed.
	 * 
	 * @param processIdentifiers Mandatory identity of the process to build.
	 * @param processParent      Mandatory predecessor of the process to build.
	 * @param processName        Mandatory name of the process to build.
	 * @param language           Optional language of translation to use for content
	 *                           build.
	 * @param resourcesBaseName  Optional name of file resource bundle.
	 * @throws IllegalArgumentException When missing mandatory parameter.
	 */
	protected ProcessBuilder(LinkedHashSet<Identifier> processIdentifiers, Entity processParent, String processName,
			Locale language, String resourcesBaseName) throws IllegalArgumentException {
		if (processIdentifiers == null || processIdentifiers.isEmpty())
			throw new IllegalArgumentException("Process identity is required!");
		if (processParent == null)
			throw new IllegalArgumentException("Process parent is required!");
		if (processName == null || "".equals(processName))
			throw new IllegalArgumentException("Process name is required!");
		this.processIdentifiers = processIdentifiers;
		this.processParent = processParent;
		this.processName = processName;

		if (language != null)
			this.i18nTranslation = language;
		if (resourcesBaseName != null && !"".equals(resourcesBaseName))
			this.resourcesBaseName = resourcesBaseName;
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
		return ProcessBuilder.instance(processIdentifiers, processParent, processName, null, null);
	}

	/**
	 * Get a builder instance allowing preparation of a process instantiation.
	 * 
	 * @param processIdentity   Mandatory identity of the process to build.
	 * @param processParent     Mandatory predecessor of the process to build.
	 * @param processName       Mandatory name of the process to build.
	 * @param language          Optional language of translation to use for content
	 *                          build.
	 * @param resourcesBaseName Optional name of file resource bundle.
	 * @throws IllegalArgumentException When missing mandatory parameter.
	 */
	public static ProcessBuilder instance(LinkedHashSet<Identifier> processIdentifiers, Entity processParent,
			String processName, Locale language, String resourcesBaseName) throws IllegalArgumentException {
		return new ProcessBuilder(processIdentifiers, processParent, processName, language, resourcesBaseName);
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
		// Build process identity
		DomainEntity processIdentity = new DomainEntity(this.processIdentifiers);

		// Build process description including a name attribute is required as minimum
		// description attribute defined
		HashMap<String, Object> descriptorAttributes = new HashMap<>();

		// Read optional defined translation set of value
		// ResourceBundle bundle = getI18NProperties();

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
		if (this.completionName != null && !this.completionName.equals(instance.completion().name())) {
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

	public ProcessBuilder withI18NTranslation(Locale i18nTranslation, String resourcesBaseName) {
		this.i18nTranslation = i18nTranslation;
		this.resourcesBaseName = resourcesBaseName;
		return this;
	}

	/**
	 * Get the translation bundle usable for contents build.
	 * 
	 * @return A bundle of translated properties, or null.
	 * @throws MissingResourceException if no resource bundle for the specified base
	 *                                  name can be found.
	 */
	protected ResourceBundle getI18NProperties() throws MissingResourceException {
		ResourceBundle bundle = null;
		if (this.i18nTranslation != null && this.resourcesBaseName != null && !"".equals(this.resourcesBaseName)) {
			bundle = ResourceBundle.getBundle(this.resourcesBaseName, this.i18nTranslation);
		}
		return bundle;
	}

}
