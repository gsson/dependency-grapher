package se.fnord.graph;

import se.fnord.graph.elements.Elements;
import se.fnord.graph.schema.PropertyDescription;

import java.util.Collection;

public interface GraphConnection<EDGE, VERTEX> extends AutoCloseable {
    void addVertexType(String vertexType, Collection<PropertyDescription> properties);
    void addEdgeType(String outVertexType, String throughEdgeType, String inVertexType, Collection<PropertyDescription> properties);

    Elements<EDGE, VERTEX> elements();
}
