package org.cybnity.framework.immutable.utility;

/**
 * Define a familly of algorithms encapsuling the generation of specific class
 * type version strategies. It's a Strategy design pattern implementation (e.g
 * helping to generate structural version value per fact type).
 * 
 * @author olivier
 *
 */
public abstract class StructuralVersionStrategy {

    /**
     * Generate a serialized value regarding a class type defining its structural
     * version.
     * 
     * @param factType Mandatory class type to read as subject of versioning.
     * @return A computed hash value.
     * @throws IllegalArgumentException When mandatory parameter is not valid (e.g
     *                                  null).
     */
    public abstract String composeCanonicalVersionHash(Class<?> factType) throws IllegalArgumentException;
}
