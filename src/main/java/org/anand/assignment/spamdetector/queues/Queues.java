package org.anand.assignment.spamdetector.queues;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

@Component
public class Queues {
    
    private ConcurrentMap<String, Integer> flaggedProfiles;
    private ConcurrentMap<String, Integer> blockedProfiles;

    /**
     * Constructs a Queue using {@link ConcurrentHashMap} for flagged and
     * blocked profiles
     */
    public Queues() {
        this.flaggedProfiles = new ConcurrentHashMap<String, Integer>();
        this.blockedProfiles = new ConcurrentHashMap<String, Integer>();
    }

    /**
     * Constructs a Queue using the specified ConcurrentMap implementations
     * 
     * @param flaggedProfiles
     * @param blockedProfiles
     */
    public Queues(ConcurrentMap<String, Integer> flaggedProfiles, ConcurrentMap<String, Integer> blockedProfiles) {
        this.flaggedProfiles = flaggedProfiles;
        this.blockedProfiles = blockedProfiles;
    }

    public void addToFlaggedQueue(String sourceProfileId, Integer count) throws InterruptedException {
        Integer oldValue = flaggedProfiles.put(sourceProfileId, count);
        System.out.println("Added to Flagged Queue : " + sourceProfileId);
        if (oldValue != null) {
            if (oldValue >= 2) {
                blockedProfiles.put(sourceProfileId, count);
                flaggedProfiles.remove(sourceProfileId);
            } else {
                oldValue++;
                flaggedProfiles.put(sourceProfileId, oldValue);
            }
        }
    }

    public void addToBlockingQueue(String sourceProfileId, Integer count) throws InterruptedException {
        blockedProfiles.put(sourceProfileId, count);
    }

}
