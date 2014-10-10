package org.anand.assignment.spamdetector.service;

import java.util.concurrent.LinkedBlockingQueue;

import org.anand.assignment.spamdetector.cache.MessageCountMapWithTimeBasedEviction;
import org.anand.assignment.spamdetector.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Workers that take and process the messages from the Queue
 * 
 * @author anand
 *
 */
public class Worker implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Worker.class);
    private final MessageCountMapWithTimeBasedEviction messageCountMapWithTimeBasedEviction;
    private final LinkedBlockingQueue<Message> queue;

    /**
     * Contructs the worker using the specified {@link LinkedBlockingQueue} and
     * {@link MessageCountMapWithTimeBasedEviction}
     * 
     * @param queue
     * @param messageCountMapWithTimeBasedEviction
     */
    public Worker(LinkedBlockingQueue<Message> queue,
            MessageCountMapWithTimeBasedEviction messageCountMapWithTimeBasedEviction) {
        this.queue = queue;
        this.messageCountMapWithTimeBasedEviction = messageCountMapWithTimeBasedEviction;
    }

    public void run() {
        while (true) {
            Message message;
            try {
                message = queue.take();
                messageCountMapWithTimeBasedEviction.put(message.getSourceProfileId(), 1);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

}