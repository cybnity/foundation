package org.cybnity.feature.security_activity_orchestration.domain.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.cybnity.feature.security_activity_orchestration.ChainCommandHandler;
import org.cybnity.feature.security_activity_orchestration.IWorkflowCommandHandler;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.MutableProperty;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a workflow phase (e.g also named process step) that define a state
 * of a working set (e.g unique or multiple actions).
 * 
 * Can be used to define .
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
		/**
		 * Type of step (e.g risk treatment phase; risk review)
		 */
		Type;
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
	 * @param predecessors            Optional prior steps.
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
	 *                                with null value.
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
	}

	/**
	 * Constructor with automatic initialization of an empty value set (prior
	 * chain).
	 * 
	 * @param propertyOwner           Mandatory entity which is owner of this
	 *                                mutable property chain.
	 * @param propertyCurrentValue    Mandatory current version of value(s)
	 *                                regarding the property. Support included keys
	 *                                with null value.
	 * @param status                  Optional history state of this property
	 *                                version. If null,
	 *                                {@link org.cybnity.framework.immutable.HistoryState.Committed}
	 *                                is defined as default state.
	 * @param commandHandlingDelegate Optional processor allowing the step to
	 *                                interpret the commands handling relative to
	 *                                the step and/or to its subtasks.
	 * @param predecessors            Optional prior steps.
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

}
