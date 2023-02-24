package org.cybnity.framework.immutable.sample;

import java.time.OffsetDateTime;

import org.cybnity.framework.immutable.Group;
import org.cybnity.framework.immutable.Member;
import org.cybnity.framework.immutable.Membership;

/**
 * Example of member implementation class (e.g an assignment regarding a role
 * into an orgoanization).
 * 
 * @author olivier
 *
 */
public class MemberImpl extends Membership {

    public MemberImpl(Member member, Group group) throws IllegalArgumentException {
	super(member, group);
    }

    @Override
    public OffsetDateTime occurredAt() {
	return this.createdAt;
    }

    @Override
    public Object immutable() throws CloneNotSupportedException {
	return new MemberImpl(this.member(), this.group()).createdAt = this.createdAt;
    }
}