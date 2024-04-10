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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Represent a snapshot of object (e.g aggregate version) which is manageable by a store
 * (e.g as history of snapshots serialized and re-hydrated).
 * <p>
 * Each recorded fact include the original version of object tracked, and
 * extracted information allowing to store/retrieve it (e.g during specific
 * steps of a process, for backup or hydration from a store).
 *
 * @author olivier
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "@class")
public class ConcreteSnapshot extends FactRecord implements ISnapshot {

    /**
     * Version of this class type.
     */
    @JsonIgnore
    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(ConcreteSnapshot.class).hashCode();

    /**
     * Commit identifier equals to the last change id applied to the source object.
     */
    private String commitVersion;

    /**
     * Constructor usable by binding framework allowing mapping of instance.
     */
    @JsonCreator
    public ConcreteSnapshot() {
        super();
    }

    /**
     * Default constructor of a snapshot based on a serializable object.
     * The commit version is defined from the aggregate last item of its changeEvents.
     *
     * @param originFullStateObject Mandatory origin object that is subject of snapshot.
     * @throws IllegalArgumentException When mandatory parameter is missing. When none change event is found from the aggregate and make impossible definition of the commit version.
     * @throws ImmutabilityException    When problem of read regarding immutable
     *                                  contents sourced from the originFullStateObject.
     */
    public ConcreteSnapshot(Aggregate originFullStateObject) throws IllegalArgumentException, ImmutabilityException {
        super(originFullStateObject);
        Identifier id = originFullStateObject.identified();
        if (id == null || id.value() == null) {
            throw new IllegalArgumentException("Only identifiable originFullStateObject is eligible to snapshot. Identifier is required!");
        }
        // Define the commit version of the snapshot based on last change of the orign object
        List<DomainEvent> lastChanges = originFullStateObject.changeEvents();
        if (lastChanges.isEmpty())
            throw new IllegalArgumentException("A minimum one change event is required to identify the commit version of the aggregate!");
        DomainEvent originObjLastChange = lastChanges.get(lastChanges.size() - 1); // Read the last change event
        commitVersion = originObjLastChange.identified().value().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ConcreteSnapshot that = (ConcreteSnapshot) o;
        return Objects.equals(commitVersion, that.commitVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), commitVersion);
    }

    @JsonIgnore
    @Override
    public Serializable immutable() throws ImmutabilityException {
        try {
            return (ConcreteSnapshot) this.clone();
        } catch (Exception e) {
            throw new ImmutabilityException(e);
        }
    }

    @Override
    public Date taken() {
        // Read the date of snapshot record moment
        return Date.from(this.recordedAt().toInstant());
    }

    @Override
    public String commitVersion() {
        return this.commitVersion;
    }

    @JsonIgnore
    @Override
    public String versionedObjectUID() {
        Integer factUID = super.getFactId();
        if (factUID != null)
            return super.getFactId().toString();
        return null;
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