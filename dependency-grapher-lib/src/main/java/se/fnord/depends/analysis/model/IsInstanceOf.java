package se.fnord.depends.analysis.model;


import se.fnord.graph.model.AbstractEdgeEntity;

public class IsInstanceOf extends AbstractEdgeEntity<Instance, Artifact> {
    public IsInstanceOf(Instance instance, Artifact artifact) {
        super(instance, artifact);
    }
}
