package org.cybnity.framework.application.vertx.common.routing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Identify the correct recipient based on an event's content.
 * A recipient channel is defined for each event type supported by a capability domain.
 * Identify each stream fact type name, with the name of a domain capability entry point (e.g redis stream channel ensuring main entrypoint of a capability domain) allowing to bridge an event forwarding.
 * The goal of a recipients list (e.g defining domain UI capability entrypoint channel for type of event) is to define the routing path in a generic way based on a domain's capabilities api supporting the event type naming (between the API IO endpoint and the UIS space).
 * <p>
 * It's an implementation of architectural pattern named "Recipient List".
 * Generally, one common recipient channel (e.g Redis entrypoint materializing domain capability IO) is supporting several event types.
 */
public class RouteRecipientList {

    /**
     * Referential of routes dedicated to specific event types which can be managed by Users Interactions Space's channels.
     * Each item is defined by an event type name (key based on DomainEventType.name()) and a UIS recipient path (value identifying a topic/stream name based on UICapabilityChannel.shortName()).
     */
    private final Map<String, String> routingMap;

    /**
     * Default constructor initializing the routing table.
     */
    public RouteRecipientList() {
        // Initialize the routing destination tables that link an event bus channel with
        // a redis channel
        // Concurrent access and upgrade shall be supported
        routingMap = new ConcurrentHashMap<>();
    }

    /**
     * Add a route definition to the current routing plan and confirm update status resulting.
     *
     * @param supportedFactEventTypeName Mandatory fact type name declared as supported by the route recipient.
     * @param recipient                  Optional recipient declared as supporting the event type.
     * @return True when routing plan have been modified (e.g newly defined, existing modified, previous existing deleted) or false when no change applied (e.g previous equals route definition was existing).
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public boolean addRoute(String supportedFactEventTypeName, String recipient) throws IllegalArgumentException {
        if (supportedFactEventTypeName == null || supportedFactEventTypeName.isEmpty())
            throw new IllegalArgumentException("supportedFactEventTypeName parameter is required!");
        // Check if a change is required into the routing map (and that event type is not already mapped to an existing equals route)
        boolean routingPlanChanged = false;

        // --- ROUTING PLAN CHANGE RULES : a change detected status is valid only when ---
        String previousRecipientValue;
        if (recipient == null || recipient.isEmpty()) {
            // - ROUTE DELETION: event type announced is not mapped to a route path, but is equals to "" or null (empty route equals to route deletion about this event type) to announce that event type is not covered by an entrypoint supported by the event provider (e.g previously supported event type is deleted by the recipient), generating a deletion of the previous known event type route in the recipients list
            // Remove previous route definition from the routing plan
            previousRecipientValue = this.routingMap.remove(supportedFactEventTypeName);
            if (previousRecipientValue != null) {
                // Confirm performed change about previous existing supported event type that have been removed from routing plan
                routingPlanChanged = true;
            }
        } else {
            // Add or update with new routing path
            previousRecipientValue = this.routingMap.put(supportedFactEventTypeName, recipient);
            if (previousRecipientValue == null) {
                // - ROUTE DEFINITION: event type was not already supported by routing map and have been defined by new definition
                // So confirm performed change
                routingPlanChanged = true;
            } else {
                // Verify if event type and route previously defined was already equals (the update does not make any change in routing map)
                if (previousRecipientValue.equals(recipient)) {
                    // - KNOWN EQUALS ROUTE ALREADY DEFINED
                    // So confirm that no change have been performed regarding previous already defined equals route
                } else {
                    // - ROUTE RE-ASSIGNMENT: event type was already supported by another routing path that is modified for a new one announced by the received event
                    // So confirm performed change
                    routingPlanChanged = true;
                }
            }
        }
        return routingPlanChanged;// Confirm status of modification applied when performed
    }

    /**
     * Find a route to Users Interactions Space's channel supporting a type of event.
     *
     * @param aFactEventTypeName Name of fact event type which shall be supported by a channel.
     * @return A recipient routing path or null.
     */
    public String recipient(String aFactEventTypeName) {
        if (aFactEventTypeName != null && !aFactEventTypeName.isEmpty()) {
            // Find existing routing path
            return routingMap.get(aFactEventTypeName);
        }
        return null;
    }

    /**
     * Get the list of event types that are supported by recipients.
     *
     * @return An immutable collection of event type names, or empty list.
     */
    public Collection<String> supportedEventTypeNames() {
        Collection<String> supportedEventTypeNames = new ArrayList<>(routingMap.size());
        // Identify all the events types supported by the recipients
        for (String eventType : routingMap.keySet()) {
            if (eventType != null && !eventType.isEmpty()) supportedEventTypeNames.add(eventType);
        }
        // Return an immutable version of the list
        return Collections.unmodifiableCollection(supportedEventTypeNames);
    }

    /**
     * Get the quantity of current supported routing paths as recipients.
     * @return A count.
     */
    public int routesCount() {
        return this.routingMap.size();
    }
}
