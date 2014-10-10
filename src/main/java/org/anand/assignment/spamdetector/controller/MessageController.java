package org.anand.assignment.spamdetector.controller;

import java.sql.Timestamp;
import java.util.Set;

import org.anand.assignment.spamdetector.model.Message;
import org.anand.assignment.spamdetector.service.MessageService;
import org.anand.assignment.spamdetector.utils.SystemProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Class exposes the REST services for messages
 * 
 * @author anand
 */
@Controller
public class MessageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    MessageService messageService;

    /**
     * Gives a Dummy Message
     * 
     * @return
     */
    @RequestMapping(value = SystemProperties.DUMMY_MESSAGE, method = RequestMethod.GET)
    public @ResponseBody Message dummyMessage() {
        LOGGER.debug("Received message : ");
        Message message = getADummyMessage();
        return message;
    }

    /**
     * Used to POST a message to the Spam Detection Queue
     * 
     * @param message
     * @return
     */
    @RequestMapping(value = SystemProperties.MESSAGE, method = RequestMethod.POST)
    public @ResponseBody boolean message(@RequestBody Message message) {
        LOGGER.debug("Received message from : " + message.getSourceProfileId());
        return messageService.addMessageToSpamDetectionQueue(message);
    }

    /**
     * Get Flagged Profiles
     * 
     * @return
     */
    @RequestMapping(value = SystemProperties.FLAGGED_PROFILES, method = RequestMethod.GET)
    public @ResponseBody Set<String> flaggedProfiles() {
        LOGGER.debug("Getting flagged Profiles");
        Set<String> flaggedProfiles = messageService.getFlaggedProfiles();
        return flaggedProfiles;
    }

    /**
     * Get Flagged Profiles
     * 
     * @return
     */
    @RequestMapping(value = SystemProperties.BLOCKED_PROFILES, method = RequestMethod.GET)
    public @ResponseBody Set<String> blockedProfiles() {
        LOGGER.debug("Getting Blocked Profiles");
        Set<String> blockedProfiles = messageService.getBlockedProfiles();
        return blockedProfiles;
    }

    /**
     * Flag a user for sending SPAM
     * 
     * @param message
     * @return
     */
    @RequestMapping(value = SystemProperties.FLAG_USER + "{sourceProfileId}", method = RequestMethod.POST)
    public @ResponseBody boolean flagSourceProfile(@PathVariable("sourceProfileId") String sourceProfileId) {
        LOGGER.debug("Flag User : " + sourceProfileId);
        messageService.flagProfile(sourceProfileId);
        return true;
    }

    private Message getADummyMessage() {
        Message message = new Message();
        message.setSourceProfileId("35603735");
        message.setTargetProfileId("36872220");
        message.setSourceClientId("undefined");
        message.setMessageId("5EFFB930-4B28-4B80-861B-760787188D29");
        message.setType("text");
        message.setTimestamp(new Timestamp(1406047430609L));
        message.setBody("Hello Anand!");
        return message;
    }

}
