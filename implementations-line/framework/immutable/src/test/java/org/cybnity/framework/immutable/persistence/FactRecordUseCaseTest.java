package org.cybnity.framework.immutable.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;

import org.cybnity.framework.immutable.NaturalKeyIdentifierGenerator;
import org.cybnity.framework.immutable.StringBasedNaturalKeyBuilder;
import org.cybnity.framework.immutable.sample.Department;
import org.cybnity.framework.immutable.sample.DepartmentChanged;
import org.cybnity.framework.immutable.sample.EntityImpl;
import org.cybnity.framework.immutable.sample.IdentifierImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of FactRecord behaviors regarding its supported requirements.
 * 
 * @author olivier
 *
 */
public class FactRecordUseCaseTest {

    private DepartmentChanged event;

    @Before
    public void initDepartmentChangeEventSample() throws Exception {
	String departmentName = "California";
	StringBasedNaturalKeyBuilder b = new StringBasedNaturalKeyBuilder(departmentName, 10);
	NaturalKeyIdentifierGenerator gen = new NaturalKeyIdentifierGenerator(b);
	gen.build();
	String id = b.getResult();
	Department dep = new Department(departmentName, new IdentifierImpl("uid", id));
	event = new DepartmentChanged(new EntityImpl(new IdentifierImpl("id", UUID.randomUUID().toString())), dep);
    }

    @After
    public void cleanDepartmentChangeEventSample() {
	this.event = null;
    }

    /**
     * Check that as uniqueness requirement supported, the object deliver the good
     * fields.
     * 
     * @throws Exception
     */
    @Test
    public void givenFactRecord_whenUniquenessBasedOn_thenValidFieldsReturned() throws Exception {
	FactRecord fact = new FactRecord(event);
	Set<Field> uniquenessBasedOn = fact.basedOn();
	assertNotNull(uniquenessBasedOn);
	// Verify uniqueness only based on two fields
	assertEquals("Only two fields shall define the uniqueness of a FactRecord!", 2, uniquenessBasedOn.size());
	// Check the chain of uniqueness evaluation
	boolean bodyHashFound = false;
	boolean factTypeVersionFound = false;
	for (Field aField : uniquenessBasedOn) {
	    if (aField.getName().equals("factTypeVersion"))
		factTypeVersionFound = true;
	    if (aField.getName().equals("bodyHash"))
		bodyHashFound = true;
	}
	// Verify found uniqueness combination
	assertTrue("Invalid constraints identified as UNIQUE combined set!", bodyHashFound && factTypeVersionFound);
    }
}
