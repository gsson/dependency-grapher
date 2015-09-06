package se.fnord.graph.model;

import java.util.function.Consumer;

public interface ElementMaterialiser<T> extends EntityMapper<T> {
    void materialise(Consumer<Object> vertexConsumer);
}
