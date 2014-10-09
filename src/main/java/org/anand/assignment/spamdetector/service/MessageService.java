package org.anand.assignment.spamdetector.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.anand.assignment.spamdetector.cache.MessageCountMapWithTimeBasedEviction;
import org.anand.assignment.spamdetector.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);
    private ExecutorService threadPool;
    private LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
    
    @Autowired
    MessageCountMapWithTimeBasedEviction messageCountMapWithTimeBasedEviction;

    @PostConstruct
    public void init() throws Exception {
        int nThreads = Runtime.getRuntime().availableProcessors() - 1;
        threadPool = Executors.newFixedThreadPool(nThreads);
        for (int i = 0; i < nThreads; i++) {
            threadPool.execute(new Worker(queue, messageCountMapWithTimeBasedEviction));
        }
    }

    @PreDestroy
    public void cleanUp() {
        threadPool.shutdown();
    }

    public boolean addMessageToSpamDetectionQueue(Message message) throws InterruptedException {
        LOGGER.info("Message added to Delivery Queue");
        queue.put(message);
        return true;
    }

}
