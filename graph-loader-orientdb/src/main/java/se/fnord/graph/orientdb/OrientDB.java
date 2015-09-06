package se.fnord.graph.orientdb;

import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.tool.ODatabaseExport;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.impls.orient.*;
import se.fnord.graph.elements.Elements;
import se.fnord.graph.schema.PropertyDescription;
import se.fnord.graph.schema.PropertyType;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public class OrientDB {
    private static final Map<PropertyType, OType> TYPE_MAP;

    static {
        final Map<PropertyType, OType> typeMap = new EnumMap<>(PropertyType.class);
        typeMap.put(PropertyType.INT, OType.INTEGER);
        typeMap.put(PropertyType.LONG, OType.LONG);
        typeMap.put(PropertyType.FLOAT, OType.FLOAT);
        typeMap.put(PropertyType.DOUBLE, OType.DOUBLE);
        typeMap.put(PropertyType.BOOLEAN, OType.BOOLEAN);
        typeMap.put(PropertyType.BYTE, OType.BYTE);
        typeMap.put(PropertyType.SHORT, OType.SHORT);
        typeMap.put(PropertyType.STRING, OType.STRING);
        TYPE_MAP = typeMap;
    }

    public static OrientConnector connect(String url, String userName, String password) {
        final OrientURL orientURL = OrientURL.create(url);
        switch (orientURL.getConnectionType()) {
            case "remote":
                return new Remote(orientURL, userName, password);
            case "local":
                return new Local(orientURL, userName, password);
            case "memory":
                return new InMemory(orientURL, userName, password);
        }
        return null;
    }

    private static abstract class AbstractOrientConnection implements OrientConnection {
        final OrientBaseGraph graph;

        public AbstractOrientConnection(OrientBaseGraph graph) {
            this.graph = graph;
        }

        @Override
        public Elements<OrientEdge, OrientVertex> elements() {
            return new OrientElements(graph);
        }

        @Override
        public void addVertexType(String vertexType, Collection<PropertyDescription> properties) {
            final OrientVertexType type = graph.createVertexType(vertexType);
            properties.stream().forEach(prop -> type.createProperty(prop.getName(), TYPE_MAP.get(prop.getType())));
        }

        @Override
        public void addEdgeType(String outVertexType, String throughEdgeType, String inVertexType, Collection<PropertyDescription> properties) {
            final OrientEdgeType edge = graph.createEdgeType(throughEdgeType);
            properties.stream().forEach(prop -> edge.createProperty(prop.getName(), TYPE_MAP.get(prop.getType())));

            final OrientVertexType fromVertex = graph.getVertexType(outVertexType);
            edge.createProperty(OrientGraph.CONNECTION_OUT, OType.LINK, fromVertex);
            final String fromPropertyName = OrientVertex.getConnectionFieldName(Direction.OUT, edge.getName(), graph.isUseVertexFieldsForEdgeLabels());
            fromVertex.createProperty(fromPropertyName, OType.LINKLIST, edge);

            final OrientVertexType toVertex = graph.getVertexType(inVertexType);
            edge.createProperty(OrientGraph.CONNECTION_IN, OType.LINK, toVertex);
            final String toPropertyName = OrientVertex.getConnectionFieldName(Direction.IN, edge.getName(), graph.isUseVertexFieldsForEdgeLabels());
            toVertex.createProperty(toPropertyName, OType.LINKLIST, edge);
        }
    }

    private static class Local implements OrientConnector {
        private final OrientURL url;
        private final String userName;
        private final String password;

        public Local(OrientURL url, String userName, String password) {
            this.url = url;
            this.userName = userName;
            this.password = password;
        }

        public OrientConnection connect() throws Exception {
            final OrientGraphFactory graphFactory = new OrientGraphFactory(url.toString(), userName, password);

            final ODatabaseDocumentTx database = graphFactory.getDatabase(false, false);
            if (database.exists()) {
                database.open(userName, password);
                database.drop();
            }

            graphFactory.setAutoStartTx(false);
            graphFactory.declareIntent(new OIntentMassiveInsert().setEnableCache(false));

            return new AbstractOrientConnection(graphFactory.getNoTx()) {
                @Override
                public void close() throws Exception {
                    graph.commit();
                    graph.getRawGraph().close();
                }
            };
        }
    }

    private static class InMemory implements OrientConnector {
        private final OrientURL url;
        private final String userName;
        private final String password;

        public InMemory(OrientURL url, String userName, String password) {
            this.url = url;
            this.userName = userName;
            this.password = password;
        }

        @Override
        public OrientConnection connect() throws Exception {
            final OrientGraphFactory graphFactory = new OrientGraphFactory("memory:graph", userName, password);

            graphFactory.setAutoStartTx(false);
            graphFactory.declareIntent(new OIntentMassiveInsert().setEnableCache(false));

            return new AbstractOrientConnection(graphFactory.getNoTx()) {
                @Override
                public void close() throws Exception {
                    graph.commit();
                    new ODatabaseExport(graph.getRawGraph(), url.getDbName(), System.err::print).exportDatabase();
                    graph.getRawGraph().close();
                }
            };
        }
    }

    private static class Remote implements OrientConnector {
        private final OrientURL url;
        private final String userName;
        private final String password;

        public Remote(OrientURL url, String userName, String password) {
            this.url = url;
            this.userName = userName;
            this.password = password;
        }

        @Override
        public OrientConnection connect() throws IOException {
            final OServerAdmin oServerAdmin = new OServerAdmin(url.toString());

            oServerAdmin.connect(userName, password);
            try {
                if (oServerAdmin.existsDatabase())
                    oServerAdmin.dropDatabase("plocal");

                oServerAdmin.createDatabase(url.getDbName(), "graph", "plocal");
            } finally {
                oServerAdmin.close();
            }

            final OrientGraphFactory graphFactory = new OrientGraphFactory(url.toString(), userName, password);
            graphFactory.setAutoStartTx(false);
            graphFactory.declareIntent(new OIntentMassiveInsert().setEnableCache(false));

            return new AbstractOrientConnection(graphFactory.getNoTx()) {
                @Override
                public void close() throws Exception {
                    graph.commit();
                    graph.getRawGraph().close();
                }
            };
        }
    }
}
