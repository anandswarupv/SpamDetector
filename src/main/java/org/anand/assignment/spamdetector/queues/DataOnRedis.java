package org.anand.assignment.spamdetector.queues;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.anand.assignment.spamdetector.utils.SystemProperties;
import org.springframework.stereotype.Component;

/**
 * Wrapper for DataStructure that could be stored on shared in-memory storage
 * like Redis.
 * 
 * All workers should have access to this data
 * 
 * @author anand
 *
 */
@Component
public class DataOnRedis {
    
    private ConcurrentMap<String, Integer> flaggedProfiles;
    private ConcurrentMap<String, Integer> blockedProfiles;

    /**
     * Constructs a Queue using {@link ConcurrentHashMap} for flagged and
     * blocked profiles
     */
    public DataOnRedis() {
        this.flaggedProfiles = new ConcurrentHashMap<String, Integer>();
        this.blockedProfiles = new ConcurrentHashMap<String, Integer>();
    }

    /**
     * Constructs a Queue using the specified ConcurrentMap implementations
     * 
     * @param flaggedProfiles
     * @param blockedProfiles
     */
    public DataOnRedis(ConcurrentMap<String, Integer> flaggedProfiles, ConcurrentMap<String, Integer> blockedProfiles) {
        this.flaggedProfiles = flaggedProfiles;
        this.blockedProfiles = blockedProfiles;
    }

    /**
     * Add the given profile to the Flagged Queue.If the profile already exists,
     * then the count gets incremented by 1.
     * 
     * @param sourceProfileId
     * @param count
     * @throws InterruptedException
     */
    public void addToFlaggedQueue(String sourceProfileId, Integer count) throws InterruptedException {
        Integer oldValue = flaggedProfiles.put(sourceProfileId, count);
        if (oldValue != null) {
            if (oldValue >= SystemProperties.NUMBER_OF_FLAGGINGS_ALLOWED_BEFORE_BLOCKING) {
                blockedProfiles.put(sourceProfileId, count);
                flaggedProfiles.remove(sourceProfileId);
            } else {
                oldValue++;
                flaggedProfiles.put(sourceProfileId, oldValue);
            }
        }
    }

    /**
     * Add the given profile to the Blocked Queue.If the profile already exists,
     * then the count gets incremented by 1.
     * 
     * @param sourceProfileId
     * @param count
     * @throws InterruptedException
     */
    public void addToBlockingQueue(String sourceProfileId, Integer count) throws InterruptedException {
        blockedProfiles.put(sourceProfileId, count);
    }

    public Set<String> getFlaggedProfiles() {
        Set<String> sourceProfiles = flaggedProfiles.keySet();
        return sourceProfiles;
    }

    public Set<String> getBlockedProfiles() {
        Set<String> sourceProfiles = blockedProfiles.keySet();
        return sourceProfiles;
    }

}
