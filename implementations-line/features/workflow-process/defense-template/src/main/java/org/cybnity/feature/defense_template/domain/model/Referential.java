package org.cybnity.feature.defense_template.domain.model;

import java.io.Serializable;

import org.cybnity.feature.defense_template.domain.IReferential;
import org.cybnity.framework.domain.ValueObject;
import org.cybnity.framework.immutable.IVersionable;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a implementation class regarding a referential.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public class Referential extends ValueObject<String> implements Serializable, IVersionable, IReferential {

	private static final long serialVersionUID = new VersionConcreteStrategy()
			.composeCanonicalVersionHash(Referential.class).hashCode();

	private String acronym;
	private String label;

	/**
	 * Default constructor.
	 * 
	 * @param label   Mandatory name of this referential.
	 * @param acronym Optional acronym of this referential label.
	 * @throws IllegalArgumentException When missing mandatory parameter.
	 */
	public Referential(String label, String acronym) throws IllegalArgumentException {
		super();
		if (label == null || "".equals(label))
			throw new IllegalArgumentException("Label parameter must be defined!");
		this.label = label;
		this.acronym = acronym;
	}

	/**
	 * Implement the generation of version hash regarding this class type according
	 * to a concrete strategy utility service.
	 */
	@Override
	public String versionHash() {
		return new VersionConcreteStrategy().composeCanonicalVersionHash(getClass());
	}

	@Override
	public String acronym() {
		return acronym;
	}

	@Override
	public String label() {
		return label;
	}

	@Override
	public Serializable immutable() throws ImmutabilityException {
		return new Referential(this.label, this.acronym);
	}

	@Override
	public String[] valueHashCodeContributors() {
		if (acronym != null) {
			return new String[] { label, acronym };
		}
		// Only based on mandatory label
		return new String[] { label };
	}

	/**
	 * Implement value equality redefined method that ensure the functional
	 * comparison of the instance about compared types of both objects and their
	 * attributes.
	 */
	@Override
	public boolean equals(Object obj) {
		boolean isEqualsType = super.equals(obj);
		// Compare functional attribute
		boolean isEqualsLabel = ((Referential) obj).label().equals(this.label());
		return isEqualsType && isEqualsLabel;
	}
}
