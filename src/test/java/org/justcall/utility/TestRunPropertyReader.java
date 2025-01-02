package org.justcall.utility;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TestRunPropertyReader {
    public static Properties properties;

    public static String propertiesName = "";

    public static String getPropertyMethod(String key) {
        if (ObjectUtils.isEmpty(properties)) {
            properties = new Properties();
            try {
                FileInputStream fileInputStream;
                if (StringUtils.isEmpty(propertiesName))
                    fileInputStream = new FileInputStream("src/test/resources/testrun.properties");
                else
                    fileInputStream = new FileInputStream("src/test/resources/" + propertiesName);

                properties.load(fileInputStream);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return properties.getProperty(key);
    }
}
