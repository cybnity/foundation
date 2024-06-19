package org.cybnity.framework.immutable.persistence;

import org.cybnity.framework.immutable.IHistoricalFact;
import org.cybnity.framework.immutable.IdentifiableFact;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represent a recorded fact relative to a topic (e.g event, command) which is
 * manageable by a store (e.g as history of facts serialized and tracked).
 * <p>
 * Each recorded fact include the original version of fact tracked, and
 * extracted information allowing to store/retrieve it (e.g during specific
 * steps of a process, for hydration of a messaging system).
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Robusteness, reqId = "REQ_ROB_3")
public class FactRecord implements IHistoricalFact, IUniqueness, Cloneable {

    /**
     * Version of this class type.
     */
    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(FactRecord.class).hashCode();
    private Serializable body;
    private OffsetDateTime factOccurredAt;
    private OffsetDateTime recordedAt;
    private TypeVersion factTypeVersion;
    /**
     * Unique identifier of this record (equals to the original identifier hash code
     * value of the recorded event).
     */
    private Integer factId;
    private Integer bodyHash;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FactRecord that = (FactRecord) o;
        return Objects.equals(factOccurredAt, that.factOccurredAt) && Objects.equals(recordedAt, that.recordedAt) && Objects.equals(factTypeVersion, that.factTypeVersion) && Objects.equals(factId, that.factId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(factOccurredAt, recordedAt, factTypeVersion, factId);
    }

    /**
     * Utility constructor of unidentifiable event which can be used by child class (e.g implementing mapping capability).
     */
    protected FactRecord() {
    }

    /**
     * Default constructor of a fact record based on a domain event.
     *
     * @param originFact Mandatory fact that is subject of recording.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     * @throws ImmutabilityException    When problem of read regarding immutable
     *                                  contents sourced from the event.
     */
    public FactRecord(IHistoricalFact originFact) throws IllegalArgumentException, ImmutabilityException {
        this(originFact, null);
    }

    /**
     * Default constructor of a fact record based on a domain event with potential custom targeted type version of ths record.
     *
     * @param originFact      Mandatory fact that is subject of recording.
     * @param targetedVersion Optional version of the type of fact (e.g different than default type version based on origin fact class).
     * @throws IllegalArgumentException When mandatory parameter is missing.
     * @throws ImmutabilityException    When problem of read regarding immutable
     *                                  contents sourced from the event.
     */
    public FactRecord(IHistoricalFact originFact, TypeVersion targetedVersion) throws IllegalArgumentException, ImmutabilityException {
        if (originFact == null) {
            throw new IllegalArgumentException("Event parameter is required!");
        }
        this.body = originFact;
        this.bodyHash = this.body.hashCode();
        this.factOccurredAt = originFact.occurredAt();
        this.factTypeVersion = (targetedVersion != null) ? targetedVersion : new TypeVersion(originFact.getClass());
        if (IdentifiableFact.class.isAssignableFrom(originFact.getClass())) {
            IdentifiableFact identifiedFact = (IdentifiableFact) originFact;
            Identifier id = identifiedFact.identified();
            if (id != null) {
                this.factId = id.value().hashCode();
            }
        }
        this.recordedAt = OffsetDateTime.now();
    }

    @Override
    public Set<Field> basedOn() {
        Set<Field> uniqueness = new HashSet<>();
        try {
            uniqueness.add(this.getClass().getDeclaredField("bodyHash"));
            uniqueness.add(this.getClass().getDeclaredField("factTypeVersion"));
        } catch (NoSuchFieldException e) {
            // Problem of implementation that shall never be thrown
            // TODO: add log for developer error notification
        }
        return uniqueness;
    }

    /**
     * Get the identifier of this fact that is equals to the value of the original
     * event's identifier value.
     *
     * @return An identifier as hashcode value; or null when this fact is about a
     * no identifiable original event.
     */
    public Integer getFactId() {
        return this.factId;
    }

    /**
     * Define the identifier of this fact that is equals to the value of the original
     * * event's identifier value.
     *
     * @param factId An identifier as hashcode value; or null when this fact is about a
     *               no identifiable original event
     */
    protected void setFactId(Integer factId) {
        this.factId = factId;
    }

    /**
     * Get the hash value regarding the recorded fact. This value allow unique
     * search, comparison or equality identification of equals fact recorded without
     * need to re-instantiation of body content.
     *
     * @return Has value of body content.
     */
    public int bodyHash() {
        if (this.bodyHash == null) {
            this.bodyHash = this.body.hashCode();
        }
        return this.bodyHash;
    }

    /**
     * Define the hash value regarding the recorded fact. This value allow unique
     * search, comparison or equality identification of equals fact recorded without
     * need to re-instantiation of body content.
     *
     * @param hash Has value of body content.
     */
    protected void setBodyHash(Integer hash) {
        this.bodyHash = hash;
    }

    /**
     * Get the original fact which is recorded in this fact.
     *
     * @return Original fact version.
     */
    public Serializable body() {
        return body;
    }

    /**
     * Define the body of this record.
     *
     * @param body A body.
     */
    protected void setBody(Serializable body) {
        this.body = body;
    }

    /**
     * Get the version of the type of origin fact.
     *
     * @return A version of fact type.
     */
    public TypeVersion factTypeVersion() {
        return factTypeVersion;
    }

    /**
     * Define the version of the type of origin fact.
     *
     * @param factTypeVersion A version of fact type.
     */
    protected void setFactTypeVersion(TypeVersion factTypeVersion) {
        this.factTypeVersion = factTypeVersion;
    }

    /**
     * Get the time when this record was instantiated.
     *
     * @return When this record was created.
     */
    public OffsetDateTime recordedAt() {
        return recordedAt;
    }

    /**
     * Define a date of fact record.
     *
     * @param recordedAt A date.
     */
    protected void setRecordedAt(OffsetDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
        try {
            return (FactRecord) this.clone();
        } catch (Exception e) {
            throw new ImmutabilityException(e);
        }
    }

    /**
     * Implement the generation of version hash regarding this class type according
     * to a concrete strategy utility service.
     */
    @Override
    public String versionHash() {
        return new VersionConcreteStrategy().composeCanonicalVersionHash(getClass());
    }

    /**
     * Get the date when the original fact was occurred.
     */
    @Override
    public OffsetDateTime occurredAt() {
        return this.factOccurredAt;
    }

    /**
     * Define a date of teh fact.
     *
     * @param factOccurredAt A date of fact.
     */
    protected void setFactOccurredAt(OffsetDateTime factOccurredAt) {
        this.factOccurredAt = factOccurredAt;
    }
}