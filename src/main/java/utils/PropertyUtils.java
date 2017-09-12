package utils;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertyUtils {
    private static final Logger LOGGER = Logger.getLogger(PropertyUtils.class);
    private static final String PROPERTY_FILE_PATH = "configuration.properties";
    private static Properties prop = new Properties();

    public static String getProperty(String key) {
        String systemProperty = System.getProperty(key);
        if (systemProperty == null) {
            if (!prop.propertyNames().hasMoreElements()) {
                try {
                    prop.load(new FileReader(new File(PROPERTY_FILE_PATH)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Object value = prop.get(key);
            if (value == null) {
                LOGGER.warn("property '" + key + "' in file '" + PROPERTY_FILE_PATH + "' required");
            }
            return String.valueOf(value);
        }
        return systemProperty;
    }

}
