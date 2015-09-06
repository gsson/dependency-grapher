package se.fnord.depends.analysis.model;

import se.fnord.graph.model.AbstractEdgeEntity;

public class DeclaredIn extends AbstractEdgeEntity<Class, Package> {
    public DeclaredIn(Class child, Package parent) {
        super(child, parent);
    }
}
