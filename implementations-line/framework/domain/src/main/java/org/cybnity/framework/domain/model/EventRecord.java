package org.cybnity.framework.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.persistence.FactRecord;
import org.cybnity.framework.immutable.persistence.TypeVersion;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.Set;

/**
 * Represent a recorded fact relative to an event which is manageable by a store
 * (e.g as history of facts serialized and tracked).
 * <p>
 * Each recorded fact include the original version of event tracked, and
 * extracted information allowing to store/retrieve it (e.g during specific
 * steps of a process, for hydration of a messaging system).
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Robusteness, reqId = "REQ_ROB_3")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "@class")
public class EventRecord extends FactRecord {

    /**
     * Version of this class type.
     */
    @JsonIgnore
    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(EventRecord.class).hashCode();

    /**
     * Constructor usable by binding framework allowing mapping of instance.
     */
    @JsonCreator
    public EventRecord() {
        super();
    }

    /**
     * Default constructor of a fact record based on a domain event.
     *
     * @param event Mandatory event that is subject of recording.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     * @throws ImmutabilityException    When problem of read regarding immutable
     *                                  contents sourced from the event.
     */
    public EventRecord(DomainEvent event) throws IllegalArgumentException, ImmutabilityException {
        super(event);
        Identifier id = event.identified();
        if (id == null || id.value() == null) {
            throw new IllegalArgumentException("Only identifiable event is eligible to store. Identifier is required!");
        }
    }

    @JsonIgnore
    @Override
    public Serializable immutable() throws ImmutabilityException {
        try {
            return (EventRecord) this.clone();
        } catch (Exception e) {
            throw new ImmutabilityException(e);
        }
    }

    /**
     * Implement the generation of version hash regarding this class type according
     * to a concrete strategy utility service.
     */
    @JsonIgnore
    @Override
    public String versionHash() {
        return String.valueOf(serialVersionUID);
    }

    /**
     * Redefined to reduce its visibility to the binding framework.
     */
    @JsonIgnore
    @Override
    public Set<Field> basedOn() {
        return super.basedOn();
    }

    @JsonProperty
    public Integer getFactId() {
        return super.getFactId();
    }

    @JsonProperty
    protected void setFactId(Integer factId) {
        super.setFactId(factId);
    }

    @JsonProperty
    public int bodyHash() {
        return super.bodyHash();
    }

    @JsonProperty
    protected void setBodyHash(Integer hash) {
        super.setBodyHash(hash);
    }

    @JsonProperty
    public Serializable body() {
        return super.body();
    }

    @JsonProperty
    protected void setBody(Serializable body) {
        super.setBody(body);
    }

    @JsonProperty
    public TypeVersion factTypeVersion() {
        return super.factTypeVersion();
    }

    @JsonProperty
    protected void setFactTypeVersion(TypeVersion factTypeVersion) {
        super.setFactTypeVersion(factTypeVersion);
    }

    @JsonProperty
    public OffsetDateTime recordedAt() {
        return super.recordedAt();
    }

    @JsonProperty
    protected void setRecordedAt(OffsetDateTime recordedAt) {
        super.setRecordedAt(recordedAt);
    }

    @JsonProperty
    @Override
    public OffsetDateTime occurredAt() {
        return super.occurredAt();
    }

    @JsonProperty
    protected void setFactOccurredAt(OffsetDateTime factOccurredAt) {
        super.setFactOccurredAt(factOccurredAt);
    }
}