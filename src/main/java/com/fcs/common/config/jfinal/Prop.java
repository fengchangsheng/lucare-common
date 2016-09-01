package com.fcs.common.config.jfinal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by Lucare.Feng on 2016/9/1.
 */
public class Prop {

    private Properties properties;

    public Prop(String fileName) {
        this(fileName,"UTF-8");
    }

    public Prop(String fileName, String encoding) {
        this.properties = null;
        InputStream inputStream = null;
        try {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if (inputStream == null) {
                throw new IllegalArgumentException("Properties file not found in classpath: " + fileName);
            }
            this.properties = new Properties();
            this.properties.load(new InputStreamReader(inputStream,encoding));
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties file.", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {

                }
            }
        }
    }

    public String get(String key) {
        return this.properties.getProperty(key);
    }

    public String get(String key, String defaultValue) {
        return this.properties.getProperty(key, defaultValue);
    }
}
