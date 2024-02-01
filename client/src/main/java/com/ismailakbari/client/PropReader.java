package com.ismailakbari.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

public class PropReader {

    public static Map<String, String> readProps(String filePath, String regex) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            properties.load(fileInputStream);
        }

        Map<String, String> filteredMap = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            if (Pattern.matches(regex, key)) {
                filteredMap.put(key, properties.getProperty(key));
            }
        }

        return filteredMap;
    }
}
