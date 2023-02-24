package org.cybnity.framework.immutable;

import java.util.UUID;

import org.cybnity.framework.immutable.sample.Department;
import org.cybnity.framework.immutable.sample.Employee;
import org.cybnity.framework.immutable.sample.IdentifierImpl;
import org.cybnity.framework.immutable.sample.MemberImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of MemberShip behaviors regarding its immutability supported
 * requirements.
 * 
 * @author olivier
 *
 */
public class MembershipUseCaseTest {

    private Group group;
    private Member member;

    @Before
    public void initRelationOrigins() throws Exception {
	group = new Department("Avengers Team", new IdentifierImpl("uid", UUID.randomUUID().toString()));
	member = new Employee("Tony Stark", new IdentifierImpl("uid", UUID.randomUUID().toString()));
    }

    @After
    public void deleteRelationOrigins() throws Exception {
	this.group = null;
	this.member = null;
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenUnknownMember_whenConstructor_thenIllegalArgumentExceptionThrown() {
	new MemberImpl(/* Unknown member violation */ null, group);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenUnknownGroup_whenConstructor_thenIllegalArgumentExceptionThrown() {
	new MemberImpl(member, /* Unknown group violation */ null);
    }

}
