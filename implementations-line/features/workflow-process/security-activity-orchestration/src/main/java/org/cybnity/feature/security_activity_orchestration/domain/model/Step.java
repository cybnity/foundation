package org.cybnity.feature.security_activity_orchestration.domain.model;

import org.cybnity.feature.security_activity_orchestration.ChainCommandHandler;
import org.cybnity.feature.security_activity_orchestration.IWorkflowCommandHandler;
import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.IState;
import org.cybnity.framework.domain.model.ActivityState;
import org.cybnity.framework.domain.model.CompletionState;
import org.cybnity.framework.immutable.*;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.io.Serializable;
import java.util.*;

/**
 * Represent a workflow phase (e.g also named process step) that define a state
 * of a working set (e.g unique or multiple actions).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public class Step extends MutableProperty implements IWorkflowCommandHandler, IState {

	private static final long serialVersionUID = new VersionConcreteStrategy().composeCanonicalVersionHash(Step.class)
			.hashCode();

	/**
	 * Delegation class supporting the handling of commands.
	 */
	private ChainCommandHandler commandProcessor;

	/**
	 * The keys set regarding the multiple attributes defining this step, and that
	 * each change need to be versioned/treated as a single atomic fact.
	 */
	public enum PropertyAttributeKey {
		/** Name of a step **/
		Name,
		/**
		 * Attributes collection specifying the step description (e.g organization
		 * level)
		 */
		Properties,
		/** Sub steps definition **/
		SubSteps,
		/**
		 * Status of completion regarding the realization of this step instance (e.g
		 * according to the executed sub-steps)
		 **/
		CompletionState,
		/** Status of activation of this step **/
		ActivityState,
		/**
		 * Event types that are supported as cause of auto-activation of this step (e.g
		 * handle for automatic change activityState to active)
		 **/
		ActivationEventTypes;
	}

	/**
	 * Default constructor.
	 * 
	 * @param propertyOwner           Mandatory owner of this step (e.g process
	 *                                entity), including the entity information.
	 * @param name                    Mandatory name of this step.
	 * @param commandHandlingDelegate Optional processor allowing the step to
	 *                                interpret the commands handling relative to
	 *                                the step and/or to its subtasks.
	 * @throws IllegalArgumentException When mandatory parameter is missing.
	 * @throws ImmutabilityException    When impossible creation of immutable
	 *                                  version regarding the owner instance.
	 */
	public Step(EntityReference propertyOwner, String name, ChainCommandHandler commandHandlingDelegate)
			throws IllegalArgumentException, ImmutabilityException {
		this(propertyOwner, name, commandHandlingDelegate, (Step) null);
	}

	/**
	 * Constructor with predecessors step automatically to save as history.
	 * 
	 * @param propertyOwner           Mandatory owner of this step (e.g process),
	 *                                including the entity information.
	 * @param name                    Mandatory name of this step.
	 * @param commandHandlingDelegate Optional processor allowing the step to
	 *                                interpret the commands handling relative to
	 *                                the step and/or to its subtasks.
	 * @param predecessors            Optional ancestor steps.
	 * @throws IllegalArgumentException When mandatory parameter is missing.
	 * @throws ImmutabilityException    When impossible creation of immutable
	 *                                  version regarding the owner instance.
	 */
	public Step(EntityReference propertyOwner, String name, ChainCommandHandler commandHandlingDelegate,
			Step... predecessors) throws IllegalArgumentException, ImmutabilityException {
		this( /* Reference identifier equals to the owner of this step */
				(propertyOwner != null) ? propertyOwner.getEntity() : null,
				(name != null && !"".equals(name)) ? buildPropertyValue(PropertyAttributeKey.Name, name) : null,
				HistoryState.COMMITTED, commandHandlingDelegate, predecessors);
		if (name == null || "".equals(name))
			throw new IllegalArgumentException("Name parameter is required!");

	}

	/**
	 * Default constructor with automatic initialization of an empty value set
	 * (prior chain).
	 * 
	 * @param propertyOwner           Mandatory entity which is owner of this
	 *                                mutable property chain.
	 * @param propertyCurrentValue    Mandatory current version of value(s)
	 *                                regarding the property. Support included keys
	 *                                with null value.When none activation or
	 *                                completion state is defined, a default state
	 *                                is automatically assigned to this step.
	 * @param status                  Optional state of this property version. If
	 *                                null,
	 *                                {@link org.cybnity.framework.immutable.HistoryState.Committed}
	 *                                is defined as default state.
	 * @param commandHandlingDelegate Optional processor allowing the step to
	 *                                interpret the commands handling relative to
	 *                                the step and/or to its subtasks.
	 * @throws IllegalArgumentException When mandatory parameter is missing, or when
	 *                                  can not be cloned regarding immutable entity
	 *                                  parameter.
	 */
	public Step(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status,
			ChainCommandHandler commandHandlingDelegate) throws IllegalArgumentException {
		super(propertyOwner, propertyCurrentValue, status);
		if (commandHandlingDelegate != null)
			setCommandProcessor(commandHandlingDelegate);
		if (this.value != null) {
			// Check existing activation and completion states defined
			try {
				// Else initialize default states
				if (this.value.get(PropertyAttributeKey.ActivityState.name()) == null) {
					this.value.put(PropertyAttributeKey.ActivityState.name(), defaultActivation());
				}
				if (this.value.get(PropertyAttributeKey.CompletionState.name()) == null) {
					this.value.put(PropertyAttributeKey.CompletionState.name(), defaultCompletion());
				}
			} catch (ImmutabilityException ie) {
				// Impossible usage of activation instance that should never arrive
				// Make log for developers
			}

			// Initialize automatic activation events handling when existing
			initializeAutomaticActivationEventsHandling();
		}
	}

	/**
	 * Constructor with automatic initialization of an empty value set (prior
	 * chain).
	 * 
	 * @param propertyOwner           Mandatory entity which is owner of this
	 *                                mutable property chain.
	 * @param propertyCurrentValue    Mandatory current version of value(s)
	 *                                regarding the property. Support included keys
	 *                                with null value. When none activation or
	 *                                completion state is defined, a default state
	 *                                is automatically assigned to this step.
	 * @param status                  Optional history state of this property
	 *                                version. If null,
	 *                                {@link org.cybnity.framework.immutable.HistoryState.Committed}
	 *                                is defined as default state.
	 * @param commandHandlingDelegate Optional processor allowing the step to
	 *                                interpret the commands handling relative to
	 *                                the step and/or to its subtasks.
	 * @param predecessors            Optional anterior steps.
	 * @throws IllegalArgumentException When mandatory parameter is missing, or when
	 *                                  can not be cloned regarding immutable entity
	 *                                  parameter.
	 */
	public Step(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status,
			ChainCommandHandler commandHandlingDelegate,
			@Requirement(reqType = RequirementCategory.Consistency, reqId = "REQ_CONS_3") Step... predecessors) {
		super(propertyOwner, propertyCurrentValue, status, predecessors);
		if (commandHandlingDelegate != null)
			setCommandProcessor(commandHandlingDelegate);
		if (this.value != null) {
			// Check existing activation and completion states defined
			try {
				// Else initialize default states
				if (this.value.get(PropertyAttributeKey.ActivityState.name()) == null) {
					this.value.put(PropertyAttributeKey.ActivityState.name(), defaultActivation());
				}
				if (this.value.get(PropertyAttributeKey.CompletionState.name()) == null) {
					this.value.put(PropertyAttributeKey.CompletionState.name(), defaultCompletion());
				}
			} catch (ImmutabilityException ie) {
				// Impossible usage of activation instance that should never arrive
				// Make log for developers
			}
			// Initialize automatic activation events handling when existing
			initializeAutomaticActivationEventsHandling();
		}
	}

	/**
	 * Initialized the handler that support the monitoring of even types which can
	 * automatically change the current activation status of this step when handled
	 * (e.g detected after a performed change on a followed domain object or group
	 * of object's attributes). This method read the event types supported as
	 * activation sources, create the handler managing the interpretation of each
	 * event types and add this handling capability to this step.
	 * 
	 * The handling of each event type is based on a generic
	 * ConcreteDomainChangeEvent domain events monitored where each one handled
	 * include a type of event occurred (allow to avoid static coded event type).
	 */
	private void initializeAutomaticActivationEventsHandling() {
		// Found existing event types to read that allow to support the automatic
		// activation of this step
		Collection<Enum<?>> activationEventTypes = activationEventTypes();
		if (activationEventTypes != null && !activationEventTypes.isEmpty()) {
			for (Enum<?> activationEventItem : activationEventTypes) {
				if (activationEventItem != null) {
					// Create a handler of the type of event justifying a change of this step
					// activation state

					// TODO create handler of activation state automatic change with set of handler
					// dedicated to interpretation of events types

				}
			}
		}
	}

	/**
	 * Get the default completion state.
	 * 
	 * @return Default completion state defined at zero percentage of progress.
	 * @throws ImmutabilityException When process root instance can't be reused as
	 *                               immutable owner of the completion property.
	 */
	private CompletionState defaultCompletion() throws ImmutabilityException {
		return new CompletionState(ownerReference(), "InitialCompletionStatus",
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
		return new ActivityState(ownerReference(), Boolean.FALSE);
	}

	/**
	 * Define the step command handling processor that allow the step to interpret
	 * the commands handling relative to the step and/or to its subtasks.
	 * 
	 * @param commandHandlingDelegate A handling processor.
	 */
	public void setCommandProcessor(ChainCommandHandler commandHandlingDelegate) {
		this.commandProcessor = commandHandlingDelegate;
	}

	@Override
	public Serializable immutable() throws ImmutabilityException {
		Step copy = new Step(this.owner(), new HashMap<String, Object>(this.currentValue()), this.historyStatus(),
				this.commandProcessor, (Step[]) null);
		// Complete with additional attributes of this complex property
		copy.changedAt = this.occurredAt();
		copy.updateChangesHistory(this.changesHistory());
		return copy;
	}

	/**
	 * Build a definition of property based on property name and value.
	 * 
	 * @param key   Mandatory key name of the property.
	 * @param value Value of the key.
	 * @return A definition of the property.
	 * @throws IllegalArgumentException When any mandatory parameter is missing.
	 */
	static public HashMap<String, Object> buildPropertyValue(PropertyAttributeKey key, Object value)
			throws IllegalArgumentException {
		if (key == null)
			throw new IllegalArgumentException("key parameter is required!");
		HashMap<String, Object> val = new HashMap<>();
		val.put(key.name(), value);
		return val;
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
	 * Get the current value of this complex property.
	 * 
	 * @return An unmodifiable set of valued attributes.
	 */
	public Map<String, Object> currentValue() {
		return Collections.unmodifiableMap(this.value);
	}

	/**
	 * Get the step name.
	 * 
	 * @return A name.
	 */
	public String name() {
		return (String) this.currentValue().get(PropertyAttributeKey.Name.name());
	}

	/**
	 * Get the list of event types that are supported and are causes of automatic
	 * change of activation state to active status.
	 * 
	 * @return A set of event types or null.
	 */
	@SuppressWarnings("unchecked")
	public Collection<Enum<?>> activationEventTypes() {
		Object value = this.currentValue().getOrDefault(PropertyAttributeKey.ActivationEventTypes.name(), null);
		if (value != null) {
			try {
				return Collections.unmodifiableCollection((Collection<Enum<?>>) value);
			} catch (Exception cce) {
				// Invalid type of collection object implemented.
				// Add developer log about coding problem
			}
		}
		return null;
	}

	/**
	 * Get the properties regarding this step.
	 * 
	 * @return A set of properties or null.
	 */
	@SuppressWarnings("unchecked")
	public Collection<Attribute> properties() {
		try {
			return (Collection<Attribute>) this.currentValue().getOrDefault(PropertyAttributeKey.Properties.name(),
					null);
		} catch (Exception cce) {
			// Invalid type of collection object implemented. Add developer log about coding
			// problem
		}
		return null;
	}

	/**
	 * Get the current status of activation of this step.
	 * 
	 * @return A state or null.
	 */
	public ActivityState activation() {
		try {
			return (ActivityState) this.currentValue().getOrDefault(PropertyAttributeKey.ActivityState.name(), null);
		} catch (Exception cce) {
			// Invalid type of collection object implemented. Add developer log about coding
			// problem
		}
		return null;
	}

	/**
	 * Get the current status of completion of this step.
	 * 
	 * @return A state or null.
	 */
	public CompletionState completion() {
		try {
			return (CompletionState) this.currentValue().getOrDefault(PropertyAttributeKey.CompletionState.name(),
					null);
		} catch (Exception cce) {
			// Invalid type of collection object implemented. Add developer log about coding
			// problem
		}
		return null;
	}

	/**
	 * Get the entity reference which is owner of this step.
	 * 
	 * @return An owner reference.
	 * @throws ImmutabilityException If impossible creation of immutable version of
	 *                               instance.
	 */
	public EntityReference ownerReference() throws ImmutabilityException {
		return this.owner().reference();
	}

	/**
	 * Redefined equality evaluation including the owner, the status, the version in
	 * history in terms of functional attributes compared.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		boolean isEquals = false;
		if (obj instanceof Step) {
			try {
				Step compared = (Step) obj;

				// Check if same property owner
				if (compared.owner().equals(this.owner())) {
					// Check if same name value
					if (compared.name().equals(this.name())) {
						// Check if same history version
						if (compared.historyStatus() == this.historyStatus()) {
							isEquals = true;
						}
					}
				}
			} catch (Exception e) {
				// any missing information generating null pointer exception or problem of
				// information read
			}
		}
		return isEquals;
	}

	@Override
	public void addParallelNextHandler(ChainCommandHandler next) {
		if (next != null) {
			if (this.commandProcessor == null) {
				// Initialize the current main processor which was not defined during the
				// construction of this instance
				this.commandProcessor = next;
			} else {
				// Add a parallel handler regarding this step, to the existing processor
				// (defined during instantiation of this step)
				this.commandProcessor.addParallelNextHandler(next);
			}
		}
	}

	/**
	 * Handle the command relative to this step or subtask when command processor
	 * delegation is defined.
	 */
	@Override
	public void handle(Command request) {
		if (request != null && this.commandProcessor != null)
			this.commandProcessor.handle(request);
	}

	/**
	 * Get the ordered sub-steps when existing.
	 * 
	 * @return A list including sub-steps or null.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IState> subStates() {
		try {
			return (List<IState>) this.currentValue().getOrDefault(PropertyAttributeKey.SubSteps.name(),
					/* Return null sub-states by default */ null);
		} catch (Exception cce) {
			// Invalid type of list object implemented. Add developer log about coding
			// problem
		}
		return null;
	}

	/**
	 * Update the current activation state by a new version.
	 * 
	 * @param state Mandatory new version of state.
	 * @throws IllegalArgumentException When mandatory parameter is not defined or
	 *                                  is not valid in terms of minimum conformity.
	 *                                  When state parameter is not regarding same
	 *                                  step identity.
	 * @throws ImmutabilityException    When impossible read of state's owner.
	 */
	public void changeActivation(ActivityState state) throws IllegalArgumentException, ImmutabilityException {
		if (state == null)
			throw new IllegalArgumentException("The state parameter is required!");
		// Check conformity of new version
		checkActivationConformity(state, owner());
		// Update this activation status
		this.value.put(PropertyAttributeKey.ActivityState.name(), state);
	}

	/**
	 * Update the current completion state by a new version.
	 * 
	 * @param state Mandatory new version of state.
	 * @throws IllegalArgumentException When mandatory parameter is not defined or
	 *                                  is not valid in terms of minimum conformity.
	 *                                  When state parameter is not regarding same
	 *                                  step identity.
	 * @throws ImmutabilityException    When impossible read of state's owner.
	 */
	public void changeCompletion(CompletionState state) throws IllegalArgumentException, ImmutabilityException {
		if (state == null)
			throw new IllegalArgumentException("The state parameter is required!");
		// Check conformity of new version
		checkCompletionConformity(state, owner());
		// Update the completion state
		this.value.put(PropertyAttributeKey.CompletionState.name(), state);
	}

	/**
	 * Verify if the state include basic attributes and values and that property
	 * owner is equals to this step.
	 * 
	 * @param state      Mandatory state to check.
	 * @param stepOwning Mandatory owner of the state to compare as a status
	 *                   condition.
	 * @throws IllegalArgumentException When non conformity cause is detected.
	 * @throws ImmutabilityException    When impossible read of description's owner.
	 */
	void checkActivationConformity(ActivityState state, Entity stepOwning)
			throws IllegalArgumentException, ImmutabilityException {
		ActivityState.checkActivationConformity(state, stepOwning);
	}

	/**
	 * Verify if the state include basic attributes and values and that property
	 * owner is equals to this step. For example, a Not-a-Number or negative
	 * completion rate is not a valid value for percentage of completion.
	 * 
	 * @param state      Optional state to check.
	 * @param stepOwning Mandatory owner of the state to compare as a status
	 *                   condition.
	 * @throws IllegalArgumentException When non conformity cause is detected.
	 * @throws ImmutabilityException    When impossible read of description's owner.
	 */
	void checkCompletionConformity(CompletionState state, Entity stepOwning)
			throws IllegalArgumentException, ImmutabilityException {
		CompletionState.checkCompletionConformity(state, stepOwning);
	}
}
