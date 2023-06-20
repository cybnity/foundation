package org.cybnity.feature.security_activity_orchestration.domain.model;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import org.cybnity.feature.security_activity_orchestration.ITemplate;
import org.cybnity.framework.IContext;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.model.Aggregate;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a workflow based on steps (e.g risk management process) realizable
 * by an actor and specifying an organizational model framing activities.
 * 
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
	 * Default constructor.
	 * 
	 * @param predecessor Mandatory parent of this process domain entity as
	 *                    aggregate. For example, can be a created social entity
	 *                    fact (see org.cybnity.framework.domain.model.SocialEntity}
	 *                    which shall exist before to create this process related to
	 *                    the organization.
	 * @param id          Unique and optional identifier of this process. For
	 *                    example, identifier is required when the process shall be
	 *                    persistent. Else, can be without identity when not
	 *                    persistent process.
	 * @param description Mandatory description of this process. A name attribute is
	 *                    required as minimum description attribute.
	 * @throws IllegalArgumentException When any mandatory parameter is missing.
	 *                                  When a problem of immutability is occurred.
	 *                                  When predecessor mandatory parameter is not
	 *                                  defined or without defined identifier.
	 */
	public Process(Entity predecessor, Identifier id, ProcessDescriptor description) throws IllegalArgumentException {
		super(predecessor, id);
		if (description == null) {
			throw new IllegalArgumentException("Description parameter is required!");
		} else {
			checkDescriptionConformity(description);
		}
		this.description = description;
	}

	/**
	 * Specific partial constructor.
	 * 
	 * @param predecessor Mandatory parent of this process domain entity as
	 *                    aggregate. For example, can be a created social entity
	 *                    fact (see org.cybnity.framework.domain.model.SocialEntity}
	 *                    which shall exist before to create this process related to
	 *                    the organization.
	 * @param identifiers Optional set of identifiers of this entity, that contains
	 *                    non-duplicable elements. For example, identifier is
	 *                    required when the process shall be persistent. Else, can
	 * @param description Mandatory description of this process. A name attribute is
	 *                    required as minimum description attribute. be without
	 *                    identity when not persistent process.
	 * @throws IllegalArgumentException When identifiers parameter is null or each
	 *                                  item does not include name and value. When
	 *                                  predecessor mandatory parameter is not
	 *                                  defined or without defined identifier.
	 */
	public Process(Entity predecessor, LinkedHashSet<Identifier> identifiers, ProcessDescriptor description)
			throws IllegalArgumentException {
		super(predecessor, identifiers);
		if (description == null) {
			throw new IllegalArgumentException("Description parameter is required!");
		} else {
			checkDescriptionConformity(description);
		}
		this.description = description;
	}

	@Override
	public Serializable immutable() throws ImmutabilityException {
		Process copy = new Process(this.parent(), new LinkedHashSet<>(this.identifiers()),
				(ProcessDescriptor) this.description.immutable());
		// Complete with additional attributes of this complex aggregate
		copy.createdAt = this.occurredAt();
		return copy;
	}

	/**
	 * Redefined equality evaluation including the property owner, the status, the
	 * version in history in terms of functional attributes compared.
	 */
	@Override
	public boolean equals(Object obj) {
		boolean isEquals = false;
		// Comparison based on owner, status, version functional attributes
		if (super.equals(obj) && obj instanceof Process) {
			try {
				Process compared = (Process) obj;
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
	 * Implement the generation of version hash regarding this class type according
	 * to a concrete strategy utility service.
	 */
	@Override
	public String versionHash() {
		return new VersionConcreteStrategy().composeCanonicalVersionHash(getClass());
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
	public void setDescription(ProcessDescriptor description) throws IllegalArgumentException, ImmutabilityException {
		if (description == null)
			throw new IllegalArgumentException("The description parameter is required!");
		// Check conformity of new version
		checkDescriptionConformity(description);
		// Check that owner of the new description is equals to this process identity
		if (description.owner() == null || !description.owner().equals(this.description.owner()))
			throw new IllegalArgumentException(
					"The owner of the new description shall be equals to this process identity!");
		// Update this process description
		this.description = description;
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
	 * Verify if the description include the minimum set of attributes required to
	 * define the process.
	 * 
	 * @param description Description instance hosting the attributes to verify.
	 * @throws IllegalArgumentException When description instance is not valid.
	 */
	private void checkDescriptionConformity(ProcessDescriptor description) throws IllegalArgumentException {
		if (description != null) {
			// Check that minimum name attribute is defined into the description
			String processName = description.name();
			if (processName == null || "".equals(processName))
				throw new IllegalArgumentException("Process name is required from description!");
		}
	}

	@Override
	public void handle(Command change, IContext ctx) throws IllegalArgumentException {
		// TODO coder traitement de la demande de changement selon l'attribute ciblé ou
		// l'état de progression du process ou de ses sous-états
		// Utiliser un delegate pour cet aggregate de type CommandHandlingService
	}

	@Override
	public Set<String> handledCommandTypeVersions() {
		return null;
	}
}
