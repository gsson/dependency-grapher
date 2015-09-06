package se.fnord.graph.model;

import se.fnord.graph.elements.PropertiesBuilder;

public interface PropertySetter {
    default void setProperties(PropertiesBuilder<?> properties) {}
}
