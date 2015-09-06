package se.fnord.graph.elements;

public interface EdgeBuilder<EDGE, VERTEX> extends PropertiesBuilder<EdgeBuilder<EDGE, VERTEX>> {
    EDGE link(VERTEX out, VERTEX in);
}
