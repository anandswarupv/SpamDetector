package org.anand.assignment.spamdetector.service;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.anand.assignment.spamdetector.cache.MessageCountMapWithTimeBasedEviction;
import org.anand.assignment.spamdetector.model.Message;
import org.anand.assignment.spamdetector.queues.DataOnRedis;
import org.anand.assignment.spamdetector.utils.SystemProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Provides all the services and interacts with the data on Redis and RabbitMQ
 * 
 * @author anand
 *
 */
@Service
public class MessageService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);
    private ExecutorService executorService;
    private LinkedBlockingQueue<Message> rabbitMQWorkQueue;
    
    MessageCountMapWithTimeBasedEviction messageCountMapWithTimeBasedEviction;
    DataOnRedis dataOnRedis;

    /**
     * @param messageCountMapWithTimeBasedEviction
     * @param dataOnRedis
     */
    @Autowired
    public MessageService(MessageCountMapWithTimeBasedEviction messageCountMapWithTimeBasedEviction,
            DataOnRedis dataOnRedis) {
        this.messageCountMapWithTimeBasedEviction = messageCountMapWithTimeBasedEviction;
        this.dataOnRedis = dataOnRedis;
        rabbitMQWorkQueue = new LinkedBlockingQueue<Message>();
    }

    /**
     * Instantiate and start the workers with the Queue
     * 
     * @throws Exception
     */
    @PostConstruct
    public void init() throws Exception {
        executorService = Executors.newFixedThreadPool(SystemProperties.NUMBER_OF_WORKERS);
        for (int i = 0; i < SystemProperties.NUMBER_OF_WORKERS; i++) {
            executorService.execute(new Worker(rabbitMQWorkQueue, messageCountMapWithTimeBasedEviction));
        }
    }

    /**
     * Shutdown the Executor Service
     * 
     */
    @PreDestroy
    public void cleanUp() {
        executorService.shutdown();
    }

    /**
     * The message will be added to the Spam Detection Queue
     * 
     * @param message
     * @return
     * @throws InterruptedException
     */
    public boolean addMessageToSpamDetectionQueue(Message message) {
        LOGGER.debug("Message added to Delivery Queue");
        try {
            rabbitMQWorkQueue.put(message);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Gives a {@link Set} of all the flagged profiles
     * 
     * @return
     */
    public boolean flagProfile(String sourceProfileId) {
        try {
            dataOnRedis.addToFlaggedQueue(sourceProfileId, 1);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Gives a {@link Set} of all the flagged profiles
     * 
     * @return
     */
    public Set<String> getFlaggedProfiles() {
        Set<String> flaggedProfiles = dataOnRedis.getFlaggedProfiles();
        return flaggedProfiles;
    }

    /**
     * Gives a {@link Set} of all the blocked profiles
     * 
     * @return
     */
    public Set<String> getBlockedProfiles() {
        Set<String> blockedProfiles = dataOnRedis.getBlockedProfiles();
        return blockedProfiles;
    }

}
