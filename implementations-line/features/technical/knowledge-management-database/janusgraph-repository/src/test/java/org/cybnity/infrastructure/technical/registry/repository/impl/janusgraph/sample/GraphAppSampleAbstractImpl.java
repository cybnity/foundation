// Copyright 2017 JanusGraph Authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.AbstractDomainGraphImpl;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.attribute.Geoshape;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Abstract class that defines a basic structure for a graph application. It contains methods for configuring a graph instance, defining a graph schema, creating a graph structure, and querying a graph.
 * Sample graph application defining a graph of sample objects.
 */
public class GraphAppSampleAbstractImpl extends AbstractDomainGraphImpl {

    /**
     * Constructs a graph app using the given properties.
     */
    public GraphAppSampleAbstractImpl(IContext ctx) throws UnoperationalStateException, IllegalArgumentException {
        super(ctx);
    }

    @Override
    public void dropGraph() throws UnoperationalStateException {

    }

    @Override
    public void createSchema() {

    }

    /**
     * Opens the graph instance. If the graph instance does not exist, a new
     * graph instance is initialized.
     */
    public GraphTraversalSource openGraph() throws ConfigurationException, IOException {
        logger().info("opening graph");
        graph = JanusGraphFactory.build().set("storage.backend", "inmemory").open();
        g = graph.traversal();
        return g;
    }
    

    /**
     * Adds the vertices, edges, and properties to the graph.
     */
    public void createElements() {
        try {
            // naive check if the graph was previously created
            if (g.V().has("name", "saturn").hasNext()) {
                if (supportsTransactions) {
                    g.tx().rollback();
                }
                return;
            }
            logger().info("creating elements");

            // see GraphOfTheGodsFactory.java

            final Vertex saturn = g.addV("titan").property("name", "saturn").property("age", 10000).next();
            final Vertex sky = g.addV("location").property("name", "sky").next();
            final Vertex sea = g.addV("location").property("name", "sea").next();
            final Vertex jupiter = g.addV("god").property("name", "jupiter").property("age", 5000).next();
            final Vertex neptune = g.addV("god").property("name", "neptune").property("age", 4500).next();
            final Vertex hercules = g.addV("demigod").property("name", "hercules").property("age", 30).next();
            final Vertex alcmene = g.addV("human").property("name", "alcmene").property("age", 45).next();
            final Vertex pluto = g.addV("god").property("name", "pluto").property("age", 4000).next();
            final Vertex nemean = g.addV("monster").property("name", "nemean").next();
            final Vertex hydra = g.addV("monster").property("name", "hydra").next();
            final Vertex cerberus = g.addV("monster").property("name", "cerberus").next();
            final Vertex tartarus = g.addV("location").property("name", "tartarus").next();

            g.V(jupiter).as("a").V(saturn).addE("father").from("a").next();
            g.V(jupiter).as("a").V(sky).addE("lives").property("reason", "loves fresh breezes").from("a").next();
            g.V(jupiter).as("a").V(neptune).addE("brother").from("a").next();
            g.V(jupiter).as("a").V(pluto).addE("brother").from("a").next();

            g.V(neptune).as("a").V(sea).addE("lives").property("reason", "loves waves").from("a").next();
            g.V(neptune).as("a").V(jupiter).addE("brother").from("a").next();
            g.V(neptune).as("a").V(pluto).addE("brother").from("a").next();

            g.V(hercules).as("a").V(jupiter).addE("father").from("a").next();
            g.V(hercules).as("a").V(alcmene).addE("mother").from("a").next();

            if (supportsGeoshape) {
                g.V(hercules).as("a").V(nemean).addE("battled").property("time", 1)
                        .property("place", Geoshape.point(38.1f, 23.7f)).from("a").next();
                g.V(hercules).as("a").V(hydra).addE("battled").property("time", 2)
                        .property("place", Geoshape.point(37.7f, 23.9f)).from("a").next();
                g.V(hercules).as("a").V(cerberus).addE("battled").property("time", 12)
                        .property("place", Geoshape.point(39f, 22f)).from("a").next();
            } else {
                g.V(hercules).as("a").V(nemean).addE("battled").property("time", 1)
                        .property("place", getGeoFloatArray(38.1f, 23.7f)).from("a").next();
                g.V(hercules).as("a").V(hydra).addE("battled").property("time", 2)
                        .property("place", getGeoFloatArray(37.7f, 23.9f)).from("a").next();
                g.V(hercules).as("a").V(cerberus).addE("battled").property("time", 12)
                        .property("place", getGeoFloatArray(39f, 22f)).from("a").next();
            }

            g.V(pluto).as("a").V(jupiter).addE("brother").from("a").next();
            g.V(pluto).as("a").V(neptune).addE("brother").from("a").next();
            g.V(pluto).as("a").V(tartarus).addE("lives").property("reason", "no fear of death").from("a").next();
            g.V(pluto).as("a").V(cerberus).addE("pet").from("a").next();

            g.V(cerberus).as("a").V(tartarus).addE("lives").from("a").next();

            if (supportsTransactions) {
                g.tx().commit();
            }

        } catch (Exception e) {
            logger().error(e.getMessage(), e);
            if (supportsTransactions) {
                g.tx().rollback();
            }
        }
    }

