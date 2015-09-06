package se.fnord.graph.orientdb;

import com.orientechnologies.orient.client.remote.OEngineRemote;
import com.orientechnologies.orient.core.engine.local.OEngineLocalPaginated;
import com.orientechnologies.orient.core.engine.memory.OEngineMemory;

import java.util.Objects;

public class OrientURL {
    private final String connectionType;
    private final String hostPort;
    private final String path;
    private final String dbName;

    public OrientURL(String connectionType, String hostPort, String path, String dbName) {
        this.connectionType = connectionType;
        this.hostPort = hostPort;
        this.path = path;
        this.dbName = dbName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(connectionType);
        sb.append(':');
        if (hostPort != null) {
            sb.append(hostPort);
        }
        if (path != null) {
            sb.append(path);
        }
        if (dbName != null) {
            sb.append(dbName);
        }

        return sb.toString();
    }

    public String getConnectionType() {
        return connectionType;
    }

    public String getHostPort() {
        return hostPort;
    }

    public String getPath() {
        return path;
    }

    public String getDbName() {
        return dbName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrientURL orientURL = (OrientURL) o;
        return Objects.equals(connectionType, orientURL.connectionType) &&
                Objects.equals(hostPort, orientURL.hostPort) &&
                Objects.equals(path, orientURL.path) &&
                Objects.equals(dbName, orientURL.dbName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectionType, hostPort, path, dbName);
    }

    public static OrientURL create(String url) {
        if (url.startsWith(OEngineRemote.NAME)) {
            url = url.substring(OEngineRemote.NAME.length() + 1);
            int pathIndex = url.indexOf('/');
            if (pathIndex == -1)
                return new OrientURL(OEngineRemote.NAME, url, "/", null);

            String hostPort = url.substring(0, pathIndex);
            url = url.substring(pathIndex);

            int dbNameIndex = url.lastIndexOf('/');
            String dbName = url.substring(dbNameIndex + 1);
            return new OrientURL(OEngineRemote.NAME, hostPort, url.substring(0, dbNameIndex + 1), dbName.isEmpty() ? null : dbName);
        }
        else if (url.startsWith(OEngineLocalPaginated.NAME)) {
            url = url.substring(OEngineLocalPaginated.NAME.length() + 1);
            if (url.isEmpty())
                return new OrientURL(OEngineLocalPaginated.NAME, null, null, null);

            int dbNameIndex = url.lastIndexOf('/');
            if (dbNameIndex == -1)
                return new OrientURL(OEngineLocalPaginated.NAME, null, "", url.isEmpty() ? null : url);

            String dbName = url.substring(dbNameIndex + 1);
            return new OrientURL(OEngineLocalPaginated.NAME, null, url.substring(0, dbNameIndex + 1), dbName.isEmpty() ? null : dbName);
        }
        else if (url.startsWith(OEngineMemory.NAME)) {
            url = url.substring(OEngineMemory.NAME.length() + 1);
            return new OrientURL(OEngineMemory.NAME, null, null, url.isEmpty() ? null : url);
        }
        throw new IllegalArgumentException("Invalid OrientDB URL");
    }
}
