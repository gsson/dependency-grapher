package se.fnord.depends.analysis.model;


import se.fnord.graph.model.AbstractEdgeEntity;

public class ExportedBy extends AbstractEdgeEntity<ClassFile, Instance> {
    public ExportedBy(ClassFile file, Instance instance) {
        super(file, instance);
    }
}
