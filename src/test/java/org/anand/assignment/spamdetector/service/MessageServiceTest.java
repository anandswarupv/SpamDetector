package org.anand.assignment.spamdetector.service;

import org.anand.assignment.spamdetector.queues.MessageBuilder;
import org.junit.Before;
import org.junit.Test;

public class MessageServiceTest {

    MessageService messageService;

    @Before
    public void setUp() throws Exception {
        messageService = new MessageService();
    }

    @Test
    public void shouldAddToSPAMDetectionQueue() throws Exception {
        messageService.addMessageToSpamDetectionQueue(MessageBuilder.aMessage().build());
    }

}
