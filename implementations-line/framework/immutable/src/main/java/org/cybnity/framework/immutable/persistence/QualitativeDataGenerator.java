package org.cybnity.framework.immutable.persistence;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a producer of qualitative data (e.g generic fat ready to be stored
 * into an event store).
 * 
 * This building process execute the instantiation of an object with application
 * of several quality rules ensuring the delivery of a data including
 * completeness, consistency, conformity, accuracy, integrity, timeliness.
 * 
 * It can be use in complement of an ACID model (Atomicity, Consistency,
 * Isolation, Durability), which is a set of principles used to guarantee the
 * reliability of database transactions.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Robusteness, reqId = "REQ_ROB_3")
public class QualitativeDataGenerator {

    private QualitativeDataBuilder builder;

    /**
     * Default constructor of generator managing the build of an information based
     * on quality concrete builder.
     * 
     * @param builder Mandatory builder to use during the generation of data, and
     *                that implement quality rules.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public QualitativeDataGenerator(QualitativeDataBuilder builder) throws IllegalArgumentException {
	if (builder == null)
	    throw new IllegalArgumentException("Builder parameter is required!");
	this.builder = builder;
    }

    /**
     * Prepare the instantiation of an object instance respecting the data quality
     * rules with execution of the build pattern process.
     */
    public void build() {
	// Execute each step of the data quality process
	builder.makeCompleteness();
	builder.makeConsistency();
	builder.makeConformity();
	builder.makeAccuracy();
	builder.makeIntegrity();
	builder.makeTimeliness();
    }
}
