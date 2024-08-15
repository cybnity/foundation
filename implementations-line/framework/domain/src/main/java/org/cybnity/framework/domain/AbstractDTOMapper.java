package org.cybnity.framework.domain;

/**
 * Implementation class type allowing instantiation of DataTransferObject (e.g degraded data view) from other type of object (e.g domain aggregate object).
 * This type of mapper shall be customized by subclass according to the type of DTO expected as output of structure conversion.
 */
public abstract class AbstractDTOMapper<T extends DataTransferObject> implements IDataTransferObjectMapping<T> {
}
