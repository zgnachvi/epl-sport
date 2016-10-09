package com.goodbarber.premierleaguene.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ProjectConfig {

    private static Logger LOGGER = LoggerFactory.getLogger(ProjectConfig.class);

    private static final Map<Object, Object> properties = new HashMap<>();

    public static <T, R> R get(T key){
        if (properties.containsKey(key)) {
            return (R)properties.get(key);
        }
        throw new RuntimeException("key does not find");
    }

    public static void load(){

        Properties prop = new Properties();

        try (final InputStream inputStream =
                     ProjectConfig.class.getResourceAsStream("/app.properties")){

            prop.load(inputStream);

           for (Object key : prop.keySet()) {
               properties.put(key, prop.get(key));
           }

        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

}
