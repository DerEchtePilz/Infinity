package io.github.derechtepilz.infinity.util;

import java.util.Map;

public class SaveUtil<K, V> {

    private final Map<K, V> loadedMap;

    public SaveUtil(Map<K, V> loadedMap) {
        this.loadedMap = loadedMap;
    }

    public void saveTo(Map<K, V> dataMap) {
        for (K key : loadedMap.keySet()) {
            dataMap.put(key, loadedMap.get(key));
        }
    }

}
