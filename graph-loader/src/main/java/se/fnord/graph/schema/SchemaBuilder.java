package se.fnord.graph.schema;

import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;
import se.fnord.graph.GraphConnection;
import se.fnord.graph.elements.Elements;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SchemaBuilder {
    private final Map<String, ElementDescription> vertices = HashObjObjMaps.newUpdatableMap();
    private final Map<String, EdgeDescription> edges = HashObjObjMaps.newUpdatableMap();

    private static class ElementDescription {
        private final List<PropertyDescription> properties;
        private final String name;

        protected ElementDescription(String name, PropertyDescription... properties) {
            this.name = name;
            this.properties = Arrays.asList(properties);
        }

        public List<PropertyDescription> getProperties() {
            return properties;
        }

        public String getName() {
            return name;
        }

    }

    private static class EdgeDescription extends ElementDescription {
        private final String outVertex;
        private final String inVertex;

        public EdgeDescription(String outVertex, String name, String inVertex, PropertyDescription... properties) {
            super(name, properties);
            this.outVertex = outVertex;
            this.inVertex = inVertex;
        }

        public String getOutVertex() {
            return outVertex;
        }

        public String getInVertex() {
            return inVertex;
        }
    }

    public static PropertyDescription property(String name, PropertyType type) {
        return new PropertyDescription(name, type);
    }

    public SchemaBuilder vertex(String name, PropertyDescription... properties) {
        vertices.put(name, new ElementDescription(name, properties));
        return this;
    }

    public SchemaBuilder edge(String outVertex, String name, String inVertex, PropertyDescription... properties) {
        edges.put(name, new EdgeDescription(outVertex, name, inVertex, properties));
        return this;
    }

    public <EDGE, VERTEX> Elements<EDGE, VERTEX> build(GraphConnection<EDGE, VERTEX> graph) {
        for (ElementDescription vertex : vertices.values()) {
            graph.addVertexType(vertex.name, vertex.properties);
        }
        for (EdgeDescription edge : edges.values()) {
            graph.addEdgeType(edge.getOutVertex(), edge.getName(), edge.getInVertex(), edge.getProperties());
        }

        return graph.elements();
    }
}
