package org.cybnity.infrastructure.technical.message_bus.adapter.api;

/**
 * Contract allowing to transform an object manageable by the space according to a type of data structure supported by Redis.
 * For example, translate a CommandEvent object into a Map (message body).
 */
public interface MessageMapper {

    /**
     * Execute transformation process of origin object to the defined target type.
     *
     * @param origin Source object to transform.
     * @throws IllegalArgumentException When mandatory parameter is missing, or when origin parameter type is not supported and compatible with this mapper.
     * @throws MappingException         When impossible transformation.
     */
    void transform(Object origin) throws IllegalArgumentException, MappingException;

    /**
     * Get the transformed version of the origin object.
     * This method shall be called AFTER execution of the transform() method to obtain the transformation process result.
     *
     * @return Transformed origin data into targeted class type. Null when transformation process was not previously executed.
     */
    Object getResult();
}
