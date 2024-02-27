package org.cybnity.framework.immutable.sample;

import org.cybnity.framework.immutable.ChildFact;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Sample of child entity following (as aggregate) a parent other entity.
 *
 * @author olivier
 */
public class ChildAggregate extends ChildFact {

    private static final long serialVersionUID = 1L;


    public ChildAggregate(Entity predecessor, Identifier id) throws IllegalArgumentException {
        super(predecessor, id);
    }

    public ChildAggregate(Entity predecessor, LinkedHashSet<Identifier> identifiers) throws IllegalArgumentException {
        super(predecessor, identifiers);
    }

    @Override
    public Identifier identified() {
        StringBuffer combinedId = new StringBuffer();
        for (Identifier id : this.identifiers()) {
            combinedId.append(id.value());
        }
        // Return combined identifier
        return new IdentifierImpl("UUID", combinedId.toString());
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
        LinkedHashSet<Identifier> ids = new LinkedHashSet<>(this.identifiers());
        return new ChildAggregate(this.parent(), ids);
    }

    /**
     * Sample of implementation generally implemented by a dedicated combination
     * rule coded by the application layer
     */
    @Override
    protected Identifier generateIdentifierPredecessorBased(Entity predecessor, Identifier childOriginalId)
            throws IllegalArgumentException {
        if (predecessor == null)
            throw new IllegalArgumentException(
                    "Mandatory predecessor parameter is required to generate a child identifier!");
        StringBuffer value = new StringBuffer();
        if (childOriginalId != null && childOriginalId.value() != null) {
            value.append(childOriginalId.value().toString());
            value.append("_");// add logical separator (e.g as convention of multiples identifiers
            // combination)
        }
        // Use predecessor identifying information(s)
        for (Identifier parentId : predecessor.identifiers()) {
            value.append(parentId.value().toString());
        }
        String childIdName = null;
        if (childOriginalId != null)
            childIdName = childOriginalId.name(); // Same name of identifier for the child as its parent
        if (childIdName == null || childIdName.equals("")) {
            // Define a specific new convention name
            childIdName = "id";
        }

        // Create new identifier from origin concatened with parent identifying
        // information
        return new IdentifierImpl(childIdName, value.toString());
    }

    /**
     * Not implemented because relevant to each combination rules coded by
     * application layer
     */
    @Override
    protected Identifier generateIdentifierPredecessorBased(Entity predecessor, Collection<Identifier> childOriginalIds)
            throws IllegalArgumentException {
        return null;
    }

    /**
     * Implement the generation of version hash regarding this class type according
     * to a concrete strategy utility service.
     */
    @Override
    public String versionHash() {
        return new VersionConcreteStrategy().composeCanonicalVersionHash(getClass());
    }
}
