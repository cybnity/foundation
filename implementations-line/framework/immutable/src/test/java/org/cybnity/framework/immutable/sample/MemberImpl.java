package org.cybnity.framework.immutable.sample;

import org.cybnity.framework.immutable.IGroup;
import org.cybnity.framework.immutable.IMember;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.Membership;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * Example of member implementation class (e.g an assignment regarding a role
 * into an orgoanization).
 * 
 * @author olivier
 *
 */
public class MemberImpl extends Membership {

    private static final long serialVersionUID = 1L;

    public MemberImpl(IMember member, IGroup group) throws IllegalArgumentException {
	super(member, group);
    }

    @Override
    public OffsetDateTime occurredAt() {
	return this.createdAt;
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	return new MemberImpl(this.member(), this.group()).createdAt = this.createdAt;
    }

    /**
     * Implement the generation of version hash regarding this class type according
     * to a concrete strategy utility service.
     */
    @Override
    public String versionHash() {
	return new VersionConcreteStrategy().composeCanonicalVersionHash(getClass());
    }
}