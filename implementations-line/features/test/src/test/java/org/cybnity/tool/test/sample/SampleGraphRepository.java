package org.cybnity.tool.test.sample;

import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.AbstractDomainGraphImpl;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.AbstractReadModelRepository;

/**
 * Sample of graph repository.
 */
public class SampleGraphRepository extends AbstractReadModelRepository {

    private static SampleGraphRepository SINGLETON;

    private SampleGraphRepository(AbstractDomainGraphImpl managedGraph) throws IllegalArgumentException {
        super(managedGraph);
    }

    /**
     * Get a repository instance.
     *
     * @param managedGraph Mandatory graph model.
     * @return A singleton instance.
     * @throws UnoperationalStateException When impossible connection and initialization of the graph model manipulated by this repository.
     * @throws IllegalArgumentException    When mandatory parameter is missing.
     */
    public static SampleGraphRepository instance(AbstractDomainGraphImpl managedGraph) throws UnoperationalStateException, IllegalArgumentException {
        if (SINGLETON == null) {
            // Initializes singleton instance
            SINGLETON = new SampleGraphRepository(managedGraph);
        }
        return SINGLETON;
    }

    /**
     * Drop graph model schema and data.
     *
     * @throws UnoperationalStateException When problem during graph model drop.
     */
    public void drop() throws UnoperationalStateException {
        this.graphModel().drop();
    }
}
