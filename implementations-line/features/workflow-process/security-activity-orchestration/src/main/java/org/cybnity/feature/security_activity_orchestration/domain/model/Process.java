package org.cybnity.feature.security_activity_orchestration.domain.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.cybnity.feature.security_activity_orchestration.ITemplate;
import org.cybnity.framework.IContext;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.model.ActivityState;
import org.cybnity.framework.domain.model.Aggregate;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a workflow based on steps (e.g risk management process) realizable
 * by an actor and specifying an organizational model framing activities.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public class Process extends Aggregate implements ITemplate {

	private static final long serialVersionUID = new VersionConcreteStrategy()
			.composeCanonicalVersionHash(Process.class).hashCode();

	/**
	 * Mutable description of this process.
	 */
	private ProcessDescriptor description;
	/**
	 * Status of activation of this process.
	 */
	private ActivityState activation;

	/**
	 * Status of completion regarding the realization of this process instance (e.g
	 * according to the executed steps)
	 */
	private CompletionState completion;

	/**
	 * Stages defining the process realization.
	 */
	private Staging staging;

	/**
	 * Default constructor. The default activation state is defined as not active.
	 * The completion state is defined by default at zero progress rate.
	 * 
	 * @param predecessor           Mandatory parent of this process domain entity
	 *                              as aggregate. For example, can be a created
	 *                              social entity fact (see
	 *                              org.cybnity.framework.domain.model.SocialEntity}
	 *                              which shall exist before to create this process
	 *                              related to the organization.
	 * @param id                    Unique and optional identifier of this process.
	 *                              For example, identifier is required when the
	 *                              process shall be persistent. Else, can be
	 *                              without identity when not persistent process.
	 * @param descriptionAttributes Mandatory description of this process. A name
	 *                              attribute is required as minimum description
	 *                              attribute defined.
	 * @throws IllegalArgumentException When any mandatory parameter is missing.
	 *                                  When a problem of immutability is occurred.
	 *                                  When predecessor mandatory parameter is not
	 *                                  defined or without defined identifier.
	 * @throws ImmutabilityException    When impossible read of identifier version.
	 */
	public Process(Entity predecessor, Identifier id, HashMap<String, Object> descriptionAttributes)
			throws IllegalArgumentException, ImmutabilityException {
		super(predecessor, id);
		if (descriptionAttributes == null || descriptionAttributes.isEmpty()) {
			throw new IllegalArgumentException("Description attributes parameter is required and not empty!");
		} else {
			// Build description based on basic attributes provided
			ProcessDescriptor desc = new ProcessDescriptor(/** owner of the descriptor **/
					rootEntity(), descriptionAttributes, HistoryState.COMMITTED);
			// check minimum set of included attributes and values
			checkDescriptionConformity(desc, rootEntity());
			this.description = desc;
		}
		// Initialize default activity state
		this.activation = defaultActivation();
		// Initialize default completion state
		this.completion = defaultCompletion();
	}

	/**
	 * Specific partial constructor. The default activation state is defined as not
	 * active. The completion state is defined by default at zero progress rate.
	 * 
	 * @param predecessor           Mandatory parent of this process domain entity
	 *                              as aggregate. For example, can be a created
	 *                              social entity fact (see
	 *                              org.cybnity.framework.domain.model.SocialEntity}
	 *                              which shall exist before to create this process
	 *                              related to the organization.
	 * @param identifiers           Optional set of identifiers of this entity, that
	 *                              contains non-duplicable elements. For example,
	 *                              identifier is required when the process shall be
	 *                              persistent. Else can be without identity when
	 *                              not persistent process.
	 * @param descriptionAttributes Mandatory description of this process. A name
	 *                              attribute is required as minimum description
	 *                              attribute defined.
	 * @throws IllegalArgumentException When identifiers parameter is null or each
	 *                                  item does not include name and value. When
	 *                                  predecessor mandatory parameter is not
	 *                                  defined or without defined identifier.
	 * @throws ImmutabilityException    When impossible read of identifier version.
	 */
	public Process(Entity predecessor, LinkedHashSet<Identifier> identifiers,
			HashMap<String, Object> descriptionAttributes) throws IllegalArgumentException, ImmutabilityException {
		super(predecessor, identifiers);
		if (descriptionAttributes == null || descriptionAttributes.isEmpty()) {
			throw new IllegalArgumentException("Description attributes parameter is required and not empty!");
		} else {
			// Build description based on basic attributes provided
			ProcessDescriptor desc = new ProcessDescriptor(/** owner of the descriptor **/
					rootEntity(), descriptionAttributes, HistoryState.COMMITTED);
			// check minimum set of included attributes and values
			checkDescriptionConformity(desc, rootEntity());
			this.description = desc;
		}
		// Initialize default activity state
		this.activation = defaultActivation();
		// Initialize default completion state
		this.completion = defaultCompletion();
	}

	/**
	 * Internal constructor dedicated to immutability management.
	 * 
	 * @param predecessor Mandatory parent of this process domain entity as
	 *                    aggregate. For example, can be a created social entity
	 *                    fact (see org.cybnity.framework.domain.model.SocialEntity}
	 *                    which shall exist before to create this process related to
	 *                    the organization.
	 * @param identifiers Optional set of identifiers of this entity, that contains
	 *                    non-duplicable elements. For example, identifier is
	 *                    required when the process shall be persistent. Else can be
	 *                    without identity when not persistent process.
	 * @param description Mandatory description of this process.
	 * @param activation  Optional activation of this process.
	 * @param completion  Optional completion of this process.
	 * @param staging     Optional stages defining this process steps included a
	 *                    minimum one step.
	 * @throws IllegalArgumentException When any mandatory parameter is missing.
	 *                                  When a problem of immutability is occurred.
	 *                                  When predecessor mandatory parameter is not
	 *                                  defined or without defined identifier.
	 * @throws ImmutabilityException    When impossible read of identifier version.
	 */
	private Process(Entity predecessor, LinkedHashSet<Identifier> identifiers, ProcessDescriptor description,
			ActivityState activation, CompletionState completion, Staging staging)
			throws IllegalArgumentException, ImmutabilityException {
		super(predecessor, identifiers);
		// None quality control
		this.description = description;
		if (activation != null) {
			this.activation = activation;
		} else {
			// Initialize default activity state
			this.activation = defaultActivation();
		}
		if (completion != null) {
			this.completion = completion;
		} else {// Initialize default completion state
			this.completion = defaultCompletion();
		}
		if (staging != null)
			this.staging = staging;
	}

	/**
	 * Get the default completion state.
	 * 
	 * @return Default completion state defined at zero percentage of progress.
	 * @throws ImmutabilityException When process root instance can't be reused as
	 *                               immutable owner of the completion property.
	 */
	private CompletionState defaultCompletion() throws ImmutabilityException {
		return new CompletionState(this.root(), "InitialCompletionStatus",
				/* Initial percentage defined at zero of completion rate */ Float.valueOf(0.0f));
	}

	/**
	 * Get the default activation state.
	 * 
	 * @return Default activity state instance defined as inactive.
	 * @throws ImmutabilityException When process root instance can't be reused as
	 *                               immutable owner of the activity property.
	 */
	private ActivityState defaultActivation() throws ImmutabilityException {
		return new ActivityState(this.root(), Boolean.FALSE);
	}

	@Override
	public Serializable immutable() throws ImmutabilityException {
		Process copy = new Process(this.parent(), new LinkedHashSet<>(this.identifiers()),
				(ProcessDescriptor) this.description.immutable(), this.activation(), this.completion(), this.staging());
		// Complete with additional attributes of this complex aggregate
		copy.createdAt = this.occurredAt();
		return copy;
	}

	/**
	 * Redefined equality evaluation including the process identity and name.
	 */
	@Override
	public boolean equals(Object obj) {
		boolean isEquals = false;
		// Comparison based on process identity
		if (super.equals(obj) && obj instanceof Process) {
			try {
				Process compared = (Process) obj;
				// Comparison based on version functional attributes
				// Check if same names
				String objNameAttribute = compared.name();
				String thisNameAttribute = this.name();
				if (objNameAttribute != null && thisNameAttribute != null) {
					isEquals = objNameAttribute.equals(thisNameAttribute);
				}
			} catch (Exception e) {
				// any missing information generating null pointer exception or problem of
				// information read
			}
		}
		return isEquals;
	}

	/**
	 * Get the name of the process.
	 * 
	 * @return A label or null.
	 */
	@Override
	public String name() {
		if (this.description != null) {
			return this.description.name();
		}
		return null;
	}

	/**
	 * Get the staging defining the realization of this process.
	 * 
	 * @return An immutable staging or null.
	 * @throws ImmutabilityException When existing staging can't be returned as
	 *                               immutable version.
	 */
	public Staging staging() throws ImmutabilityException {
		Staging s = null;
		if (this.staging != null)
			s = (Staging) this.staging.immutable();
		return s;
	}

	/**
	 * Implement the generation of version hash regarding this class type according
	 * to a concrete strategy utility service.
	 */
	@Override
	public String versionHash() {
		return new VersionConcreteStrategy().composeCanonicalVersionHash(getClass());
	}

	/**
	 * Change the current staging of this process.
	 * 
	 * @param staginig Mandatory new version of staging version for replace the
	 *                 current process's steps definition.
	 * @throws IllegalArgumentException When mandatory parameter is not defined or
	 *                                  is not valid in terms of minimum conformity.
	 *                                  When staging parameter is not regarding same
	 *                                  process identity.
	 * @throws ImmutabilityException    When impossible read of any stage and/or
	 *                                  step's owner.
	 */
	public void changeStaging(Staging staging) throws IllegalArgumentException, ImmutabilityException {
		if (staging == null)
			throw new IllegalArgumentException("Stages parameter is required with included minimum one step!");
		// Check conformity of integrated stages and steps
		checkStagingConformity(staging, rootEntity());
		// Update this staging
		this.staging = staging;
	}

	/**
	 * Verify if the staging is valid (e.g owner equals to the process owner
	 * parameter).
	 * 
	 * @param staging      Optional staging to verify. Ignore if null.
	 * @param processOwner Optional process owner to compare. No control of staging
	 *                     owning when null.
	 * @throws IllegalArgumentException When cause of invalidity is detected.
	 * @throws ImmutabilityException    When problem of property read.
	 */
	void checkStagingConformity(Staging staging, Entity processOwner)
			throws IllegalArgumentException, ImmutabilityException {
		if (staging != null && processOwner != null) {
			// Check staging owner
			if (!staging.owner().equals(processOwner))
				throw new IllegalArgumentException("Staging must be owned by this process identity!");
		}
	}

	/**
	 * Change the current description of this process with automatic change history
	 * management.
	 * 
	 * @param description Mandatory new version of description for replace the
	 *                    current description.
	 * @throws IllegalArgumentException When mandatory parameter is not defined or
	 *                                  is not valid in terms of minimum conformity.
	 *                                  When description parameter is not regarding
	 *                                  same process identity.
	 * @throws ImmutabilityException    When impossible read of description's owner.
	 */
	public void changeDescription(ProcessDescriptor description)
			throws IllegalArgumentException, ImmutabilityException {
		if (description == null)
			throw new IllegalArgumentException("The description parameter is required!");
		// Check conformity of new version
		checkDescriptionConformity(description, rootEntity());
		// Update this process description
		this.description = description;
	}

	/**
	 * Verify if the description include the minimum set of attributes required to
	 * define the process and if owner is equals.
	 * 
	 * @param description  Optional description instance hosting the attributes to
	 *                     verify.
	 * @param processOwner Mandatory owner of the description to compare as a
	 *                     description condition.
	 * @throws IllegalArgumentException When description instance is not valid.
	 * @throws ImmutabilityException    When impossible read of description's owner.
	 */
	void checkDescriptionConformity(ProcessDescriptor description, Entity processOwner)
			throws IllegalArgumentException, ImmutabilityException {
		if (description != null) {
			if (processOwner == null)
				throw new IllegalArgumentException("Process owner parameter is required!");
			// Check that minimum name attribute is defined into the description

			// Check the process name
			String processName = description.name();
			if (processName == null || "".equals(processName))
				throw new IllegalArgumentException("Process name is required from description!");

			if (processOwner != null) {
				// Check that owner of the new description is equals to this process identity
				if (description.owner() == null || !description.owner().equals(processOwner))
					throw new IllegalArgumentException(
							"The owner of the new description shall be equals to this process!");
			}
		}
	}

	/**
	 * Update the current completion state by a new version.
	 * 
	 * @param state Mandatory new version of state.
	 * @throws IllegalArgumentException When mandatory parameter is not defined or
	 *                                  is not valid in terms of minimum conformity.
	 *                                  When state parameter is not regarding same
	 *                                  process identity.
	 * @throws ImmutabilityException    When impossible read of state's owner.
	 */
	public void changeCompletion(CompletionState state) throws IllegalArgumentException, ImmutabilityException {
		if (state == null)
			throw new IllegalArgumentException("The state parameter is required!");
		// Check conformity of new version
		checkCompletionConformity(state, rootEntity());
		// Update the completion state
		this.completion = state;
	}

	/**
	 * Verify if the state include basic attributes and values and that property
	 * owner is equals to this process. For example, a Not-a-Number or negative
	 * completion rate is not a valid value for percentage of completion.
	 * 
	 * @param state        Optional state to check.
	 * @param processOwner Mandatory owner of the state to compare as a status
	 *                     condition.
	 * @throws IllegalArgumentException When non conformity cause is detected.
	 * @throws ImmutabilityException    When impossible read of description's owner.
	 */
	void checkCompletionConformity(CompletionState state, Entity processOwner)
			throws IllegalArgumentException, ImmutabilityException {
		if (state != null) {
			if (processOwner == null)
				throw new IllegalArgumentException("Process owner parameter is required!");
			// Check that minimum name and percentage attributes are defined into the status

			// Check the name value
			if (state.name() == null)
				throw new IllegalArgumentException("Name value is required from state!");
			// Check the percentage value
			if (state.percentage() == null)
				throw new IllegalArgumentException("Percentage value is required from state!");
			// Check that percentage is positive
			if (state.percentage().isNaN() || state.percentage().compareTo(Float.valueOf(0.0f)) < 0)
				throw new IllegalArgumentException("Percentage value must be positive!");

			if (processOwner != null) {
				// Check that owner of the new state is equals to this process identity
				if (state.owner() == null || !state.owner().equals(processOwner))
					throw new IllegalArgumentException(
							"The owner of the new completion state shall be equals to this process!");
			}
		}
	}

	/**
	 * Update the current activation state by a new version.
	 * 
	 * @param state Mandatory new version of state.
	 * @throws IllegalArgumentException When mandatory parameter is not defined or
	 *                                  is not valid in terms of minimum conformity.
	 *                                  When state parameter is not regarding same
	 *                                  process identity.
	 * @throws ImmutabilityException    When impossible read of state's owner.
	 */
	public void changeActivation(ActivityState state) throws IllegalArgumentException, ImmutabilityException {
		if (state == null)
			throw new IllegalArgumentException("The state parameter is required!");
		// Check conformity of new version
		checkActivationConformity(state, rootEntity());
		// Update this process activation status
		this.activation = state;
	}

	/**
	 * Verify if the state include basic attributes and values and that property
	 * owner is equals to this process.
	 * 
	 * @param state        Mandatory state to check.
	 * @param processOwner Mandatory owner of the state to compare as a status
	 *                     condition.
	 * @throws IllegalArgumentException When non conformity cause is detected.
	 * @throws ImmutabilityException    When impossible read of description's owner.
	 */
	void checkActivationConformity(ActivityState state, Entity processOwner)
			throws IllegalArgumentException, ImmutabilityException {
		if (state != null) {
			if (processOwner == null)
				throw new IllegalArgumentException("Process owner parameter is required!");
			// Check that minimum name attribute is defined into the status

			// Check the status value
			if (state.isActive() == null)
				throw new IllegalArgumentException("Status value is required from state!");

			if (processOwner != null) {
				// Check that owner of the new state is equals to this process identity
				if (state.owner() == null || !state.owner().equals(processOwner))
					throw new IllegalArgumentException(
							"The owner of the new activity state shall be equals to this process!");
			}
		}
	}

	/**
	 * Get a description regarding this process.
	 * 
	 * @return An immutable version of this process description.
	 * @throws ImmutabilityException When impossible return of an immutable version
	 *                               of the description.
	 */
	public ProcessDescriptor description() throws ImmutabilityException {
		return (ProcessDescriptor) this.description.immutable();
	}

	/**
	 * Get the current status of activation regarding this process.
	 * 
	 * @return An immutable version of the state.
	 * @throws ImmutabilityException When impossible return of immutable version of
	 *                               the status.
	 */
	public ActivityState activation() throws ImmutabilityException {
		return (ActivityState) this.activation.immutable();
	}

	/**
	 * Get the current status of process completion.
	 * 
	 * @return An immutable version of the state.
	 * @throws ImmutabilityException When impossible return of immutable version of
	 *                               the status.
	 */
	public CompletionState completion() throws ImmutabilityException {
		return (CompletionState) this.completion.immutable();
	}

	@Override
	public void handle(Command change, IContext ctx) throws IllegalArgumentException {
		// TODO coder traitement de la demande de changement selon l'attribute ciblé ou
		// l'état de progression du process ou de ses sous-états

		// Utiliser un delegate de type CommandHandlingService pour cet aggregate
	}

	@Override
	public Set<String> handledCommandTypeVersions() {
		return null;
	}
}
