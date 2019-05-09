package org.ifzen.util;

import org.ifzen.util.DateTimeUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CacheManager {

    private static Map<String, ValueWrapper> _cache = new ConcurrentHashMap<String, ValueWrapper>();

    private CacheManager() {

    }

    public static Object get(String key) {
        ValueWrapper wrapper = _cache.get(key);
        if (null == wrapper) {
            return null;
        }

        // if the cache expires, remove it and return null
        if (new Date().after(wrapper.getExpireAt())) {
            _cache.remove(key);
            return null;
        }

        // if expireAt is null, that means TTL is long enough, it doesn't expire
        return wrapper.getValue();
    }

    public static void put(String key, Object value, Date expireAt) {

        ValueWrapper cacheWrapper = new ValueWrapper(value, expireAt);
        _cache.put(key, cacheWrapper);
    }

    //By default, the value expires at the start of a new day
    public static void putDaily(String key, Object value, Date timePoint) {
        put(key, value, DateTimeUtil.getOffsetFixedDateTime(timePoint, 1, Calendar.DATE, 0, 0, 0));
    }

    public static void putHourly(String key, Object value, Date timePoint) {

        Date expireAt = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timePoint);
        int minute = calendar.get(Calendar.MINUTE);

        //Since actual data replica finishes at around HH:10, so make the cache expires at HH:12, so the cache refreshes after HH:12
        expireAt = (minute >= 12 ? DateTimeUtil.getOffsetFixedDateTime(timePoint, 1, Calendar.HOUR_OF_DAY, null, 12, 0) :
                DateTimeUtil.getOffsetFixedDateTime(new Date(), 0, null, null, 12, 0));

        put(key, value, expireAt);
    }

    public static void clear(String key) {

        if (StringUtils.isBlank(key)) {
            clear();
        }
        _cache.remove(key);
    }

    public static void clearExpired() {

        Date now = new Date();
        Set<String> clearedCache = new HashSet<String>();
        for (Map.Entry<String, ValueWrapper> item : _cache.entrySet()) {

            ValueWrapper wrapper = item.getValue();
            String key = item.getKey();

            if (null == wrapper) {
                _cache.remove(item.getKey());
                clearedCache.add(key);
                continue;
            }

            Date expireAt = wrapper.getExpireAt();
            if (null != expireAt && expireAt.before(now)) {

                _cache.remove(item.getKey());
                clearedCache.add(key);
            }
        }
        log.info("cleared cache keys are : " + clearedCache);
    }

    public static void clear() {
        _cache.clear();
        log.info("All cache cleared");
    }
}


@Data
class ValueWrapper implements java.io.Serializable{
    private Date expireAt;
    private Object value;

    public ValueWrapper(Object value, Date expireAt) {
        this.value = value;
        this.expireAt = expireAt;
    }
}
