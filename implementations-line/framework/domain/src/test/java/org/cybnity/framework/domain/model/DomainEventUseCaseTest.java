package org.cybnity.framework.domain.model;

import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountChanged;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountIdentity;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.Identifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test of DomainEvent behaviors regarding its supported requirements.
 *
 * @author olivier
 */
public class DomainEventUseCaseTest {

    @Test
    public void givenUnknownIdentifier_whenConstructor_thenOccurrenceDatedAndVersionDefined() throws Exception {
        UserAccountChanged event = new UserAccountChanged();
        // Verify default contents initialized
        assertNotNull(event.occurredAt()); // When event occurred
        assertNotNull(event.versionHash()); // Which version of event type
        assertNull(event.identified()); // not identified event
    }

    @Test
    public void givenIdentifiedEvent_whenConstructor_thenIdentifierAttached() throws Exception {
        // Create an identifiable event
        Identifier id = new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), "KJGF8765");
        Entity identity = new UserAccountIdentity(id);

        UserAccountChanged event = new UserAccountChanged(identity);
        // Verify that identity is maintained
        assertNotNull(event.identified());
        // Check immutable copy is available and valid
        assertEquals(event.identified(), id, "Lost identity!");
    }

}
