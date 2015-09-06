package se.fnord.depends.analysis.artifacts;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Resolver {
    private final RepositorySystem repositorySystem;
    private final RepositorySystemSession repositorySystemSession;
    private final List<RemoteRepository> remotes;

    private Resolver(RepositorySystem repositorySystem, RepositorySystemSession repositorySystemSession, List<RemoteRepository> remotes) {
        this.repositorySystem = repositorySystem;
        this.repositorySystemSession = repositorySystemSession;
        this.remotes = remotes;
    }

    private static DefaultRepositorySystemSession newRepositorySystemSession(RepositorySystem system, LocalRepository localRepo) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

        session.setTransferListener(new ConsoleTransferListener(System.err));
        session.setRepositoryListener(new ConsoleRepositoryListener(System.err));

        return session;
    }

    private static RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

        locator.setErrorHandler(new DefaultServiceLocator.ErrorHandler() {
            @Override
            public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
                exception.printStackTrace();
            }
        });

        return locator.getService(RepositorySystem.class);
    }

    public static LocalRepository local(String contentType, String path) {
        return new LocalRepository(new File(path), contentType);
    }

    public static RemoteRepository remote(String id, String contentType, String url) {
        return new RemoteRepository.Builder(id, contentType, url).build();
    }

    public static Resolver resolver(LocalRepository local, RemoteRepository... remotes) {
        final RepositorySystem repositorySystem = newRepositorySystem();
        final RepositorySystemSession repositorySystemSession = newRepositorySystemSession(repositorySystem, local);
        return new Resolver(repositorySystem, repositorySystemSession, Arrays.asList(remotes));
    }

    public DependencyResult resolveDependencies(Artifact artifact) throws DependencyCollectionException, DependencyResolutionException {
        CollectRequest collectRequest = new CollectRequest();

        collectRequest.setRoot(new Dependency(artifact, ""));
        collectRequest.setRepositories(remotes);
        DependencyRequest dependencyRequest = new DependencyRequest();

        dependencyRequest.setCollectRequest(collectRequest);

        return repositorySystem.resolveDependencies(repositorySystemSession, dependencyRequest);
    }

    public DependencyResult resolveDependencies(String artifact) throws DependencyCollectionException, DependencyResolutionException {
        return resolveDependencies(new DefaultArtifact(artifact));
    }
}
