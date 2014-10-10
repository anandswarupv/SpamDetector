package org.anand.assignment.spamdetector.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.anand.assignment.spamdetector.cache.MessageCountMapWithTimeBasedEviction;
import org.anand.assignment.spamdetector.model.Message;
import org.anand.assignment.spamdetector.queues.DataOnRedis;
import org.anand.assignment.spamdetector.service.MessageService;
import org.anand.assignment.spamdetector.utils.SystemProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class MessageControllerTest {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    private final static String SAMPLE_MESSAGE = "{\"sourceProfileId\": \"35603735\","
            + "\"targetProfileId\": \"36872220\",\"sourceClientId\": \"undefined\",\"messageId\": "
            + "\"5EFFB930-4B28-4B80-861B-760787188D29\",\"type\": \"text\","
            + "\"timestamp\": 1406047430609,\"body\": \"Hello Anand!\"}";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private static MessageService messageService = Mockito.mock(MessageService.class);

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Mockito.reset(messageService);
    }

    @Test
    public void shouldGetAMessage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(SystemProperties.DUMMY_MESSAGE)
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldPOSTAMessage() throws Exception {
        Mockito.when(messageService.addMessageToSpamDetectionQueue(Mockito.any(Message.class))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post(SystemProperties.MESSAGE)
                        .contentType(APPLICATION_JSON_UTF8)
                .content(SAMPLE_MESSAGE))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void shouldFlagAUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(SystemProperties.FLAG_USER + 35603735)
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

    }

    @Test
    public void shouldFlagAUserAndAddToQueue() throws Exception {
        String sourceProfileId = "35603735";
        mockMvc.perform(MockMvcRequestBuilders.post(SystemProperties.FLAG_USER + sourceProfileId)
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
        Mockito.verify(messageService).flagProfile(sourceProfileId);
    }

    @Test
    public void shouldGetFlaggedProfiles() throws Exception {
        Set<String> flaggedProfiles = new HashSet<String>(Arrays.asList("1", "2", "3"));
        Mockito.when(messageService.getFlaggedProfiles()).thenReturn(flaggedProfiles);
        mockMvc.perform(MockMvcRequestBuilders.get(SystemProperties.FLAGGED_PROFILES)
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().string("[\"3\",\"2\",\"1\"]"));
    }

    @Test
    public void shouldGetBlockedProfiles() throws Exception {
        Set<String> blockedProfiles = new HashSet<String>(Arrays.asList("1", "2", "3"));
        Mockito.when(messageService.getBlockedProfiles()).thenReturn(blockedProfiles);
        mockMvc.perform(MockMvcRequestBuilders.get(SystemProperties.BLOCKED_PROFILES)
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().string("[\"3\",\"2\",\"1\"]"));
    }
    
    @Configuration
    @EnableWebMvc
    public static class TestConfiguration {

        @Bean
        public MessageController messagePostingController() {
            MessageController messagePostingController = new MessageController();
            return messagePostingController;
        }

        @Bean
        public MessageService messageService() throws InterruptedException {
            return messageService;
        }

        @Bean
        public MessageCountMapWithTimeBasedEviction messageCountMapWithTimeBasedEviction() {
            MessageCountMapWithTimeBasedEviction messageCountMapWithTimeBasedEviction = new MessageCountMapWithTimeBasedEviction();
            return messageCountMapWithTimeBasedEviction;
        }

        @Bean
        public DataOnRedis dataOnRedis() {
            DataOnRedis dataOnRedis = new DataOnRedis();
            return dataOnRedis;
        }

    }

}
