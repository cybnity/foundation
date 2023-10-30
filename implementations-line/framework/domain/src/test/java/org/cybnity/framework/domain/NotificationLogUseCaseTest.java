package org.cybnity.framework.domain;

import org.cybnity.framework.domain.model.NotificationLog;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountIdentityCreation;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.Identifier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

	@BeforeEach
	public void initLogOrigin() {
		originalLogId = new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), "KJHG986754");
		userAccountCreationFact = new UserAccountIdentityCreation(
				new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), "98765DFGHJKJHG"));
	}

	@AfterEach
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
		assertNotNull(eventLog.occurredAt()); // When log occurred
		// Check that log id is not modified and without dependency with the logged
		// fact's identifier
		Identifier identifiedBy = eventLog.identified();
		assertEquals(originalLogId, identifiedBy,
				"Should not had been modified with any dependency to logged parent's identifier!");
		// Verify only one identifying information is saved regarding this log
		assertEquals(

				1, eventLog.identifiers().size(),
				"Invalid quantity of identifying information generated for this log only based on unique technical id!");
		// Verify immutable copy generated
		NotificationLog copy = (NotificationLog) eventLog.immutable();
		// Check equals log id copied
		assertEquals(originalLogId, copy.identified(), "Invalid immutable version of log id!");
		// Check equals parent attached
		assertEquals(userAccountCreationFact, copy.parent(), "Invalid immutable version of logged fact!");
	}

	@Test
	public void givenInvalidIdentifierName_whenConstructor_thenIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> {
			// Create instance of log identifier based on invalid identifier name
			originalLogId = new IdentifierStringBased("any_other_uuid_name", "KJHG986754");
			new NotificationLog(userAccountCreationFact, originalLogId);
		});
	}

}
