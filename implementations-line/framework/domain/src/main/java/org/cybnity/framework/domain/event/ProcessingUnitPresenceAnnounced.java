package org.cybnity.framework.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.IPresenceObservability;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Event about a processing unit presence changed status.
 * For example, when a UI capability component providing a subdomain's set of features is started and become operational (or after its reboot), it can publish this type of event allowing to potential other domain components to be notified as able for collaboration.
 */
@JsonTypeName("ProcessingUnitPresenceAnnounced")
public class ProcessingUnitPresenceAnnounced extends ConcreteDomainChangeEvent {

    @JsonIgnore
    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(ProcessingUnitPresenceAnnounced.class).hashCode();

    /**
     * Collection of routing paths (e.g event type name per recipient channel short name) definitions, based on unmodifiable attributes.
     */
    @JsonProperty
    protected Collection<Attribute> eventsRoutingPaths;

    @JsonCreator
    public ProcessingUnitPresenceAnnounced() {
        super();
    }

    public ProcessingUnitPresenceAnnounced(Enum<?> eventType) {
        super(eventType);
    }

    public ProcessingUnitPresenceAnnounced(Entity identifiedBy) {
        super(identifiedBy);
    }

    public ProcessingUnitPresenceAnnounced(Entity identifiedBy, Enum<?> eventType) {
        super(identifiedBy, eventType);
    }

    public ProcessingUnitPresenceAnnounced(Entity identifiedBy, String eventType) {
        super(identifiedBy, eventType);
    }

    @JsonIgnore
    @Override
    public Serializable immutable() throws ImmutabilityException {
        ProcessingUnitPresenceAnnounced instance = new ProcessingUnitPresenceAnnounced(this.getIdentifiedBy());
        instance.occurredOn = this.occurredAt();

        // Add immutable version of each additional attributes hosted by this event
        EntityReference cmdRef = this.changeCommandReference();
        if (cmdRef != null)
            instance.setChangeCommandRef(cmdRef);
        EntityReference subjectRef = this.changedModelElementReference();
        if (subjectRef != null)
            instance.setChangedModelElementRef(subjectRef);
        if (this.specification != null && !this.specification.isEmpty()) {
            instance.specification = this.specification();
        }
        if (this.eventsRoutingPaths != null && !this.eventsRoutingPaths.isEmpty()) {
            instance.eventsRoutingPaths = this.eventsRoutingPaths();
        }
        return instance;
    }

    /**
     * Define collection of routing path definitions, based on unmodifiable attributes.
     *
     * @param routingDefinitions Description elements relative each supported event type per channel entrypoint (e.g event type name per recipient channel short name).
     */
    public void setEventsRoutingPaths(Collection<Attribute> routingDefinitions) {
        this.eventsRoutingPaths = routingDefinitions;
    }

    /**
     * Add attribute to the routing definitions container.
     *
     * @param routingDefinition Routing definition attribute to add.
     * @return True when defined criteria have been added to the events routing paths and that not previously existing. Else return false.
     */
    @JsonIgnore
    public boolean appendEventRoutingPath(Attribute routingDefinition) {
        if (eventsRoutingPaths == null) {
            // Initialize the attributes container of unmodifiable specification
            eventsRoutingPaths = new ArrayList<>();
        }
        boolean appended = false;
        // Check that equals named attribute is not already defined in the list
        if (!eventsRoutingPaths.contains(routingDefinition)) {
            // Append the criteria
            eventsRoutingPaths.add(routingDefinition);
            appended = true;
        }
        return appended;
    }

    /**
     * Get the current list of routing paths definitions.
     *
     * @return A collection of definitions or null.
     */
    public Collection<Attribute> eventsRoutingPaths() {
        if (this.eventsRoutingPaths != null && !this.eventsRoutingPaths.isEmpty()) {
            // Return immutable version
            return Collections.unmodifiableCollection(this.eventsRoutingPaths);
        }
        return null;
    }

    /**
     * Supported attributes set by a presence announce.
     */
    public enum SpecificationAttribute implements IAttribute {
        /**
         * Attribute regarding the logical identification name of a services provider (e.g UI capability processing unit which ensure domain event treatments).
         */
        SERVICE_NAME,

        /**
         * Attribute regarding a state of an announced presence.
         */
        PRESENCE_STATUS;
    }

    /**
     * Add a specification relative to the service name which is subject of the announced presence statue.
     *
     * @param name A service name. Ignored when null.
     */
    @JsonIgnore
    public void setServiceName(String name) {
        if (name != null) {
            appendSpecification(new Attribute(SpecificationAttribute.SERVICE_NAME.name(), name));
        }
    }

    /**
     * Get the service name which is considered as presence subject.
     *
     * @return A service name or null.
     */
    @JsonIgnore
    public Attribute serviceName() {
        if (this.specification != null) {
            // Search optionally and previously defined value
            return EventSpecification.findSpecificationByName(SpecificationAttribute.SERVICE_NAME.name(), this.specification);
        }
        return null;
    }

    /**
     * Add a specification relative to the state of the announced presence.
     *
     * @param status A status of presence. Ignored when null.
     */
    @JsonIgnore
    public void setPresenceStatus(IPresenceObservability.PresenceState status) {
        if (status != null) {
            appendSpecification(new Attribute(SpecificationAttribute.PRESENCE_STATUS.name(), status.name()));
        }
    }

    /**
     * Get the presence status which is announced.
     *
     * @return A state or null.
     */
    @JsonIgnore
    public Attribute presenceStatus() {
        if (this.specification != null) {
            // Search optionally and previously defined value
            return EventSpecification.findSpecificationByName(SpecificationAttribute.PRESENCE_STATUS.name(), this.specification);
        }
        return null;
    }

}
