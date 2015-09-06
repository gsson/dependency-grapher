package se.fnord.depends.analysis.artifacts;

import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferResource;

import java.io.PrintStream;
import java.util.Objects;

class ConsoleTransferListener extends AbstractTransferListener {
    private final PrintStream out;

    public ConsoleTransferListener(PrintStream out) {
        this.out = Objects.requireNonNull(out);
    }

    private static String resourceName(TransferResource resource) {
        return resource.getRepositoryUrl() + resource.getResourceName();
    }

    @Override
    public void transferInitiated(TransferEvent event) {
        out.printf("Downloading %s: ... ", resourceName(event.getResource()));
    }

    @Override
    public void transferSucceeded(TransferEvent event) {
        out.println("Success");
    }

    @Override
    public void transferFailed(TransferEvent event) {
        out.println("Failed");

        event.getException().printStackTrace(out);
    }

    @Override
    public void transferCorrupted(TransferEvent event) {
        out.println("Failed");

        event.getException().printStackTrace(out);
    }
}
