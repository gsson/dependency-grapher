package se.fnord.graph.model;

import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;
import se.fnord.graph.elements.EdgeBuilder;

import java.util.Map;
import java.util.function.Function;

public class EdgeCache<OUT_V extends PropertySetter, IN_V extends PropertySetter, T extends EdgeEntity<OUT_V, IN_V>, EDGE, VERTEX> {
    private final Map<T, EDGE> entities;
    private final EdgeBuilder<EDGE, VERTEX> builder;
    private final Function<? super OUT_V, ? extends VERTEX> out;
    private final Function<? super IN_V, ? extends VERTEX> in;

    public static
    <_OUT_V extends PropertySetter, _IN_V extends PropertySetter, _T extends EdgeEntity<_OUT_V, _IN_V>, _EDGE, _VERTEX>
    EdgeCache<_OUT_V, _IN_V, _T, _EDGE, _VERTEX> cacheFor(Class<_T> t, EdgeBuilder<_EDGE, _VERTEX> builder, Function<? super _OUT_V, ? extends _VERTEX> out, Function<? super _IN_V, ? extends _VERTEX> in) {
        return new EdgeCache<>(t, builder, out, in, 10);
    }

    public EdgeCache(Class<T> entityType, EdgeBuilder<EDGE, VERTEX> builder, Function<? super OUT_V, ? extends VERTEX> out, Function<? super IN_V, ? extends VERTEX> in, int expectedSize) {
        this.out = out;
        this.in = in;
        this.builder = builder;
        this.entities = HashObjObjMaps.newUpdatableMap(expectedSize);
    }

    public EDGE get(T entity) {
        return entities.computeIfAbsent(entity, k -> {
            k.setProperties(builder);
            return builder.link(out.apply(k.getOutVertex()), in.apply(k.getInVertex()));
        });
    }
}
