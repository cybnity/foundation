package org.cybnity.framework.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.cybnity.framework.Context;
import org.cybnity.framework.IContext;
import org.junit.jupiter.api.Test;

/**
 * Unit test of Context behaviors regarding its supported requirements.
 * 
 * @author olivier
 *
 */
public class ContextUseCaseTest {

    @Test
    public void givenManagedResource_whenGotResourceReferenceChanged_thenOriginalReferenceMaintainedInContext()
	    throws Exception {
	IContext ctx = new Context();
	String propertyInstance = "managedValue";
	String resourceName = "id1";
	// Create a resource and save in context
	ctx.addResource(propertyInstance, resourceName, false);

	// Verify that instance from name confirmed that is managed by context
	Object instanceSafeRef = ctx.get(resourceName);
	assertNotNull(instanceSafeRef, "Shall be found from context bundle!");

	// Check that returned instance reference is the same pointer than the
	// original resource reference initially stored
	assertTrue(instanceSafeRef == propertyInstance,
		"Safe reference shall be equals with original pointer before changed to other memore space!");
	assertEquals(propertyInstance, (String) instanceSafeRef,
		"Referenced value in memory space shall be the same pointed by the 2 references!");

	// Change the external reference (simulate a remove by user of the context, that
	// shall not generate impact on the references managed by the context's bundle)
	instanceSafeRef = new String("original resource violation");
	// Verify changed link to the memory space by extenal reference
	assertNotEquals(propertyInstance, instanceSafeRef,
		"Values shall be in different memory location pointed by references!");
	assertFalse(instanceSafeRef == propertyInstance,
		"Safe reference shall had been changed to the new memory space of new value!");

	// Simulate eligible to garbage collector with null
	instanceSafeRef = null;
	// Check original value managed by context is always not changed
	Object otherRefToOriginalResource = ctx.get(new String(resourceName));
	assertTrue(otherRefToOriginalResource == propertyInstance,
		"Original reference shall had been maintained by context!");
	assertEquals(propertyInstance, (String) otherRefToOriginalResource,
		"Referenced value in memory space shall be the same pointed by the 2 references!");
    }
}
