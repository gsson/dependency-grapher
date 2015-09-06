package se.fnord.depends;

import org.eclipse.aether.graph.DependencyNode;
import se.fnord.depends.analysis.DependencySchema;
import se.fnord.depends.analysis.ModelCache;
import se.fnord.depends.analysis.classes.JarAnalyser;
import se.fnord.depends.analysis.classes.JarContents;
import se.fnord.depends.analysis.model.*;
import se.fnord.depends.analysis.model.Class;
import se.fnord.depends.analysis.model.Package;
import se.fnord.graph.GraphConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ModelUpdater {

    private final GraphConnection<?, ?> connection;
    private ModelCache modelCache;


    public ModelUpdater(GraphConnection<?, ?> connection) {
        this.connection = connection;
    }

    private static class Progress {
        private final long intervalMillis;
        private final int max;
        private final int commitIntervalUpdates;
        private int current;
        private long nextReport;
        private int nextCommit;

        public Progress(int max, long reportIntervalMillis, int commitIntervalUpdates) {
            this.intervalMillis = reportIntervalMillis;
            this.max = max;
            this.commitIntervalUpdates = commitIntervalUpdates;
            this.current = 0;
            this.nextReport = 0;
            this.nextCommit = 0;
        }

        public void next() {
            current++;
            if (current >= nextCommit) {

                nextCommit = current + commitIntervalUpdates;
            }
            long now = System.currentTimeMillis();
            if (now >= nextReport) {
                nextReport = now + intervalMillis;
                System.err.printf("\r%d of %d", current, max);
            }
        }
    }

    private Instance addInstance(DependencyNode node) throws IOException {
        final ArtifactGroup group = modelCache.group(node.getArtifact());
        final Artifact artifact = modelCache.artifact(node.getArtifact());
        final Instance instance = modelCache.instance(node.getArtifact());

        modelCache.belongsTo(artifact, group);
        modelCache.isInstanceOf(instance, artifact);

        final File jarFile = node.getArtifact().getFile();

        final List<JarContents> result = JarAnalyser.analyse(new FileInputStream(jarFile));

        result.stream().forEach(jarClass -> addClass(instance, jarClass));

        return instance;
    }

    static List<String> packagePath(String packageName) {
        int last = packageName.lastIndexOf('/');
        if (last == -1)
            return Collections.singletonList(packageName);

        List<String> path = new ArrayList<>();
        path.add(packageName);

        while (last != -1) {
            packageName = packageName.substring(0, last);
            path.add(packageName);
            last = packageName.lastIndexOf('/');
        }

        return path;

    }

    static List<String> packagePathFromClass(String className) {
        int last = className.lastIndexOf('/');
        if (last == -1)
            return Collections.emptyList();
        return packagePath(className.substring(0, last));
    }

    private Optional<Package> addPackage(String className) {
        final List<String> strings = packagePathFromClass(className);

        Iterator<String> it = strings.iterator();
        if (!it.hasNext())
            return Optional.empty();

        String name = it.next();
        if (modelCache.hasPkg(name))
            return Optional.of(modelCache.pkg(name));

        Package child = modelCache.pkg(name);
        Optional<Package> result = Optional.of(child);
        while (it.hasNext()) {
            Package parent;
            name = it.next();
            if (modelCache.hasPkg(name)) {
                parent = modelCache.pkg(name);
                modelCache.parent(child, parent);
                break;
            }
            parent = modelCache.pkg(name);
            modelCache.parent(child, parent);
            child = parent;
        }
        return result;
    }

    private Class addClass(String className) {
        if (modelCache.hasCls(className))
            return modelCache.cls(className);

        final se.fnord.depends.analysis.model.Class cls = modelCache.cls(className);
        addPackage(className).ifPresent(pkg -> modelCache.declaredIn(cls, pkg));

        return cls;
    }

    private void addClass(Instance instance, JarContents jarContents) {
        final ClassFile file = modelCache.file(jarContents.getClassFile());

        final se.fnord.depends.analysis.model.Class cls = addClass(jarContents.getExportedType());

        modelCache.exportedBy(file, instance);
        modelCache.implementedBy(cls, file, jarContents.getModifiers());

        for (String reference : jarContents.getReferences()) {
            final se.fnord.depends.analysis.model.Class ref = addClass(reference);

            modelCache.references(file, ref);
        }
    }

    private Application addApplication(String name) {
        return modelCache.application(name);
    }

    private void addDependencyRec(Instance dependent, List<DependencyNode> children) throws IOException {
        for (DependencyNode dependeeNode : children) {
            final Instance dependee = addInstance(dependeeNode);
            modelCache.dependsOn(dependent, dependee, dependeeNode.getDependency().getScope(), dependeeNode.getDependency().getOptional());
            addDependencyRec(dependee, dependeeNode.getChildren());
        }
    }

    public void initialize() throws Exception {
        modelCache = new ModelCache(DependencySchema.create(connection));
    }

    public void finish() throws Exception {
        final Progress progress = new Progress(modelCache.size(), 5000, 1000);
        modelCache.save(o -> progress.next());
        System.err.println();
        connection.close();
    }

    public void addToApplication(String applicationName, DependencyNode node) throws IOException {
        final Application application = addApplication(applicationName);
        final Instance root = addInstance(node);
        modelCache.partOf(root, application);

        addDependencyRec(root, node.getChildren());
    }
}
