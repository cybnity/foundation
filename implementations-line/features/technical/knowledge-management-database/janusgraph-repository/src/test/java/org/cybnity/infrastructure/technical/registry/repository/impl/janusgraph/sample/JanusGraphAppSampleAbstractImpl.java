
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

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.ContextualizedJanusGraphActiveTestContainer;
import org.janusgraph.core.Multiplicity;
import org.janusgraph.core.attribute.Geoshape;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Specialized GraphApp using JanusGraph-specific methods to create the schema.
 */
public class JanusGraphAppSampleAbstractImpl extends GraphAppSampleAbstractImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(JanusGraphAppSampleAbstractImpl.class);

    protected static final String APP_NAME = "jgex";
    protected static final String MIXED_INDEX_CONFIG_NAME = "jgex";

    // Storage backends

    protected static final String BERKELEYJE = "berkeleyje";
    protected static final String CASSANDRA = "cassandra";
    protected static final String CQL = "cql";
    protected static final String HBASE = "hbase";
    protected static final String INMEMORY = "inmemory";

    // Index backends

    protected static final String LUCENE = "lucene";
    protected static final String ELASTICSEARCH = "elasticsearch";
    protected static final String SOLR = "solr";

    /**
     * Constructs a graph app using the given properties.
     */
    public JanusGraphAppSampleAbstractImpl(IContext ctx) throws UnoperationalStateException, IllegalArgumentException {
        super(ctx);
        this.supportsSchema = true;
        this.supportsTransactions = true;
        this.supportsGeoshape = true;
    }


    @Override
    protected void createVertexLabels(final JanusGraphManagement management) {
        management.makeVertexLabel("titan").make();
        management.makeVertexLabel("location").make();
        management.makeVertexLabel("god").make();
        management.makeVertexLabel("demigod").make();
        management.makeVertexLabel("human").make();
        management.makeVertexLabel("monster").make();
    }


    @Override
    protected void createEdgeLabels(final JanusGraphManagement management) {
        management.makeEdgeLabel("father").multiplicity(Multiplicity.MANY2ONE).make();
        management.makeEdgeLabel("mother").multiplicity(Multiplicity.MANY2ONE).make();
        management.makeEdgeLabel("lives").signature(management.getPropertyKey("reason")).make();
        management.makeEdgeLabel("pet").make();
        management.makeEdgeLabel("brother").make();
        management.makeEdgeLabel("battled").make();
    }


    @Override
    protected void createProperties(final JanusGraphManagement management) {
        management.makePropertyKey("name").dataType(String.class).make();
        management.makePropertyKey("age").dataType(Integer.class).make();
        management.makePropertyKey("time").dataType(Integer.class).make();
        management.makePropertyKey("reason").dataType(String.class).make();
        management.makePropertyKey("place").dataType(Geoshape.class).make();
    }


    @Override
    protected void createCompositeIndexes(final JanusGraphManagement management) {
        management.buildIndex("nameIndex", Vertex.class).addKey(management.getPropertyKey("name")).buildCompositeIndex();
    }


    @Override
    protected void createMixedIndexes(final JanusGraphManagement management) {
    }

    /**
     * Returns a string representation of the schema generation code. This
     * request string is submitted to the Gremlin Server via a client
     * connection to create the schema on the graph instance running on the
     * server.
     */
    protected String createSchemaRequest() {
        String s = "JanusGraphManagement management = graph.openManagement(); " +
                "boolean created = false; " +

                // naive check if the schema was previously created
                "if (management.getRelationTypes(RelationType.class).iterator().hasNext()) { management.rollback(); created = false; } else { " +

                // properties
                "PropertyKey name = management.makePropertyKey(\"name\").dataType(String.class).make(); " +
                "PropertyKey age = management.makePropertyKey(\"age\").dataType(Integer.class).make(); " +
                "PropertyKey time = management.makePropertyKey(\"time\").dataType(Integer.class).make(); " +
                "PropertyKey reason = management.makePropertyKey(\"reason\").dataType(String.class).make(); " +
                "PropertyKey place = management.makePropertyKey(\"place\").dataType(Geoshape.class).make(); " +

                // vertex labels
                "management.makeVertexLabel(\"titan\").make(); " +
                "management.makeVertexLabel(\"location\").make(); " +
                "management.makeVertexLabel(\"god\").make(); " +
                "management.makeVertexLabel(\"demigod\").make(); " +
                "management.makeVertexLabel(\"human\").make(); " +
                "management.makeVertexLabel(\"monster\").make(); " +

                // edge labels
                "management.makeEdgeLabel(\"father\").multiplicity(Multiplicity.MANY2ONE).make(); " +
                "management.makeEdgeLabel(\"mother\").multiplicity(Multiplicity.MANY2ONE).make(); " +
                "management.makeEdgeLabel(\"lives\").signature(reason).make(); " +
                "management.makeEdgeLabel(\"pet\").make(); " +
                "management.makeEdgeLabel(\"brother\").make(); " +
                "management.makeEdgeLabel(\"battled\").make(); " +

                // composite indexes
                "management.buildIndex(\"nameIndex\", Vertex.class).addKey(name).buildCompositeIndex(); " +
                "management.commit(); created = true; }";

        return s;
    }

    public static void main(String[] args) throws Exception {
        final String fileName = (args != null && args.length > 0) ? args[0] : null;
        final boolean drop = (args != null && args.length > 1) && "drop".equalsIgnoreCase(args[1]);
        final JanusGraphAppSampleAbstractImpl app = new JanusGraphAppSampleAbstractImpl(ContextualizedJanusGraphActiveTestContainer.getContextInstance());
        if (drop) {
            app.open();
            app.drop();
        } else {
            app.runApp();
        }
    }
}
