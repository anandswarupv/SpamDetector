package org.anand.assignment.spamdetector.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.anand.assignment.spamdetector.cache.MessageCountMapWithTimeBasedEviction;
import org.anand.assignment.spamdetector.model.Message;
import org.anand.assignment.spamdetector.queues.DataOnRedis;
import org.anand.assignment.spamdetector.queues.MessageBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class MessageServiceTest {

    MessageCountMapWithTimeBasedEviction messageCountMapWithTimeBasedEviction = Mockito.mock(MessageCountMapWithTimeBasedEviction.class);
    DataOnRedis dataOnRedis = Mockito.mock(DataOnRedis.class);
    MessageService messageService;

    @Before
    public void setUp() throws Exception {
        messageService = new MessageService(messageCountMapWithTimeBasedEviction, dataOnRedis);
    }

    @Test
    public void shouldAddMessageToSpamDetectionQueue() throws Exception {
        Message message = MessageBuilder.aMessage().build();
        boolean confirmation = messageService.addMessageToSpamDetectionQueue(message);
        Assert.assertTrue(confirmation);
    }

    @Test
    public void shouldAddProfileToFlaggedQueue() throws Exception {
        String sourceProfileId = "123";
        messageService.flagProfile(sourceProfileId);
        Mockito.verify(dataOnRedis).addToFlaggedQueue(sourceProfileId, 1);

    }

    @Test
    public void shouldGetFlaggedProfile() throws Exception {
        Set<String> flaggedProfiles = new HashSet<String>(Arrays.asList("1", "2", "3"));
        Mockito.when(dataOnRedis.getFlaggedProfiles()).thenReturn(flaggedProfiles);
        Set<String> flaggedProfilesFromService = messageService.getFlaggedProfiles();
        Assert.assertEquals(flaggedProfiles, flaggedProfilesFromService);
    }

    @Test
    public void shouldGetBlockedProfile() throws Exception {
        Set<String> blockedProfiles = new HashSet<String>(Arrays.asList("1", "2", "3"));
        Mockito.when(dataOnRedis.getBlockedProfiles()).thenReturn(blockedProfiles);
        Set<String> blockedProfilesFromService = messageService.getBlockedProfiles();
        Assert.assertEquals(blockedProfiles, blockedProfilesFromService);
    }
}
