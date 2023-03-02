package org.cybnity.framework.domain;

/**
 * A service in a domain is a stateless operation that fulfills a
 * domain-specific task. Often the best indication that a Service is created in
 * a domain model is when the operation to perform feels out of place as a
 * method on an Aggregate or a Value Object.
 * 
 * Domain Service is usable to perform a significant business process, to
 * transform a domain object from one composition to another, or to calculate a
 * Value requiring input from more than one domain object.
 * 
 * @author olivier
 *
 */
public interface IService {

}
