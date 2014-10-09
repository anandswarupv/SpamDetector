package org.anand.assignment.spamdetector.cache;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.anand.assignment.spamdetector.queues.DataOnRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.MapMaker;

@Component
public class MessageCountMapWithTimeBasedEviction {

    private int TIMEOUT = 10;

    @Autowired
    DataOnRedis dataOnRedis;

    @SuppressWarnings("deprecation")
    ConcurrentMap<String, Integer> messageCountMap = new MapMaker().expiration(TIMEOUT, TimeUnit.SECONDS).makeMap();

    /**
     * Returns the value to which the specified key is mapped, or {@code null}
     * if this map contains no mapping for the key.
     * 
     * @param key
     * @return
     */
    public Integer get(String key) {
        return messageCountMap.get(key);
    }

    /**
     * Associates the specified value with the specified key in this map. If the
     * map previously contained a mapping for the key, the old value is
     * incremented by 1.
     * 
     * If the old value goes higher than 50, it is reset to 1 and the key get
     * added to the flagged Profiles Queue.
     * 
     * The key gets timed out after 10 seconds.
     * 
     * @param key
     * @param value
     * @throws InterruptedException
     */
    public void put(String key, Integer value) throws InterruptedException {
        Integer oldValue = messageCountMap.put(key, value);
        if (oldValue != null) {
            if (oldValue >= 50) {
                messageCountMap.put(key, value);
                dataOnRedis.addToFlaggedQueue(key, value);
            } else {
                oldValue++;
                messageCountMap.put(key, oldValue);
            }
        }
    }

    /**
     * Removes the mapping for a key from this map if it is present
     * 
     * @param key
     */
    public void remove(String key) {
        messageCountMap.remove(key);
    }

}
