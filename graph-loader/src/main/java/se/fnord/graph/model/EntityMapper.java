package se.fnord.graph.model;

public interface EntityMapper<T> {
    T getEntity(T entity);

    int size();

    boolean containsEntity(T entity);
}
