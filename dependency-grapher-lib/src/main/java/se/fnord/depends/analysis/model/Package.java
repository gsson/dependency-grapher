package se.fnord.depends.analysis.model;

import se.fnord.graph.elements.PropertiesBuilder;
import se.fnord.graph.model.PropertySetter;

import java.util.Objects;

public class Package implements PropertySetter {
    private final String name;

    public Package(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Package that = (Package) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public void setProperties(PropertiesBuilder<?> properties) {
        properties.set("name", name);
    }
}
