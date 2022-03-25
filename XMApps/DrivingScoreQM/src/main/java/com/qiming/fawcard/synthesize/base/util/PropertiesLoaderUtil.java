package com.qiming.fawcard.synthesize.base.util;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by summit on 3/9/17.
 */

public class PropertiesLoaderUtil {
    public static Properties load(Class<?> clz, String propertyFileName) {
        if (clz == null) {
            return null;
        }
        InputStream subAppCfgIs = clz.getClassLoader().getResourceAsStream(propertyFileName);
        Properties subAppCfg = new Properties();
        try {
            subAppCfg.load(subAppCfgIs);
            return subAppCfg;
        } catch (Exception e) {
            return null;
        }
    }
}
