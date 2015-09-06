package se.fnord.graph.elements;

public interface PropertiesBuilder<U extends PropertiesBuilder<U>> {
    U set(String prop, Object value);
    U clear();
}
