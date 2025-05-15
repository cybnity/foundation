package org.cybnity.framework.immutable.persistence;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a Builder pattern implementation of data quality regarding an
 * object to instantiate.
 * <p>
 * Several quality rules ensuring the delivery of a data including completeness,
 * consistency, conformity, accuracy, integrity, timeliness.
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Robusteness, reqId = "REQ_ROB_3")
public abstract class QualitativeDataBuilder {

    /**
     * Completeness is defined as expected comprehensiveness. Data can be complete
     * even if optional data is missing. As long as the data meets the expectations
     * then the data is considered complete.
     * <p>
     * For example, a customer’s first name and last name are mandatory but middle
     * name is optional; so a record can be considered complete even if a middle
     * name is not available.
     * <p>
     * Questions you can ask yourself:
     * <p>
     * Is all the requisite information available?
     * <p>
     * Do any data values have missing elements? Or are they in an unusable state?
     */
    public abstract void makeCompleteness();

    /**
     * Consistency means data across all systems reflects the same information and
     * are in sync with each other across the enterprise. Examples:
     * <p>
     * A business unit status is closed but there are sales for that business unit.
     * <p>
     * Employee status is terminated but pay status is active.
     * <p>
     * Questions you can ask yourself:
     * <p>
     * Are data values the same across the data sets?
     * <p>
     * Are there any distinct occurrences of the same data instances that provide
     * conflicting information?
     */
    public abstract void makeConsistency();

    /**
     * Conformity means the data is following the set of standard data definitions
     * like data type, size and format. For example, date of birth of customer is in
     * the format “mm/dd/yyyy”
     * <p>
     * Questions you can ask yourself:
     * <p>
     * Do data values comply with the specified formats?
     * <p>
     * If so, do all the data values comply with those formats?
     * <p>
     * Maintaining conformance to specific formats is important.
     */
    public abstract void makeConformity();

    /**
     * Accuracy is the degree to which data correctly reflects the real world object
     * OR an event being described. Examples:
     * <p>
     * Sales of the business unit are the real value. Address of an employee in the
     * employee database is the real address.
     * <p>
     * Questions you can ask yourself:
     * <p>
     * Do data objects accurately represent the “real world” values they are
     * expected to model?
     * <p>
     * Are there incorrect spellings of product or person names, addresses, and even
     * untimely or not current data?
     * <p>
     * These issues can impact operational and advanced analytics applications.
     */
    public abstract void makeAccuracy();

    /**
     * Integrity means validity of data across the relationships and ensures that
     * all data in a database can be traced and connected to other data.
     * <p>
     * For example, in a customer database, there should be a valid customer,
     * addresses and relationship between them. If there is an address relationship
     * data without a customer then that data is not valid and is considered an
     * orphaned record.
     * <p>
     * Ask yourself:
     * <p>
     * Is there are any data missing important relationship linkages?
     * <p>
     * The inability to link related records together may actually introduce
     * duplication across your systems.
     */
    public abstract void makeIntegrity();

    /**
     * Timeliness references whether information is available when it is expected
     * and needed. Timeliness of data is very important. This is reflected in:
     * <p>
     * Companies that are required to publish their quarterly results within a given
     * frame of time Customer service providing up-to date information to the
     * customers Credit system checking in real-time on the credit card account
     * activity
     * <p>
     * The timeliness depends on user expectation. Online availability of data could
     * be required for room allocation system in hospitality, but nightly data could
     * be perfectly acceptable for a billing system.
     */
    public abstract void makeTimeliness();

    /**
     * Get the instance of object built with data quality rules application.
     *
     * @return The created instance created during the executed build process.
     * @throws Exception If process was not executed before to acquire the result.
     */
    public abstract Object getResult() throws Exception;
}
