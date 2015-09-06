package se.fnord.graph.orientdb;

import com.orientechnologies.common.util.OPair;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.tinkerpop.blueprints.impls.orient.*;
import net.openhft.koloboke.collect.map.hash.HashObjIntMap;
import net.openhft.koloboke.collect.map.hash.HashObjIntMaps;
import se.fnord.graph.elements.EdgeBuilder;
import se.fnord.graph.elements.PropertiesBuilder;
import se.fnord.graph.elements.VertexBuilder;

import java.util.Collection;

public class OrientElements implements se.fnord.graph.elements.Elements<OrientEdge, OrientVertex> {
    private final OrientBaseGraph graph;

    public static class PropertySchema {
        private final HashObjIntMap<String> propertyMap;

        public PropertySchema(Collection<OProperty> properties) {
            final HashObjIntMap<String> map = HashObjIntMaps.newUpdatableMap(properties.size());
            int i = 0;
            for (OProperty property : properties) {
                map.put(property.getName(), i++);
            }
            this.propertyMap = HashObjIntMaps
                    .getDefaultFactory()
                    .withDefaultValue(-1)
                    .newImmutableMap(map);
        }

        private int indexOf(String propertyName) {
            final int propertyIndex = propertyMap.getInt(propertyName);
            if (propertyIndex == -1)
                throw new IllegalArgumentException("No such property " + propertyName);
            return propertyIndex;
        }

        public void updateValue(SparsePairs<String, Object> valueList, String propertyName, Object value) {
            final int propertyIndex = indexOf(propertyName);
            valueList.set(propertyIndex, value);
        }

        @SuppressWarnings("unchecked")
        SparsePairs<String, Object> newValueList() {
            final OPair<String, Object>[] pairs = new OPair[propertyMap.size()];
            propertyMap.forEach((String k, int v) -> pairs[v] = new OPair<>(k, null));
            return new SparsePairs<>(pairs);
        }
    }

    public static class ElementBuilder<U extends PropertiesBuilder<U>> implements PropertiesBuilder<U> {
        private final PropertySchema propsSchema;
        private final SparsePairs<String, Object> propValues;
        private final Object[] propValueArray;

        public ElementBuilder(PropertySchema propsSchema) {
            this.propsSchema = propsSchema;
            this.propValues = propsSchema.newValueList();
            this.propValueArray = new Object[] { propValues };
        }

        protected Object[] propValues() {
            return propValueArray;
        }

        @SuppressWarnings("unchecked")
        public U set(String prop, Object value) {
            propsSchema.updateValue(propValues, prop, value);
            return (U) this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public U clear() {
            propValues.clear();
            return (U) this;
        }
    }

    private class VertexBuilderImpl extends ElementBuilder<VertexBuilder<OrientVertex>> implements VertexBuilder<OrientVertex> {
        private final String className;

        public VertexBuilderImpl(String className, PropertySchema factory) {
            super(factory);
            this.className = className;
        }

        @Override
        public OrientVertex create() {
            return graph.addVertex(className, propValues());
        }
    }

    private class EdgeBuilderImpl extends ElementBuilder<EdgeBuilder<OrientEdge, OrientVertex>> implements EdgeBuilder<OrientEdge, OrientVertex> {
        private final String className;

        public EdgeBuilderImpl(String className, PropertySchema factory) {
            super(factory);
            this.className = className;
        }

        @Override
        public OrientEdge link(OrientVertex out, OrientVertex in) {
            return graph.addEdge(className, out, in, null).setProperties(propValues());
        }
    }

    @Override
    public VertexBuilder<OrientVertex> vertexFactory(String type) {
        return vertexFactory(graph.getVertexType(type));
    }

    @Override
    public EdgeBuilder<OrientEdge, OrientVertex> edgeFactory(String type) {
        return edgeFactory(graph.getEdgeType(type));
    }

    public VertexBuilder<OrientVertex> vertexFactory(OrientVertexType type) {
        final String className = "class:" + type.getName();
        final PropertySchema propertySchema = new PropertySchema(type.declaredProperties());
        return new VertexBuilderImpl(className, propertySchema);
    }

    public EdgeBuilder<OrientEdge, OrientVertex> edgeFactory(OrientEdgeType type) {
        final String className = "class:" + type.getName();

        final PropertySchema propertySchema = new PropertySchema(type.declaredProperties());
        return new EdgeBuilderImpl(className, propertySchema);
    }

    OrientElements(OrientBaseGraph graph) {
        this.graph = graph;
    }

}
