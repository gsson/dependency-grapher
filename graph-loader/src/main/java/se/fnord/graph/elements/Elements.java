package se.fnord.graph.elements;

public interface Elements<EDGE, VERTEX> {
    VertexBuilder<VERTEX> vertexFactory(String type);

    EdgeBuilder<EDGE, VERTEX> edgeFactory(String type);
}
