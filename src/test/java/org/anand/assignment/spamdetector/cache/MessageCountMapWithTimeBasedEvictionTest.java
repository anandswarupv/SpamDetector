package org.anand.assignment.spamdetector.cache;

import org.anand.assignment.spamdetector.cache.MessageCountMapWithTimeBasedEviction;
import org.anand.assignment.spamdetector.queues.DataOnRedis;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class MessageCountMapWithTimeBasedEvictionTest {

    private static final String sourceProfileId = "35603735";
    private static final Integer count = 1;

    @Mock
    DataOnRedis dataOnRedis;

    @InjectMocks
    MessageCountMapWithTimeBasedEviction messageCountMap = new MessageCountMapWithTimeBasedEviction();

    @Before
    public void setUp() throws Exception {
        messageCountMap.remove(sourceProfileId);
    }

    @Test
    public void shouldGetFlaggedOnce() throws Exception {
        for (int i = 0; i < 51; i++) {
            messageCountMap.put(sourceProfileId, count);
        }
        Mockito.verify(dataOnRedis).addToFlaggedQueue(sourceProfileId, count);
    }

    @Test
    public void shouldNotGetFlagged() throws Exception {
        for (int i = 0; i < 50; i++) {
            messageCountMap.put(sourceProfileId, count);
        }
        waitForKeyTimeout();
        messageCountMap.put(sourceProfileId, count);
        Mockito.verifyZeroInteractions(dataOnRedis);
    }

    @Test
    public void shouldGetFlaggedTwice() throws Exception {
        for (int i = 0; i < 101; i++) {
            messageCountMap.put(sourceProfileId, count);
        }
        Mockito.verify(dataOnRedis, Mockito.times(2)).addToFlaggedQueue(sourceProfileId, count);
    }

    @Test
    public void shouldResetMessageCountToTwo() throws Exception {
        for (int i = 0; i < 152; i++) {
            messageCountMap.put(sourceProfileId, count);
        }
        Integer messageCountAfterBeingFlaggedTwice = messageCountMap.get(sourceProfileId);
        Assert.assertEquals((Integer) 2, messageCountAfterBeingFlaggedTwice);
    }

    private void waitForKeyTimeout() throws InterruptedException {
        Thread.sleep(10000);
    }

}
