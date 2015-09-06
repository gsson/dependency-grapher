package se.fnord.depends.analysis.model;

import se.fnord.graph.elements.PropertiesBuilder;
import se.fnord.graph.model.PropertySetter;

import java.util.Objects;

public class ClassFile implements PropertySetter {
    private final String fileName;

    public ClassFile(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassFile classFile = (ClassFile) o;
        return Objects.equals(fileName, classFile.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName);
    }

    @Override
    public void setProperties(PropertiesBuilder<?> properties) {
        properties.set("fileName", fileName);
    }
}
