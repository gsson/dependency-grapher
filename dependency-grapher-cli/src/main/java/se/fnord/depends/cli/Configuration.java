package se.fnord.depends.cli;

import java.util.List;
import java.util.Objects;

public class Configuration {
    public static class Resolver {
        private Repository localRepository;
        private List<Repository> remoteRepositories;

        public Repository getLocalRepository() {
            return localRepository;
        }

        public void setLocalRepository(Repository localRepository) {
            this.localRepository = localRepository;
        }

        public List<Repository> getRemoteRepositories() {
            return remoteRepositories;
        }

        public void setRemoteRepositories(List<Repository> remoteRepositories) {
            this.remoteRepositories = remoteRepositories;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Resolver resolver = (Resolver) o;
            return Objects.equals(localRepository, resolver.localRepository) &&
                    Objects.equals(remoteRepositories, resolver.remoteRepositories);
        }

        @Override
        public int hashCode() {
            return Objects.hash(localRepository, remoteRepositories);
        }
    }

    public static class Repository {
        public String contentType;
        public String id;
        public String location;

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Repository repository = (Repository) o;
            return Objects.equals(contentType, repository.contentType) &&
                    Objects.equals(id, repository.id) &&
                    Objects.equals(location, repository.location);
        }

        @Override
        public int hashCode() {
            return Objects.hash(contentType, id, location);
        }
    }


    public static class Application {
        private String name;
        private List<String> artifacts;

        public String getName() {
            return name;
        }

        public List<String> getArtifacts() {
            return artifacts;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setArtifacts(List<String> artifacts) {
            this.artifacts = artifacts;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Application that = (Application) o;
            return Objects.equals(name, that.name) &&
                    Objects.equals(artifacts, that.artifacts);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, artifacts);
        }
    }


    public static class Database {
        private String connector;
        private String url;
        private String username;
        private String password;

        public String getConnector() {
            return connector;
        }

        public String getUrl() {
            return url;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public void setConnector(String connector) {
            this.connector = connector;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Database database = (Database) o;
            return Objects.equals(connector, database.connector) &&
                    Objects.equals(url, database.url) &&
                    Objects.equals(username, database.username) &&
                    Objects.equals(password, database.password);
        }

        @Override
        public int hashCode() {
            return Objects.hash(connector, url, username, password);
        }
    }

    private Database database;
    private List<Application> applications;
    private Resolver resolver;

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    public Resolver getResolver() {
        return resolver;
    }

    public void setResolver(Resolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Configuration that = (Configuration) o;
        return Objects.equals(database, that.database) &&
                Objects.equals(applications, that.applications) &&
                Objects.equals(resolver, that.resolver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(database, applications, resolver);
    }
}
