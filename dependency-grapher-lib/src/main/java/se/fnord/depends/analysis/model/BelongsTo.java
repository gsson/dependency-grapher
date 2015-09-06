package se.fnord.depends.analysis.model;


import se.fnord.graph.model.AbstractEdgeEntity;

public class BelongsTo extends AbstractEdgeEntity<Artifact, ArtifactGroup> {
    public BelongsTo(Artifact artifact, ArtifactGroup artifactGroup) {
        super(artifact, artifactGroup);
    }
}
