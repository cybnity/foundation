package org.cybnity.framework.domain;

/**
 * Contract of mapping between an object type and a DataTransferObject version.
 */
public interface IDataTransferObjectMapping<T extends DataTransferObject> {

    /**
     * Convert a source object and its including data, into another type of class (e.g degraded version) that is returned.
     * The returned mapped object is including full or partial version of the origin object's information according to the implemented method.
     *
     * @param source       Mandatory object type that shall be read for convert into the expected type.
     * @return A new instance of expected type, including full or partial values mapped/interpreted from the read source object.
     * @throws IllegalArgumentException      When any mandatory parameter is missing.
     * @throws UnsupportedOperationException When problem is occurred during conversion (e.g impossible mapping of any mandatory data expected by new instance to create, that is not found from the origin object).
     */
    public T convertTo(Object source) throws IllegalArgumentException, UnsupportedOperationException;
}
