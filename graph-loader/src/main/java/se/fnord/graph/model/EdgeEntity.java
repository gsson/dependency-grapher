package se.fnord.graph.model;

public interface EdgeEntity<OUT_V extends PropertySetter, IN_V extends PropertySetter> extends PropertySetter {
    OUT_V getOutVertex();
    IN_V getInVertex();
}
