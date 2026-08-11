package com.geometry.ui.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.MissingResourceException;
import java.util.Properties;

/** Chinese product copy used by the Swing presentation layer. */
public final class UiStrings {

    private static final Properties PROPS = loadProperties();

    private UiStrings() {
    }

    private static Properties loadProperties() {
        Properties p = new Properties();
        try (InputStream is = UiStrings.class.getClassLoader()
                .getResourceAsStream("i18n/messages_zh_CN.properties");
             InputStreamReader reader = is != null
                     ? new InputStreamReader(is, StandardCharsets.UTF_8)
                     : null) {
            if (reader != null) {
                p.load(reader);
            }
        } catch (IOException e) {
            // Fallback to empty properties; key will be returned as-is.
        }
        return p;
    }

    public static String text(String key) {
        try {
            String value = PROPS.getProperty(key);
            if (value == null) {
                return key;
            }
            return value;
        } catch (MissingResourceException ex) {
            return key;
        }
    }
}
