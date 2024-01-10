package org.cybnity.framework.application.vertx.common.routing;

import org.cybnity.framework.application.vertx.common.data.UICapabilityChannelSample;
import org.cybnity.framework.domain.event.CollaborationEventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test of recipient list behaviors for management of routing plan.
 */
public class UISRecipientListUseCaseTest {

    private UISRecipientList routingMap;

    @BeforeEach
    void init() {
        routingMap = new UISRecipientList();
    }

    @AfterEach
    void clean() {
        routingMap = null;
    }

    /**
     * Validate that new route definition is stored in routing map and confirmed.
     */
    @Test
    public void givenUndefinedPreviousEqualsRoute_whenAddRoute_thenChangeConfirmed() {
        // Create new route definition and add new in routing plan without previously equals defined event type
        boolean isRoutingMapChanged = routingMap.addRoute(CollaborationEventType.PROCESSING_UNIT_PRESENCE_ANNOUNCED.name(), UICapabilityChannelSample.access_control_pu_presence_announcing.shortName());
        Assertions.assertTrue(isRoutingMapChanged);
    }

    /**
     * Validate that requested deletion of existing route is confirmed.
     */
    @Test
    public void givenPreviousExistingRoute_whenRemoveRoute_thenChangeConfirmed() {
        // Create new route definition and add new in routing plan without previously equals defined event type
        boolean isRoutingMapChanged = routingMap.addRoute(CollaborationEventType.PROCESSING_UNIT_PRESENCE_ANNOUNCED.name(), UICapabilityChannelSample.access_control_pu_presence_announcing.shortName());
        Assertions.assertTrue(isRoutingMapChanged);
        // Prepare a route definition regarding equals event type but identifying a routing path to Null
        isRoutingMapChanged = routingMap.addRoute(CollaborationEventType.PROCESSING_UNIT_PRESENCE_ANNOUNCED.name(), /* representing a "none event responsibility delegate" */ null);
        // Check changed recipients list
        Assertions.assertTrue(isRoutingMapChanged, "Shall have been changed because removed recipients list entry!");
        // Check removed recipient from updated list
        Assertions.assertNull(routingMap.recipient(CollaborationEventType.PROCESSING_UNIT_PRESENCE_ANNOUNCED.name()), "Old recipient shall had been deleted and event type shall not been referenced as supported!");
    }

    /**
     * Validate that new route definition about previous existing route is assigned and confirmed as updated.
     */
    @Test
    public void givenPreviousExistingRoute_whenAddNewRoute_thenChangeConfirmed() {
        // Feed existing route path for an event type in recipient
        UICapabilityChannelSample originPath = UICapabilityChannelSample.access_control_pu_presence_announcing;

        boolean isRoutingMapChanged = routingMap.addRoute(CollaborationEventType.PROCESSING_UNIT_PRESENCE_ANNOUNCED.name(), originPath.shortName());
        // Check initial state as valid
        Assertions.assertTrue(isRoutingMapChanged);
        Assertions.assertNotNull(routingMap.recipient(CollaborationEventType.PROCESSING_UNIT_PRESENCE_ANNOUNCED.name()));
        // Prepare new version of routing definition (recipient path) regarding equals event type and update the routing plan
        isRoutingMapChanged = routingMap.addRoute(/* equals event type */CollaborationEventType.PROCESSING_UNIT_PRESENCE_ANNOUNCED.name(), /* differential recipient path sample */ UICapabilityChannelSample.access_control_in.shortName());
        // Check changed routing map
        Assertions.assertTrue(isRoutingMapChanged);
        // Check that new recipient path replaced the old
        String channel = routingMap.recipient(CollaborationEventType.PROCESSING_UNIT_PRESENCE_ANNOUNCED.name());
        Assertions.assertNotEquals(originPath.shortName(), channel, "Shall have been replaced for same existing event type!");
        Assertions.assertEquals(UICapabilityChannelSample.access_control_in.shortName(), channel);
    }

    /**
     * Validate that existing previous equals route definition in routing plan, is not changed when attempt of update with new definition that have the same values (that does not require change in recipients list).
     */
    @Test
    public void givenPreviousExistingRoute_whenAddEqualsRoute_thenChangeNotConfirmed() {
        // Feed existing route path for an event type in recipient
        UICapabilityChannelSample originPath = UICapabilityChannelSample.access_control_pu_presence_announcing;

        boolean isRoutingMapChanged = routingMap.addRoute(CollaborationEventType.PROCESSING_UNIT_PRESENCE_ANNOUNCED.name(), originPath.shortName());
        // Check initial state as valid
        Assertions.assertTrue(isRoutingMapChanged);
        // Prepare equals version of routing definition (recipient path) regarding equals event type and try to update the routing plan
        isRoutingMapChanged = routingMap.addRoute(/* equals event type */ CollaborationEventType.PROCESSING_UNIT_PRESENCE_ANNOUNCED.name(), /* equals path */ originPath.shortName());
        // Check not changed routing map
        Assertions.assertFalse(isRoutingMapChanged);
    }

}
