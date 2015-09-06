package se.fnord.graph.model;

import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;
import se.fnord.graph.GraphConnection;
import se.fnord.graph.elements.Elements;
import se.fnord.graph.schema.PropertyDescription;
import se.fnord.graph.schema.SchemaBuilder;

import java.util.Map;
import java.util.function.Consumer;

public class MapperBuilder {
    private static class EdgeMapping<E extends EdgeEntity<?, ?>> {
        private final PropertyDescription[] properties;
        private final Class<E> edgeEntity;
        private final String fromVertex;
        private final String throughEdge;
        private final String toVertex;

        public EdgeMapping(Class<E> edgeEntity, String fromVertex, String throughEdge, String toVertex, PropertyDescription ... properties) {
            this.edgeEntity = edgeEntity;
            this.fromVertex = fromVertex;
            this.throughEdge = throughEdge;
            this.toVertex = toVertex;
            this.properties = properties;
        }

        public PropertyDescription[] getProperties() {
            return properties;
        }

        public Class<E> getEdgeEntity() {
            return edgeEntity;
        }

        public String getFromVertex() {
            return fromVertex;
        }

        public String getThroughEdge() {
            return throughEdge;
        }

        public String getToVertex() {
            return toVertex;
        }
    }

    private static class VertexMapping<V extends PropertySetter> {
        private final String vertexName;
        private final Class<V> vertexEntity;
        private final PropertyDescription[] properties;

        public VertexMapping(Class<V> vertexEntity, String vertexName, PropertyDescription ... properties) {
            this.vertexName = vertexName;
            this.vertexEntity = vertexEntity;
            this.properties = properties;
        }

        public String getVertexName() {
            return vertexName;
        }

        public Class<V> getVertexEntity() {
            return vertexEntity;
        }

        public PropertyDescription[] getProperties() {
            return properties;
        }
    }

    private final Map<Class<?>, EdgeMapping<?>> edges = HashObjObjMaps.newUpdatableMap();
    private final Map<Class<?>, VertexMapping<?>> vertices = HashObjObjMaps.newUpdatableMap();

    public <E extends EdgeEntity<?, ?>>
    MapperBuilder edge(Class<E> edgeEntity, String fromVertex, String throughEdge, String toVertex, PropertyDescription ... properties) {
        edges.put(edgeEntity, new EdgeMapping<>(edgeEntity, fromVertex, throughEdge, toVertex, properties));
        return this;
    }

    public <V extends PropertySetter>
    MapperBuilder vertex(Class<V> vertexEntity, String vertexName, PropertyDescription ... properties) {
        vertices.put(vertexEntity, new VertexMapping<>(vertexEntity, vertexName, properties));
        return this;
    }

    private <EDGE, VERTEX> Elements<EDGE, VERTEX> buildSchema(GraphConnection<EDGE, VERTEX> graph) {
        final SchemaBuilder schemaBuilder = new SchemaBuilder();
        for (VertexMapping<?> vertexMapping : vertices.values()) {
            schemaBuilder.vertex(vertexMapping.getVertexName(), vertexMapping.getProperties());
        }

        for (EdgeMapping<?> edgeMapping : edges.values()) {
            schemaBuilder.edge(edgeMapping.getFromVertex(), edgeMapping.getThroughEdge(), edgeMapping.getToVertex(), edgeMapping.getProperties());
        }

        return schemaBuilder.build(graph);
    }

    public <EDGE, VERTEX> Mappers build(GraphConnection<EDGE, VERTEX> graph) {
        final Elements<EDGE, VERTEX> elements = buildSchema(graph);

        Map<Class<? extends PropertySetter>, ElementMaterialiser<?>> mappers = HashObjObjMaps.newUpdatableMap();
        Map<String, VertexManager<?, VERTEX>> vertexMapperByName = HashObjObjMaps.newUpdatableMap();

        for (VertexMapping vertex : vertices.values()) {
            final VertexManager<?, VERTEX> vertexManager = VertexManager.vertices(vertex.getVertexEntity(), elements.vertexFactory(vertex.getVertexName()));
            mappers.put(vertex.getVertexEntity(), vertexManager);
            vertexMapperByName.put(vertex.getVertexName(), vertexManager);
        }

        for (EdgeMapping edge : edges.values()) {
            final EdgeManager<?, ?, ?, EDGE, VERTEX> edgeManager = EdgeManager.edges(
                    edge.getEdgeEntity(),
                    elements.edgeFactory(edge.getThroughEdge()),
                    vertexMapperByName.get(edge.getFromVertex()),
                    vertexMapperByName.get(edge.getToVertex()));
            mappers.put(edge.getEdgeEntity(), edgeManager);
        }

        return new Mappers(mappers);
    }

    public static class Mappers {
        private final Map<Class<? extends PropertySetter>, ElementMaterialiser<?>> mappers;

        Mappers(Map<Class<? extends PropertySetter>, ElementMaterialiser<?>> mappers) {
            this.mappers = mappers;
        }

        public <T extends PropertySetter> EntityMapper<T> mapperFor(Class<T> entityType) {
            return (EntityMapper<T>) mappers.get(entityType);
        }

        public void materialise(Consumer<Object> consumer) {
            for (ElementMaterialiser<?> entityMapper : mappers.values()) {
                entityMapper.materialise(consumer);
            }
        }
    }
}
