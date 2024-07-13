package example.annotation;


import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class YmlConfigLoader {
    private final Map<String, Object> config;

    public YmlConfigLoader(String filePath) {
        Yaml yaml = new Yaml();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(filePath)) {
            config = yaml.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config file", e);
        }
    }

    public Object getValue(String key) {
        String[] keys = key.split("\\.");
        Map<String, Object> map = config;
        for (int i = 0; i < keys.length - 1; i++) {
            map = (Map<String, Object>) map.get(keys[i]);
        }
        return map.get(keys[keys.length - 1]);
    }
}
