package org.yanzhe.robomaster.recodaemon.core.utils;

import java.util.HashMap;
import java.util.Map;

public interface CachedSingleton {
    Map<Class, CachedSingleton> instances = new HashMap<>();

    @SuppressWarnings("unchecked")
    static <T extends CachedSingleton> T getInstance(Class<T> cls) {
        T instance = null;
        try {
            Object got = instances.get(cls);
            if (got != null) instance = (T) got;
            else {
                instance = cls.getDeclaredConstructor().newInstance();
                System.out.println(1111111);
//        instance=(T)cls.getMethod("getInstance").invoke(null);
                instances.put(cls, instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }
}
