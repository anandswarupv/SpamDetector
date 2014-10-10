package org.anand.assignment.spamdetector.queues;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DataOnRedis.class, ConcurrentHashMap.class })
public class DataOnRedisTest {

    private static final String sourceProfileId = "35603735";
    private static final String anotherSourceProfileId = "35603736";
    private static final Integer count = 1;
    private ConcurrentMap<String, Integer> flaggedProfiles;
    private ConcurrentMap<String, Integer> blockedProfiles;

    DataOnRedis dataOnRedis;

    @Before
    public void setUp() throws Exception {
        flaggedProfiles = new ConcurrentHashMap<String, Integer>();
        blockedProfiles = new ConcurrentHashMap<String, Integer>();
        dataOnRedis = new DataOnRedis(flaggedProfiles, blockedProfiles);
    }

    @Test
    public void shouldGetAddedToFlaggedQueue() throws Exception {
        dataOnRedis.addToFlaggedQueue(sourceProfileId, count);
        Assert.assertThat(flaggedProfiles.get(sourceProfileId), Matchers.equalTo(1));
    }

    @Test
    public void shouldGetAddedToBlockedQueue() throws Exception {
        dataOnRedis.addToBlockingQueue(sourceProfileId, count);
        Assert.assertThat(blockedProfiles.get(sourceProfileId), Matchers.equalTo(1));
    }

    @Test
    public void shouldGetAddedToFlaggedQueueAndCountShouldBeUpdated() throws Exception {
        dataOnRedis.addToFlaggedQueue(sourceProfileId, count);
        dataOnRedis.addToFlaggedQueue(sourceProfileId, count);
        Assert.assertThat(flaggedProfiles.get(sourceProfileId), Matchers.equalTo(2));
    }

    @Test
    public void shouldGetAllFlaggedProfiles() throws Exception {
        dataOnRedis.addToFlaggedQueue(sourceProfileId, count);
        dataOnRedis.addToFlaggedQueue(anotherSourceProfileId, count);
        Set<String> flaggedProfiles = dataOnRedis.getFlaggedProfiles();
        Assert.assertThat(flaggedProfiles, Matchers.contains(sourceProfileId, anotherSourceProfileId));
    }

    @Test
    public void shouldGetAllBlockedProfiles() throws Exception {
        dataOnRedis.addToBlockingQueue(sourceProfileId, count);
        dataOnRedis.addToBlockingQueue(anotherSourceProfileId, count);
        Set<String> blockedProfiles = dataOnRedis.getBlockedProfiles();
        Assert.assertThat(blockedProfiles, Matchers.contains(sourceProfileId, anotherSourceProfileId));
    }

    @Test
    public void shouldGetMovedToBlockedQueueForBeingFlaggedThreeTimes() throws Exception {
        dataOnRedis.addToFlaggedQueue(sourceProfileId, count);
        dataOnRedis.addToFlaggedQueue(sourceProfileId, count);
        dataOnRedis.addToFlaggedQueue(sourceProfileId, count);
        Assert.assertThat(blockedProfiles.get(sourceProfileId), Matchers.equalTo(1));
        Assert.assertThat(flaggedProfiles.get(sourceProfileId), Matchers.equalTo(null));
    }

    @Test
    public void shouldGetAddedToDefaultFlaggedQueue() throws Exception {
        @SuppressWarnings("unchecked")
        ConcurrentHashMap<String, Integer> flaggedProfiles = Mockito.mock(ConcurrentHashMap.class);
        PowerMockito.whenNew(ConcurrentHashMap.class).withNoArguments().thenReturn(flaggedProfiles);
        DataOnRedis queues = new DataOnRedis();
        queues.addToFlaggedQueue(sourceProfileId, count);
        Mockito.verify(flaggedProfiles).put(sourceProfileId, count);
    }

    @Test
    public void shouldGetAddedToDefaultBlockedQueue() throws Exception {
        @SuppressWarnings("unchecked")
        ConcurrentHashMap<String, Integer> blockedProfiles = Mockito.mock(ConcurrentHashMap.class);
        PowerMockito.whenNew(ConcurrentHashMap.class).withNoArguments().thenReturn(blockedProfiles);
        DataOnRedis queues = new DataOnRedis();
        queues.addToBlockingQueue(sourceProfileId, count);
        Mockito.verify(blockedProfiles).put(sourceProfileId, count);
    }

}
