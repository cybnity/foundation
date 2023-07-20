package org.cybnity.framework.domain;

/**
 * Represent a flat structure that contain no business logic but contain only
 * storage, accessors and eventually methods related to serialization or
 * parsing.
 * 
 * It's a data container which is used to transport data between layers and
 * tiers. It mainly contains of attributes.
 * 
 * Declares and enforces a schema for data: names and types. Offers no
 * guarantees about correctness of values.
 * 
 * DTOs also help when the domain model is composed of many different objects
 * and the presentation model needs all their data at once, or they can even
 * reduce roundtrip between client and server.
 * 
 * With DTOs, different views can be built from domain models, allowing us to
 * create other representations of the same domain (e.g ReadModel) but
 * optimizing them to the clients' needs without affecting the related domain
 * design.
 * 
 * Generally used via an assembler on a server-side to transfer data between any
 * domain objects and services.
 * 
 * Another benefit is the encapsulation of the serialization's logic (the
 * mechanism that translates the object structure and data to a specific format
 * that can be stored and transferred). It provides a single point of change in
 * the serialization nuances. It also decouples the domain models from the
 * presentation layer, allowing both to change independently.
 * 
 * @author olivier
 *
 */
public abstract class DataTransferObject {

	/**
	 * Implement value equality redefined method that ensure the functional
	 * comparison of the instance about compared types of both objects and then
	 * their attributes.
	 */
	@Override
	public boolean equals(Object obj) {
		boolean equalsObject = false;
		if (obj == this)
			return true;
		if (obj != null && this.getClass() == obj.getClass()) {
			// Compare all the functional contributors, so comparison based on hash code
			equalsObject = (obj.hashCode() == this.hashCode());
		}
		return equalsObject;
	}
}
