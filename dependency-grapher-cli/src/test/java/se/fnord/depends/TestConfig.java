package se.fnord.depends;

import org.junit.Test;
import se.fnord.depends.cli.Configuration;
import se.fnord.depends.cli.Main;

import java.io.IOException;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class TestConfig {



    public Configuration createConfiguration() {
        final Configuration.Database database = new Configuration.Database();
        database.setConnector("connector");
        database.setUrl("url");
        database.setUsername("username");
        database.setPassword("password");

        Configuration.Application application = new Configuration.Application();
        application.setName("name");
        application.setArtifacts(asList("art1", "art2"));

        final Configuration expected = new Configuration();
        expected.setDatabase(database);
        expected.setApplications(asList(application));
        return expected;
    }



    @Test
    public void testLoad() throws IOException {
        final Configuration expected = createConfiguration();
        final Configuration configuration = Main.loadConfiguration(TestConfig.class.getResourceAsStream("TestConfig.yml"));

        assertEquals(expected, configuration);

    }
}
