package se.fnord.depends.analysis.model;


import se.fnord.graph.elements.PropertiesBuilder;
import se.fnord.graph.model.AbstractEdgeEntity;

public class DependsOn extends AbstractEdgeEntity<Instance, Instance> {
    private final String scope;
    private final boolean optional;

    public DependsOn(Instance dependent, Instance dependee, String scope, boolean optional) {
        super(dependent, dependee);
        this.scope = scope;
        this.optional = optional;
    }

    @Override
    public void setProperties(PropertiesBuilder<?> builder) {
        builder
            .set("scope", scope)
            .set("optional", optional);
    }
}
