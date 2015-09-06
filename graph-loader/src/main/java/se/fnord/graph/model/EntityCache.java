package se.fnord.graph.model;

import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;

import java.util.Map;
import java.util.function.Consumer;

public class EntityCache<T> {
    private final Map<T, T> entities = HashObjObjMaps.newMutableMap();

    public static <TT> EntityCache<TT> cacheFor(Class<TT> t) {
        return new EntityCache<>();
    }

    public T get(T entity) {
        final T oldEntity = entities.putIfAbsent(entity, entity);
        if (oldEntity != null)
            return oldEntity;
        return entity;
    }

    public void forEach(Consumer<T> entity) {
        entities.values().forEach(entity);
    }

    public int size() {
        return entities.size();
    }

    public boolean contains(T entity) {
        return entities.containsKey(entity);
    }
}
