package example.annotation;

import example.annotation.YmlConfigLoader;

import java.lang.reflect.Field;

public class ConfigValueProcessor {
    private final YmlConfigLoader configLoader;

    public ConfigValueProcessor(YmlConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    public void process(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(YamlValue.class)) {
                YamlValue annotation = field.getAnnotation(YamlValue.class);
                String key = annotation.value();
                Object value = configLoader.getValue(key);
                if (value != null) {
                    field.setAccessible(true);
                    try {
                        field.set(obj, convertToFieldType(field.getType(), value));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to set field value", e);
                    }
                }
            }
        }
    }

    private Object convertToFieldType(Class<?> fieldType, Object value) {
        if (fieldType == String.class) {
            return value.toString();
        } else if (fieldType == int.class || fieldType == Integer.class) {
            return Integer.parseInt(value.toString());
        } else if (fieldType == long.class || fieldType == Long.class) {
            return Long.parseLong(value.toString());
        } else if (fieldType == boolean.class || fieldType == Boolean.class) {
            return Boolean.parseBoolean(value.toString());
        }
        throw new IllegalArgumentException("Unsupported field type: " + fieldType);
    }
}
