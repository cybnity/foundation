package org.cybnity.framework.immutable.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;

import org.cybnity.framework.immutable.NaturalKeyIdentifierGenerator;
import org.cybnity.framework.immutable.StringBasedNaturalKeyBuilder;
import org.cybnity.framework.immutable.sample.Department;
import org.cybnity.framework.immutable.sample.DepartmentChanged;
import org.cybnity.framework.immutable.sample.EntityImpl;
import org.cybnity.framework.immutable.sample.IdentifierImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test of FactRecord behaviors regarding its supported requirements.
 * 
 * @author olivier
 *
 */
public class FactRecordUseCaseTest {

    private DepartmentChanged event;

    @BeforeEach
    public void initDepartmentChangeEventSample() throws Exception {
	String departmentName = "California";
	StringBasedNaturalKeyBuilder b = new StringBasedNaturalKeyBuilder(departmentName, 10);
	NaturalKeyIdentifierGenerator gen = new NaturalKeyIdentifierGenerator(b);
	gen.build();
	String id = b.getResult();
	Department dep = new Department(departmentName, new IdentifierImpl("uid", id));
	event = new DepartmentChanged(new EntityImpl(new IdentifierImpl("id", UUID.randomUUID().toString())), dep);
    }

    @AfterEach
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
	assertEquals(2, uniquenessBasedOn.size(), "Only two fields shall define the uniqueness of a FactRecord!");
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
	assertTrue(bodyHashFound && factTypeVersionFound, "Invalid constraints identified as UNIQUE combined set!");
    }

}
