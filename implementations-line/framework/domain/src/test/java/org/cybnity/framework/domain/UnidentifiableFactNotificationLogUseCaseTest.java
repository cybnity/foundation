package org.cybnity.framework.domain;

import org.cybnity.framework.domain.model.sample.writemodel.UserAccountChanged;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.IHistoricalFact;
import org.cybnity.framework.immutable.Identifier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @BeforeEach
    public void initLogOrigin() {
	originalLogId = new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), "KJHG986754");
	unidentifiableObservedFact = new UserAccountChanged(/** none identity */
		null);
    }

    @AfterEach
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

		1, eventLog.identifiers().size(),
		"Invalid quantity of identifying information generated for this log only based on unique technical id!");
	// Verify immutable copy generated
	UnidentifiableFactNotificationLog copy = (UnidentifiableFactNotificationLog) eventLog.immutable();
	// Check equals log id copied
	assertEquals(originalLogId, copy.identified(), "Invalid immutable version of log id!");
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
	List<IHistoricalFact> parents = eventLog.originFacts();
	assertFalse(parents == null || parents.isEmpty(), "Origin facts should be defined!");
	assertEquals(1, parents.size(), "Invalid quantity of original facts saved!");
	for (IHistoricalFact origin : parents) {
	    // Check that unidentifiable fact is equals based on his time occured and nature
	    assertEquals(unidentifiableObservedFact.getClass().getName(),
		    ((IHistoricalFact) origin.immutable()).getClass().getName());
	    assertEquals(unidentifiableObservedFact.occurredAt(), ((IHistoricalFact) origin.immutable()).occurredAt());
	}
    }

}
