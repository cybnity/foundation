package org.cybnity.framework.immutable.persistence;

import java.io.Serializable;
import java.time.OffsetDateTime;

import org.cybnity.framework.immutable.IHistoricalFact;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a recorded fact relative to a topic (e.g event, command) which is
 * manageable by a store (e.g as history of facts serialized and tracked).
 * 
 * Each recorded fact include the original version of fact tracked, and
 * extracted informations allowing to store/retrieve it (e.g during specific
 * steps of a process, for hydratation of a messaging system).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Robusteness, reqId = "REQ_ROB_3")
public class FactRecord implements IHistoricalFact {

    /**
     * Version of this class type.
     */
    private static final long serialVersionUID = 1L;
    private Serializable body;
    private OffsetDateTime factOccuredAt;
    private OffsetDateTime recordedAt;
    private int bodyHash;
    private TypeVersion factTypeVersion;

    /**
     * Default constructor of a fact record based on a domain event.
     * 
     * @param fact Mandatory fact that is subject of recording.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     * @throws ImmutabilityException    When problem of read regarding immutable
     *                                  contents sourced from the event.
     */
    public FactRecord(IHistoricalFact originFact) throws IllegalArgumentException, ImmutabilityException {
	super();
	if (originFact == null) {
	    throw new IllegalArgumentException("Event parameter is required!");
	}
	this.body = originFact;
	this.bodyHash = originFact.hashCode();
	this.factOccuredAt = originFact.occurredAt();
	this.factTypeVersion = new TypeVersion(originFact.getClass());
	this.recordedAt = OffsetDateTime.now();
    }

    /**
     * Get the hash value regarding the recorded fact. This value allow unique
     * search, comparison or equality identication of equals fact recorded without
     * need to reinstantiation of body content.
     * 
     * @return
     */
    public int bodyHash() {
	return this.bodyHash;
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
     * Get the version of the type of origin fact.
     * 
     * @return A version of fact type.
     */
    public TypeVersion factTypeVersion() {
	return factTypeVersion;
    }

    /**
     * Get the time when this record was instantiated.
     * 
     * @return When this record was created.
     */
    public OffsetDateTime recordedAt() {
	return recordedAt;
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
     * Get the date when the original fact was occured.
     */
    @Override
    public OffsetDateTime occurredAt() {
	return this.factOccuredAt;
    }

}
