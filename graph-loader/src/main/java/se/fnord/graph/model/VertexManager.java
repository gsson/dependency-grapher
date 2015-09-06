package se.fnord.graph.model;

import se.fnord.graph.elements.VertexBuilder;

import java.util.function.Consumer;

public class VertexManager<T extends PropertySetter, VERTEX> implements ElementMaterialiser<T> {
    private final VertexCache<T, VERTEX> vertexCache;
    private final EntityCache<T> entityCache;

    public static <_T extends PropertySetter, _VERTEX> VertexManager<_T, _VERTEX> vertices(Class<_T> t, VertexBuilder<_VERTEX> builder) {
        return new VertexManager<>(EntityCache.cacheFor(t), VertexCache.cacheFor(t, builder));
    }

    private VertexManager(EntityCache<T> entityCache, VertexCache<T, VERTEX> vertexCache) {
        this.entityCache = entityCache;
        this.vertexCache = vertexCache;
    }

    public VERTEX getVertex(T entity) {
        return vertexCache.get(getEntity(entity));
    }

    @Override
    public T getEntity(T entity) {
        return entityCache.get(entity);
    }

    @Override
    public void materialise(Consumer<Object> vertexConsumer) {
        forEachEntity(e -> vertexConsumer.accept(vertexCache.get(e)));
    }

    public void forEachEntity(Consumer<T> entityConsumer) {
        entityCache.forEach(entityConsumer);
    }

    @Override
    public int size() {
        return entityCache.size();
    }

    @Override
    public boolean containsEntity(T entity) {
        return entityCache.contains(entity);
    }
}
