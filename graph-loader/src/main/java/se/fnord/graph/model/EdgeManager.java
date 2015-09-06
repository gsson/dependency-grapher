package se.fnord.graph.model;

import se.fnord.graph.elements.EdgeBuilder;

import java.util.function.Consumer;

public class EdgeManager<OUT_V extends PropertySetter, IN_V extends PropertySetter, E extends EdgeEntity<OUT_V, IN_V>, EDGE, VERTEX> implements ElementMaterialiser<E> {
    private final EdgeCache<OUT_V, IN_V, E, EDGE, VERTEX> edgeCache;
    private final EntityCache<E> entityCache;

    public static <_OUT_V extends PropertySetter, _IN_V extends PropertySetter, _E extends EdgeEntity<_OUT_V, _IN_V>, _EDGE, _VERTEX>
    EdgeManager<_OUT_V, _IN_V, _E, _EDGE, _VERTEX> edges(Class<_E> edgeType, EdgeBuilder<_EDGE, _VERTEX> edgeBuilder, VertexManager<_OUT_V, _VERTEX> out, VertexManager<_IN_V, _VERTEX> in) {
        return new EdgeManager<>(EntityCache.cacheFor(edgeType), EdgeCache.cacheFor(edgeType, edgeBuilder, out::getVertex, in::getVertex));
    }

    private EdgeManager(EntityCache<E> entityCache, EdgeCache<OUT_V, IN_V, E, EDGE, VERTEX> edgeCache) {
        this.entityCache = entityCache;
        this.edgeCache = edgeCache;
    }

    public E getEntity(E entity) {
        return entityCache.get(entity);
    }

    @Override
    public void materialise(Consumer<Object> edgeConsumer) {
        forEachEntity(e -> edgeConsumer.accept(edgeCache.get(e)));
    }

    public void forEachEntity(Consumer<E> entityConsumer) {
        entityCache.forEach(entityConsumer);
    }

    public int size() {
        return entityCache.size();
    }

    @Override
    public boolean containsEntity(E entity) {
        return entityCache.contains(entity);
    }
}
