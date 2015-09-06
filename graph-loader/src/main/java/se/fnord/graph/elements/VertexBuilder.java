package se.fnord.graph.elements;

public interface VertexBuilder<VERTEX> extends PropertiesBuilder<VertexBuilder<VERTEX>> {
    VERTEX create();
}
