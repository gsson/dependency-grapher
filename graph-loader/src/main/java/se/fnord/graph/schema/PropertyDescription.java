package se.fnord.graph.schema;

import java.util.Objects;

public class PropertyDescription {
    private final String name;
    private final PropertyType type;

    public PropertyDescription(String name, PropertyType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public PropertyType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertyDescription)) return false;
        PropertyDescription that = (PropertyDescription) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}
