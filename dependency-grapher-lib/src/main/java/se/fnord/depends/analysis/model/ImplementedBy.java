package se.fnord.depends.analysis.model;


import se.fnord.graph.elements.PropertiesBuilder;
import se.fnord.graph.model.AbstractEdgeEntity;

public class ImplementedBy extends AbstractEdgeEntity<Class, ClassFile> {
    private final int modifiers;

    public ImplementedBy(Class cls, ClassFile file, int modifiers) {
        super(cls, file);
        this.modifiers = modifiers;
    }

    @Override
    public void setProperties(PropertiesBuilder<?> builder) {
        builder.set("modifiers", modifiers);
    }

}
