package org.cybnity.framework.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.cybnity.framework.domain.model.DomainEvent;
import org.cybnity.framework.domain.model.sample.UserAccountCreationCommitted;
import org.cybnity.framework.immutable.HistoricalFact;
import org.cybnity.framework.immutable.Identifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of UnidentifiableFactNotificationLog behaviors regarding its
 * supported requirements.
 * 
 * @author olivier
 *
 */
public class UnidentifiableFactNotificationLogUseCaseTest {

    /**
     * UID of a notification log sample.
     */
    private Identifier originalLogId;

    /**
     * Sample of unidentifiable parent fact that is logged by the log sample.
     */
    private DomainEvent unidentifiableObservedFact;

    @Before
    public void initLogOrigin() {
	originalLogId = new EventIdentifierStringBased(NotificationLog.IDENTIFIER_NAME, "KJHG986754");
	unidentifiableObservedFact = new UserAccountCreationCommitted(/** none identity */
		null);
    }

    @After
    public void cleanLogOrigin() {
	originalLogId = null;
	unidentifiableObservedFact = null;
    }

    /**
     * Check creation of log regarding unidentified logged fact and valid log
     * identifier named.
     */
    @Test
    public void givenValidLogIdentifier_whenConstructor_thenFactValidLogCreated() throws Exception {
	UnidentifiableFactNotificationLog eventLog = new UnidentifiableFactNotificationLog(originalLogId);
	// Verify default contents initialized
	assertNotNull(eventLog.occurredAt()); // When log occured
	// Check that log id is not modified and without dependency with the logged
	// fact's identifier
	Identifier identifiedBy = eventLog.identified();
	assertNotNull(identifiedBy);
	// Verifi only one identifying information is saved regarding this log
	assertEquals(
		"Invalid quantity of identifying information generated for this log only based on unique technical id!",
		1, eventLog.identifiers().size());
	// Verify immutable copy generated
	UnidentifiableFactNotificationLog copy = (UnidentifiableFactNotificationLog) eventLog.immutable();
	// Check equals log id copied
	assertEquals("Invalid immutable version of log id!", originalLogId, copy.identified());
	// Check that origin causes list of empty by default
	assertTrue(eventLog.originFacts().isEmpty());
    }

    /**
     * Check support of unidentifiable historical facts originally caused the log
     * are saved.
     */
    @Test
    public void givenKnownOriginFacts_whenReadParent_thenOriginsMatches() throws Exception {
	// Create log about not identified origin fact
	UnidentifiableFactNotificationLog eventLog = new UnidentifiableFactNotificationLog(originalLogId,
		unidentifiableObservedFact);
	// Read origin facts
	List<HistoricalFact> parents = eventLog.originFacts();
	assertFalse("Origin facts should be defined!", parents == null || parents.isEmpty());
	assertEquals("Invalid quantity of original facts saved!", 1, parents.size());
	for (HistoricalFact origin : parents) {
	    // Check that unidentifiable fact is equals based on his time occured and nature
	    assertEquals(unidentifiableObservedFact.getClass().getName(),
		    ((HistoricalFact) origin.immutable()).getClass().getName());
	    assertEquals(unidentifiableObservedFact.occurredAt(), ((HistoricalFact) origin.immutable()).occurredAt());
	}
    }

}
