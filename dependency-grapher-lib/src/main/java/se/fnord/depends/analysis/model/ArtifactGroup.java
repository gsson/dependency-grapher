package se.fnord.depends.analysis.model;

import se.fnord.graph.elements.PropertiesBuilder;
import se.fnord.graph.model.PropertySetter;

import java.util.Objects;

public class ArtifactGroup implements PropertySetter {
    private final String groupId;

    public ArtifactGroup(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtifactGroup that = (ArtifactGroup) o;
        return Objects.equals(groupId, that.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(groupId);
    }

    @Override
    public void setProperties(PropertiesBuilder<?> properties) {
        properties.set("groupId", groupId);
    }

}
