package se.fnord.depends.analysis.model;

import se.fnord.graph.model.AbstractEdgeEntity;

public class References extends AbstractEdgeEntity<ClassFile, Class> {
    public References(ClassFile file, Class cls) {
        super(file, cls);
    }
}
