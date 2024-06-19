package org.cybnity.infastructure.technical.persistence.store.impl.redis;

import org.cybnity.framework.domain.ValueObject;
import org.cybnity.framework.immutable.ImmutabilityException;

import java.io.Serializable;

/**
 * Implementation class regarding space name defining a storage resource path name.
 */
public class SnapshotSpaceName extends ValueObject<String> implements Serializable {

    /**
     * Storage area path name were snapshot versions can be stored.
     */
    private final String spaceNamePath;

    /**
     * Default constructor.
     *
     * @param spaceNamePath Mandatory resource path name regarding the storage area were snapshot versions can be stored.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public SnapshotSpaceName(String spaceNamePath) throws IllegalArgumentException {
        if (spaceNamePath == null || spaceNamePath.isEmpty())
            throw new IllegalArgumentException("spaceNamePath parameter is required!");
        this.spaceNamePath = spaceNamePath;
    }

    /**
     * Get the path name of storage area were snapshot versions can be stored
     *
     * @return A resource path name (e.g stream path name) immutable version.
     */
    public String getSpaceNamePath() {
        return String.valueOf(spaceNamePath);
    }

    @Override
    public String[] valueHashCodeContributors() {
        return new String[]{spaceNamePath};
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
        return new SnapshotSpaceName(spaceNamePath);
    }
}