    /**
     * Runs some traversal queries to get data from the graph.
     */
    public void readElements() {
        try {
            if (g == null) {
                return;
            }

            logger().info("reading elements");

            // look up vertex by name can use a composite index in JanusGraph
            final Optional<Map<Object, Object>> v = g.V().has("name", "jupiter").valueMap().tryNext();
            if (v.isPresent()) {
                logger().info(v.get().toString());
            } else {
                logger().warn("jupiter not found");
            }

            // look up an incident edge
            final Optional<Map<Object, Object>> edge = g.V().has("name", "hercules").outE("battled").as("e").inV()
                    .has("name", "hydra").select("e").valueMap().tryNext();
            if (edge.isPresent()) {
                logger().info(edge.get().toString());
            } else {
                logger().warn("hercules battled hydra not found");
            }

            // numerical range query can use a mixed index in JanusGraph
            final List<Object> list = g.V().has("age", P.gte(5000)).values("age").toList();
            logger().info(list.toString());

            // pluto might be deleted
            final boolean plutoExists = g.V().has("name", "pluto").hasNext();
            if (plutoExists) {
                logger().info("pluto exists");
            } else {
                logger().warn("pluto not found");
            }

            // look up jupiter's brothers
            final List<Object> brothers = g.V().has("name", "jupiter").both("brother").values("name").dedup().toList();
            logger().info("jupiter's brothers: " + brothers.toString());

        } finally {
            // the default behavior automatically starts a transaction for
            // any graph interaction, so it is best to finish the transaction
            // even for read-only graph query operations
            if (supportsTransactions && g != null) {
                g.tx().rollback();
            }
        }
    }

    /**
     * Makes an update to the existing graph structure. Does not create any
     * new vertices or edges.
     */
    public void updateElements() {
        try {
            if (g == null) {
                return;
            }
            logger().info("updating elements");
            final long ts = System.currentTimeMillis();
            g.V().has("name", "jupiter").property("ts", ts).iterate();
            if (supportsTransactions) {
                g.tx().commit();
            }
        } catch (Exception e) {
            logger().error(e.getMessage(), e);
            if (supportsTransactions) {
                g.tx().rollback();
            }
        }
    }

    /**
     * Deletes elements from the graph structure. When a vertex is deleted,
     * its incident edges are also deleted.
     */
    public void deleteElements() {
        try {
            if (g == null) {
                return;
            }
            logger().info("deleting elements");
            // note that this will succeed whether or not pluto exists
            g.V().has("name", "pluto").drop().iterate();
            if (supportsTransactions) {
                g.tx().commit();
            }
        } catch (Exception e) {
            logger().error(e.getMessage(), e);
            if (supportsTransactions) {
                g.tx().rollback();
            }
        }
    }

    /**
     * Run the entire application:
     * 1. Open and initialize the graph
     * 2. Define the schema
     * 3. Build the graph
     * 4. Run traversal queries to get data from the graph
     * 5. Make updates to the graph
     * 6. Close the graph
     */
    public void runApp() {
        try {
            // open and initialize the graph
            openGraph();

            // define the schema before loading data
            if (supportsSchema) {
                createSchema();
            }

            // build the graph structure
            createElements();
            // read to see they were made
            readElements();

            for (int i = 0; i < 3; i++) {
                try {
                    Thread.sleep((long) (Math.random() * 500) + 500);
                } catch (InterruptedException e) {
                    logger().error(e.getMessage(), e);
                }
                // update some graph elements with changes
                updateElements();
                // read to see the changes were made
                readElements();
            }

            // delete some graph elements
            deleteElements();
            // read to see the changes were made
            readElements();

            // close the graph
            closeGraph();
        } catch (Exception e) {
            logger().error(e.getMessage(), e);
        }
    }

}