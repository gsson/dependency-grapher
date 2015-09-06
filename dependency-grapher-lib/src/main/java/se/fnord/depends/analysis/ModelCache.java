package se.fnord.depends.analysis;

import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;
import se.fnord.depends.analysis.model.*;
import se.fnord.depends.analysis.model.Class;
import se.fnord.depends.analysis.model.Package;
import se.fnord.graph.model.EntityMapper;
import se.fnord.graph.model.MapperBuilder;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ModelCache {
    private final EntityMapper<Application> applications;
    private final EntityMapper<ArtifactGroup> groups;
    private final EntityMapper<Artifact> artifacts;
    private final EntityMapper<Instance> instances;

    private final EntityMapper<ClassFile> files;
    private final EntityMapper<Package> packages;
    private final EntityMapper<Class> classes;
    private final Map<String, ClassFile> fileByName;

    private final EntityMapper<BelongsTo> belongsTo;
    private final EntityMapper<IsInstanceOf> isInstanceOf;
    private final EntityMapper<DependsOn> dependsOn;
    private final EntityMapper<PartOf> partOf;

    private final EntityMapper<References> references;
    private final EntityMapper<ImplementedBy> implementedBy;
    private final EntityMapper<ExportedBy> exportedBy;
    private final EntityMapper<Parent> parent;
    private final EntityMapper<DeclaredIn> declaredIn;
    private final MapperBuilder.Mappers mappers;

    public ModelCache(MapperBuilder.Mappers mappers) {
        this.mappers = mappers;
        applications = mappers.mapperFor(Application.class);
        groups = mappers.mapperFor(ArtifactGroup.class);
        artifacts = mappers.mapperFor(Artifact.class);
        instances = mappers.mapperFor(Instance.class);

        files = mappers.mapperFor(ClassFile.class);
        packages = mappers.mapperFor(Package.class);
        classes = mappers.mapperFor(Class.class);
        fileByName = HashObjObjMaps.newUpdatableMap();

        partOf = mappers.mapperFor(PartOf.class);
        belongsTo = mappers.mapperFor(BelongsTo.class);
        isInstanceOf = mappers.mapperFor(IsInstanceOf.class);
        dependsOn = mappers.mapperFor(DependsOn.class);

        references = mappers.mapperFor(References.class);
        implementedBy = mappers.mapperFor(ImplementedBy.class);
        exportedBy = mappers.mapperFor(ExportedBy.class);
        parent = mappers.mapperFor(Parent.class);
        declaredIn = mappers.mapperFor(DeclaredIn.class);
    }

    public Instance instance(org.eclipse.aether.artifact.Artifact artifact) {
        return instances.getEntity(new Instance(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion()));
    }

    public Artifact artifact(org.eclipse.aether.artifact.Artifact artifact) {
        return artifacts.getEntity(new Artifact(artifact.getGroupId(), artifact.getArtifactId()));
    }

    public ArtifactGroup group(org.eclipse.aether.artifact.Artifact artifact) {
        return groups.getEntity(new ArtifactGroup(artifact.getGroupId()));
    }

    public Application application(String name) {
        return applications.getEntity(new Application(name));
    }

    public BelongsTo belongsTo(Artifact artifact1, ArtifactGroup group) {
        return belongsTo.getEntity(new BelongsTo(artifact1, group));
    }

    public IsInstanceOf isInstanceOf(Instance instance, Artifact artifact1) {
        return isInstanceOf.getEntity(new IsInstanceOf(instance, artifact1));
    }

    public DependsOn dependsOn(Instance dependent, Instance dependee, String scope, boolean optional) {
        return dependsOn.getEntity(new DependsOn(dependent, dependee, scope, optional));
    }

    public PartOf partOf(Instance instance, Application application) {
        return partOf.getEntity(new PartOf(instance, application));
    }

    public Class cls(String name) {
        return classes.getEntity(new Class(name));
    }

    public boolean hasCls(String name) {
        return classes.containsEntity(new Class(name));
    }

    public ClassFile file(String fileName) {
        ClassFile c = files.getEntity(new ClassFile(fileName));
        fileByName.put(fileName, c);
        return c;
    }

    public Package pkg(String name) {
        return packages.getEntity(new Package(name));
    }

    public boolean hasPkg(String name) {
        return packages.containsEntity(new Package(name));
    }

    public ExportedBy exportedBy(ClassFile file, Instance instance) {
        return exportedBy.getEntity(new ExportedBy(file, instance));
    }

    public ImplementedBy implementedBy(Class cls, ClassFile file, int modifiers) {
        return implementedBy.getEntity(new ImplementedBy(cls, file, modifiers));
    }

    public References references(ClassFile file, Class cls) {
        return references.getEntity(new References(file, cls));
    }

    public Parent parent(Package child, Package parent) {
        return this.parent.getEntity(new Parent(child, parent));
    }

    public DeclaredIn declaredIn(Class cls, Package pkg) {
        return declaredIn.getEntity(new DeclaredIn(cls, pkg));
    }

    public int size() {
        return IntStream.concat(
                Stream.of(groups, artifacts, instances, files, classes, packages).mapToInt(e -> e.size()),
                Stream.of(belongsTo, isInstanceOf, dependsOn, partOf, implementedBy, references, exportedBy, parent, declaredIn).mapToInt(e -> e.size())
        ).sum();
    }

    public void save(Consumer<Object> elementConsumer) {
        mappers.materialise(elementConsumer);
    }

}
