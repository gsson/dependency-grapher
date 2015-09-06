package se.fnord.depends.analysis.artifacts;

import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.RepositoryEvent;

import java.io.PrintStream;
import java.util.Objects;


class ConsoleRepositoryListener extends AbstractRepositoryListener {
    private final PrintStream out;

    public ConsoleRepositoryListener(PrintStream out) {
        this.out = Objects.requireNonNull(out);
    }

    public void artifactDescriptorInvalid(RepositoryEvent event) {
        out.printf("Invalid artifact descriptor for %s: %s%n", event.getArtifact(), event.getException().getMessage());
    }

    public void artifactDescriptorMissing(RepositoryEvent event) {
        out.printf("Missing artifact descriptor for %s%n", event.getArtifact());
    }

    public void artifactResolving(RepositoryEvent event) {
        out.printf("Resolving artifact %s%n", event.getArtifact());
    }

    public void artifactResolved(RepositoryEvent event) {
        out.printf("Resolved artifact %s: %s%n", event.getArtifact(), event.getRepository());
    }

    public void artifactDownloading(RepositoryEvent event) {
        out.printf("Downloading artifact %s: %s%n", event.getArtifact(), event.getRepository());
    }

    public void artifactDownloaded(RepositoryEvent event) {
        out.printf("Downloaded artifact %s: %s%n", event.getArtifact(), event.getRepository());
    }

}
