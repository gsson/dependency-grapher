package se.fnord.graph.orientdb;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestOrientURL {
    @Test
    public void remoteHappyCase() {
        final OrientURL remote = OrientURL.create("remote:ap.se:3434/fnord");

        assertEquals(new OrientURL("remote", "ap.se:3434", "/", "fnord"), remote);
        assertEquals("remote:ap.se:3434/fnord", remote.toString());
    }

    @Test
    public void remoteWithEmptyDbNameShouldBeNull() {
        final OrientURL remote = OrientURL.create("remote:ap.se:3434/");

        assertEquals(new OrientURL("remote", "ap.se:3434", "/", null), remote);
        assertEquals("remote:ap.se:3434/", remote.toString());
    }

    @Test
    public void remoteShouldAddTrailingSlashPath() {
        final OrientURL remote = OrientURL.create("remote:ap.se:3434");

        assertEquals(new OrientURL("remote", "ap.se:3434", "/", null), remote);
        assertEquals("remote:ap.se:3434/", remote.toString());
    }

    @Test
    public void plocalHappyCase() {
        final OrientURL remote = OrientURL.create("plocal:/fnord");

        assertEquals(new OrientURL("plocal", null, "/", "fnord"), remote);
        assertEquals("plocal:/fnord", remote.toString());
    }

    @Test
    public void memoryHappyCase() {
        final OrientURL url = OrientURL.create("memory:/fnord");

        assertEquals(new OrientURL("memory", null, null, "/fnord"), url);
        assertEquals("memory:/fnord", url.toString());
    }


}
