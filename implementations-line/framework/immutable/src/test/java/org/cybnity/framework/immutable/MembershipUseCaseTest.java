package org.cybnity.framework.immutable;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.cybnity.framework.immutable.sample.Department;
import org.cybnity.framework.immutable.sample.Employee;
import org.cybnity.framework.immutable.sample.IdentifierImpl;
import org.cybnity.framework.immutable.sample.MemberImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test of MemberShip behaviors regarding its immutability supported
 * requirements.
 * 
 * @author olivier
 *
 */
public class MembershipUseCaseTest {

    private IGroup group;
    private IMember member;

    @BeforeEach
    public void initRelationOrigins() throws Exception {
	group = new Department("Avengers Team", new IdentifierImpl("uid", UUID.randomUUID().toString()));
	member = new Employee("Tony Stark", new IdentifierImpl("uid", UUID.randomUUID().toString()));
    }

    @AfterEach
    public void deleteRelationOrigins() throws Exception {
	this.group = null;
	this.member = null;
    }

    @Test
    public void givenUnknownMember_whenConstructor_thenIllegalArgumentExceptionThrown() {
	assertThrows(IllegalArgumentException.class, () -> {
	    new MemberImpl(/* Unknown member violation */ null, group);
	});
    }

    @Test
    public void givenUnknownGroup_whenConstructor_thenIllegalArgumentExceptionThrown() {
	assertThrows(IllegalArgumentException.class, () -> {
	    new MemberImpl(member, /* Unknown group violation */ null);
	});
    }

}
