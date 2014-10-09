package org.anand.assignment.spamdetector.service;

import java.util.concurrent.LinkedBlockingQueue;

import org.anand.assignment.spamdetector.cache.MessageCountMapWithTimeBasedEviction;
import org.anand.assignment.spamdetector.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Worker implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Worker.class);
    private final MessageCountMapWithTimeBasedEviction messageCountMapWithTimeBasedEviction;
    private final LinkedBlockingQueue<Message> queue;

    public Worker(LinkedBlockingQueue<Message> queue,
            MessageCountMapWithTimeBasedEviction messageCountMapWithTimeBasedEviction) {
        this.queue = queue;
        this.messageCountMapWithTimeBasedEviction = messageCountMapWithTimeBasedEviction;
    }

    public void run() {
        System.out.println("Worker Started");
        while (true) {
            Message message;
            try {
                message = queue.take();
                messageCountMapWithTimeBasedEviction.put(message.getSourceProfileId(), 1);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }

}