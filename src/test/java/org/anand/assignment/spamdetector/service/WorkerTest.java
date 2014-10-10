package org.anand.assignment.spamdetector.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.anand.assignment.spamdetector.cache.MessageCountMapWithTimeBasedEviction;
import org.anand.assignment.spamdetector.model.Message;
import org.anand.assignment.spamdetector.utils.MessageBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WorkerTest {

    private static final int SOME_WAIT_FOR_THE_WORKER_THREAD = 2000;
    private final MessageCountMapWithTimeBasedEviction messageCountMapWithTimeBasedEviction = new MessageCountMapWithTimeBasedEviction();
    private final LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Before
    public void setUp() throws Exception {
        executorService.execute(new Worker(queue, messageCountMapWithTimeBasedEviction));
    }

    @Test
    public void shouldPickMessageFromQueue() throws Exception {
        String sourceProfileId = "123123";
        queue.put(MessageBuilder.aMessage().withSourceProfileId(sourceProfileId).build());
        Assert.assertEquals(1, queue.size());
    }

    @Test
    public void shouldPickMessageFromQueueAndPutOnMessageCountMap() throws Exception {
        String sourceProfileId = "123123";
        queue.put(MessageBuilder.aMessage().withSourceProfileId(sourceProfileId).build());
        Assert.assertEquals(1, queue.size());
        Thread.sleep(SOME_WAIT_FOR_THE_WORKER_THREAD);
        Assert.assertEquals((Integer) 1, messageCountMapWithTimeBasedEviction.get(sourceProfileId));
    }

    @Test
    public void shouldPickMessageFromQueueAndUpdateCountMapIfKeyAlreadExists() throws Exception {
        String sourceProfileId = "123123";
        queue.put(MessageBuilder.aMessage().withSourceProfileId(sourceProfileId).build());
        queue.put(MessageBuilder.aMessage().withSourceProfileId(sourceProfileId).build());
        Thread.sleep(SOME_WAIT_FOR_THE_WORKER_THREAD);
        Assert.assertEquals((Integer) 2, messageCountMapWithTimeBasedEviction.get(sourceProfileId));
    }

}
