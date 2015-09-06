package se.fnord.depends.analysis.model;

import se.fnord.graph.model.AbstractEdgeEntity;

public class PartOf extends AbstractEdgeEntity<Instance, Application> {
    public PartOf(Instance instance, Application application) {
        super(instance, application);
    }
}
