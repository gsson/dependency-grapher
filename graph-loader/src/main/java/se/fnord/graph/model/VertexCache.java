package se.fnord.graph.model;

import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;
import se.fnord.graph.elements.VertexBuilder;

import java.util.Map;

public class VertexCache<T extends PropertySetter, VERTEX> {
    private final Map<T, VERTEX> entities;
    private final VertexBuilder<VERTEX> builder;

    public static <_T extends PropertySetter, _VERTEX> VertexCache<_T, _VERTEX> cacheFor(Class<_T> t, VertexBuilder<_VERTEX> builder) {
        return new VertexCache<>(builder, 10);
    }

    private VertexCache(VertexBuilder<VERTEX> builder, int expectedSize) {
        this.builder = builder;
        this.entities = HashObjObjMaps.newUpdatableMap(expectedSize);
    }

    public VERTEX get(T entity) {
        return entities.computeIfAbsent(entity, k -> {
            k.setProperties(builder);
            return builder.create();
        });
    }

}
