package org.cybnity.framework.immutable.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Set;

import org.cybnity.framework.immutable.sample.CreateDepartment;
import org.cybnity.framework.immutable.sample.DepartmentChanged;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of FactEdge behaviors regarding its supported requirements.
 * 
 * @author olivier
 *
 */
public class FactEdgeUseCaseTest {

    private FactType predecessorInitialFactType;
    private FactType successorRelatedFactType;

    @Before
    public void initFactTypes() {
	successorRelatedFactType = new FactType(DepartmentChanged.class.getSimpleName());
	predecessorInitialFactType = new FactType(CreateDepartment.class.getSimpleName());
    }

    @After
    public void cleanFactTypes() {
	this.predecessorInitialFactType = null;
	this.successorRelatedFactType = null;
    }

    /**
     * Check that as uniqueness requirement supported, the object deliver the good
     * fields.
     * 
     * @throws Exception
     */
    @Test
    public void givenSuccessorAndPredecessorAndRelationRole_whenUniquenessBasedOn_thenValidFieldsReturned()
	    throws Exception {
	RelationRole role = new RelationRole("source->target", predecessorInitialFactType, successorRelatedFactType);
	FactEdge edge = new FactEdge(successorRelatedFactType.id(), predecessorInitialFactType.id(), role);

	Set<Field> uniquenessBasedOn = edge.basedOn();
	assertNotNull(uniquenessBasedOn);
	// Verify uniqueness only based on three fields
	assertEquals("Only two fields shall define the uniqueness of a FactEdge!", 3, uniquenessBasedOn.size());
	// Check the chain of uniqueness evaluation
	boolean successorIdFound = false;
	boolean predecessorIdFound = false;
	boolean factsRelationTypeFound = false;
	for (Field aField : uniquenessBasedOn) {
	    if (aField.getName().equals("predecessorId"))
		predecessorIdFound = true;
	    if (aField.getName().equals("successorId"))
		successorIdFound = true;
	    if (aField.getName().equals("factsRelationType"))
		factsRelationTypeFound = true;
	}
	// Verify found uniqueness combination
	assertTrue("Invalid constraints identified as UNIQUE combined set!",
		successorIdFound && predecessorIdFound && factsRelationTypeFound);
    }
}
