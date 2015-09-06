package se.fnord.depends.analysis.model;


import se.fnord.graph.model.AbstractEdgeEntity;

public class Parent extends AbstractEdgeEntity<Package, Package> {
    public Parent(Package child, Package parent) {
        super(child, parent);
    }
}
