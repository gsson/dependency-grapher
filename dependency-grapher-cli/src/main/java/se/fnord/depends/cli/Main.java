package se.fnord.depends.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.DependencyResult;
import se.fnord.depends.ModelUpdater;
import se.fnord.depends.analysis.artifacts.Resolver;
import se.fnord.graph.GraphConnector;
import se.fnord.graph.orientdb.OrientDB;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class Main {
    public static Configuration loadConfiguration(String configPath) throws IOException {
        return loadConfiguration(new FileInputStream(configPath));
    }

    public static Configuration loadConfiguration(InputStream inputStream) throws IOException {
        YAMLFactory yamlFactory = new YAMLFactory(new ObjectMapper());
        YAMLParser yamlParser = yamlFactory.createParser(inputStream);
        return yamlParser.readValueAs(Configuration.class);
    }

    public static Resolver createResolver(Configuration.Resolver configuration) {
        Configuration.Repository localRepo = configuration.getLocalRepository();

        return Resolver.resolver(
                Resolver.local(localRepo.getContentType(), localRepo.getLocation()),
                configuration.getRemoteRepositories().stream()
                        .map(repo -> Resolver.remote(repo.getId(), repo.getContentType(), repo.getLocation()))
                        .toArray(RemoteRepository[]::new)
        );
    }

    private static GraphConnector<?, ?> createConnector(Configuration.Database configuration) throws IOException {
        switch (configuration.getConnector()) {
            case "orientdb":
                return OrientDB.connect(configuration.getUrl(), configuration.getUsername(), configuration.getPassword());
        }
        throw new IllegalArgumentException("Invalid connector type " + configuration.getConnector());
    }


    public static void run(Configuration configuration) throws Exception {
        Resolver resolver = createResolver(configuration.getResolver());

        GraphConnector<?, ?> connector = createConnector(configuration.getDatabase());
        ModelUpdater modelUpdater = new ModelUpdater(connector.connect());
        modelUpdater.initialize();

        for (Configuration.Application app : configuration.getApplications()) {
            System.err.println("Mucking about with " + app.getName());

            for (String artifact : app.getArtifacts()) {
                final DependencyResult result = resolver.resolveDependencies(artifact);

                modelUpdater.addToApplication(app.getName(), result.getRoot());
            }
        }

        long start = System.nanoTime();
        modelUpdater.finish();
        System.err.printf("Saved in %d ms%n", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
    }


    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Need config file");
        }

        Configuration configuration = loadConfiguration(args[0]);
        run(configuration);
    }
}
