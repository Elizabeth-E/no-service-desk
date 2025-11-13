package nl.inholland.student.noservicedesk;

import java.util.Properties;

public class AppContext {

    private final Properties config;

    public AppContext(Properties config) {
        this.config = config;
    }

    public String get(String key) {
        return config.getProperty(key);
    }

    public Properties getProperties() {
        return config;
    }
}
