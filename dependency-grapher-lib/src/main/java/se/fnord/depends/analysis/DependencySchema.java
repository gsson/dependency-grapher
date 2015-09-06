package se.fnord.depends.analysis;

import se.fnord.depends.analysis.model.*;
import se.fnord.depends.analysis.model.Class;
import se.fnord.depends.analysis.model.Package;
import se.fnord.graph.GraphConnection;
import se.fnord.graph.model.MapperBuilder;
import se.fnord.graph.schema.PropertyType;

import static se.fnord.graph.schema.SchemaBuilder.property;

public class DependencySchema {
    private static final String REFERENCES = "References";
    private static final String IMPLEMENTED_BY = "ImplementedBy";
    private static final String PARENT = "Parent";
    private static final String EXPORTED_BY = "ExportedBy";
    private static final String DECLARED_IN = "DeclaredIn";

    private static final String PACKAGE = "Package";
    private static final String FILE = "File";
    private static final String CLASS = "Class";

    private static final String APPLICATION = "Application";

    private static final String ARTIFACT_INSTANCE = "ArtifactInstance";
    private static final String ARTIFACT = "Artifact";
    private static final String ARTIFACT_GROUP = "ArtifactGroup";

    private static final String DEPENDS_ON = "DependsOn";
    private static final String BELONGS_TO = "BelongsTo";
    private static final String IS_INSTANCE_OF = "IsInstanceOf";
    private static final String PART_OF = "PartOf";

    public static <_EDGE, _VERTEX>
    MapperBuilder.Mappers create(GraphConnection<_EDGE, _VERTEX> graphConnection) {
        final MapperBuilder.Mappers mappers = new MapperBuilder()
                .vertex(ClassFile.class, FILE,
                        property("fileName", PropertyType.STRING))
                .vertex(Class.class, CLASS,
                        property("name", PropertyType.STRING))
                .vertex(Package.class, PACKAGE,
                        property("name", PropertyType.STRING))

                .edge(References.class, FILE, REFERENCES, CLASS)
                .edge(ImplementedBy.class, CLASS, IMPLEMENTED_BY, FILE,
                        property("modifiers", PropertyType.INT))
                .edge(Parent.class, PACKAGE, PARENT, PACKAGE)
                .edge(ExportedBy.class, FILE, EXPORTED_BY, ARTIFACT_INSTANCE)
                .edge(DeclaredIn.class, CLASS, DECLARED_IN, PACKAGE)

                .vertex(Instance.class, ARTIFACT_INSTANCE,
                        property("label", PropertyType.STRING),
                        property("version", PropertyType.STRING))
                .vertex(Artifact.class, ARTIFACT,
                        property("label", PropertyType.STRING),
                        property("artifactId", PropertyType.STRING))
                .vertex(ArtifactGroup.class, ARTIFACT_GROUP,
                        property("groupId", PropertyType.STRING))
                .vertex(Application.class, APPLICATION,
                        property("name", PropertyType.STRING))

                .edge(IsInstanceOf.class, ARTIFACT_INSTANCE, IS_INSTANCE_OF, ARTIFACT)
                .edge(BelongsTo.class, ARTIFACT, BELONGS_TO, ARTIFACT_GROUP)
                .edge(PartOf.class, ARTIFACT_INSTANCE, PART_OF, APPLICATION)
                .edge(DependsOn.class, ARTIFACT_INSTANCE, DEPENDS_ON, ARTIFACT_INSTANCE,
                        property("scope", PropertyType.STRING),
                        property("optional", PropertyType.BOOLEAN))
                .build(graphConnection);

        return mappers;
    }


}
