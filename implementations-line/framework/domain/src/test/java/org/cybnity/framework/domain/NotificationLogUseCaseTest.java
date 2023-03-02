package org.cybnity.framework.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.cybnity.framework.domain.model.sample.writemodel.UserAccountIdentityCreation;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.Identifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of NotificationLog behaviors regarding its supported requirements.
 * 
 * @author olivier
 *
 */
public class NotificationLogUseCaseTest {

    /**
     * UID of a notification log sample.
     */
    private Identifier originalLogId;
    /**
     * Sample of parent fact that is logged by the log sample.
     */
    private Entity userAccountCreationFact;

    @Before
    public void initLogOrigin() {
	originalLogId = new IdentifierStringBased(NotificationLog.IDENTIFIER_NAME, "KJHG986754");
	userAccountCreationFact = new UserAccountIdentityCreation(
		new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), "98765DFGHJKJHG"));
    }

    @After
    public void cleanLogOrigin() {
	originalLogId = null;
	userAccountCreationFact = null;
    }

    /**
     * Check creation of log regarding known logged fact and valid log identifier
     * named.
     */
    @Test
    public void givenValidLogIdentifier_whenConstructor_thenFactValidLogCreated() throws Exception {
	NotificationLog eventLog = new NotificationLog(userAccountCreationFact, originalLogId);
	// Verify default contents initialized
	assertNotNull(eventLog.occurredAt()); // When log occured
	// Check that log id is not modified and without dependency with the logged
	// fact's identifier
	Identifier identifiedBy = eventLog.identified();
	assertEquals("Should not had been modified with any dependency to logged parent's identifier!", originalLogId,
		identifiedBy);
	// Verifi only one identifying information is saved regarding this log
	assertEquals(
		"Invalid quantity of identifying information generated for this log only based on unique technical id!",
		1, eventLog.identifiers().size());
	// Verify immutable copy generated
	NotificationLog copy = (NotificationLog) eventLog.immutable();
	// Check equals log id copied
	assertEquals("Invalid immutable version of log id!", originalLogId, copy.identified());
	// Check equals parent attached
	assertEquals("Invalid immutable version of logged fact!", userAccountCreationFact, copy.parent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenInvalidIdentifierName_whenConstructor_thenIllegalArgumentException() {
	// Create instance of log identifier based on invalid identifier name
	originalLogId = new IdentifierStringBased("any_other_uuid_name", "KJHG986754");
	new NotificationLog(userAccountCreationFact, originalLogId);
    }

}
