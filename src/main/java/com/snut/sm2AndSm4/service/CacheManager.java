package com.snut.sm2AndSm4.service;

import java.util.HashMap;
import java.util.Map;

/**
 * 缓存
 */
public class CacheManager {
    private static Map<String, String> cacheMap = null;


    public static Map<String, String> getInstance() {
        if (cacheMap == null) {
            cacheMap = new HashMap();
        }
        return cacheMap;
    }

    // 添加缓存
    public void addCacheData(String key, String obj) {
        cacheMap.put(key, obj);
    }

    // 获取缓存
    public String getCacheData(String key) {
        return cacheMap.get(key);
    }

    // 删除缓存
    public void removeCacheData(String key) {
        cacheMap.remove(key);
    }


}
