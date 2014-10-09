package org.anand.assignment.spamdetector.queues;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.anand.assignment.spamdetector.queues.Queues;
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
@PrepareForTest({ Queues.class, ConcurrentHashMap.class })
public class QueuesTest {

    private static final String sourceProfileId = "35603735";
    private static final Integer count = 1;
    private ConcurrentMap<String, Integer> flaggedProfiles;
    private ConcurrentMap<String, Integer> blockedProfiles;

    Queues queues;

    @Before
    public void setUp() throws Exception {
        flaggedProfiles = new ConcurrentHashMap<String, Integer>();
        blockedProfiles = new ConcurrentHashMap<String, Integer>();
        queues = new Queues(flaggedProfiles, blockedProfiles);
    }

    @Test
    public void shouldGetAddedToFlaggedQueue() throws Exception {
        queues.addToFlaggedQueue(sourceProfileId, count);
        Assert.assertThat(flaggedProfiles.get(sourceProfileId), Matchers.equalTo(1));
    }

    @Test
    public void shouldGetAddedToBlockedQueue() throws Exception {
        queues.addToBlockingQueue(sourceProfileId, count);
        Assert.assertThat(blockedProfiles.get(sourceProfileId), Matchers.equalTo(1));
    }

    @Test
    public void shouldGetAddedToFlaggedQueueAndCountShouldBeUpdated() throws Exception {
        queues.addToFlaggedQueue(sourceProfileId, count);
        queues.addToFlaggedQueue(sourceProfileId, count);
        Assert.assertThat(flaggedProfiles.get(sourceProfileId), Matchers.equalTo(2));
    }

    @Test
    public void shouldGetMovedToBlockedQueueForBeingFlaggedThreeTimes() throws Exception {
        queues.addToFlaggedQueue(sourceProfileId, count);
        queues.addToFlaggedQueue(sourceProfileId, count);
        queues.addToFlaggedQueue(sourceProfileId, count);
        Assert.assertThat(blockedProfiles.get(sourceProfileId), Matchers.equalTo(1));
        Assert.assertThat(flaggedProfiles.get(sourceProfileId), Matchers.equalTo(null));
    }

    @Test
    public void shouldGetAddedToDefaultFlaggedQueue() throws Exception {
        @SuppressWarnings("unchecked")
        ConcurrentHashMap<String, Integer> flaggedProfiles = Mockito.mock(ConcurrentHashMap.class);
        PowerMockito.whenNew(ConcurrentHashMap.class).withNoArguments().thenReturn(flaggedProfiles);
        Queues queues = new Queues();
        queues.addToFlaggedQueue(sourceProfileId, count);
        Mockito.verify(flaggedProfiles).put(sourceProfileId, count);
    }

    @Test
    public void shouldGetAddedToDefaultBlockedQueue() throws Exception {
        @SuppressWarnings("unchecked")
        ConcurrentHashMap<String, Integer> blockedProfiles = Mockito.mock(ConcurrentHashMap.class);
        PowerMockito.whenNew(ConcurrentHashMap.class).withNoArguments().thenReturn(blockedProfiles);
        Queues queues = new Queues();
        queues.addToBlockingQueue(sourceProfileId, count);
        Mockito.verify(blockedProfiles).put(sourceProfileId, count);
    }

}
