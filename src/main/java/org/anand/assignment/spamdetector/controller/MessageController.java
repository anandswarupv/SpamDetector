package org.anand.assignment.spamdetector.controller;

import java.sql.Timestamp;
import java.util.List;

import org.anand.assignment.spamdetector.model.Message;
import org.anand.assignment.spamdetector.service.MessageService;
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
    private static final String MESSAGE = "/service/message";
    private static final String DUMMY_MESSAGE = "/service/dummyMessage";
    private static final String FLAG_USER = "/service/flag/";
    private static final String FLAGGED_PROFILES = "/service/flagged";
    private static final String BLOCKED_PROFILES = "/service/blocked";

    @Autowired
    MessageService messageService;

    /**
     * Gives a Dummy Message
     * 
     * @return
     */
    @RequestMapping(value = DUMMY_MESSAGE, method = RequestMethod.GET)
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
    @RequestMapping(value = MESSAGE, method = RequestMethod.POST)
    public @ResponseBody boolean message(@RequestBody Message message) {
        LOGGER.debug("Received message from : " + message.getSourceProfileId());
        try {
            messageService.addMessageToSpamDetectionQueue(message);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Get Flagged Profiles
     * 
     * @return
     */
    @RequestMapping(value = FLAGGED_PROFILES, method = RequestMethod.GET)
    public @ResponseBody List<String> flaggedProfiles() {
        LOGGER.debug("Getting flagged Profiles");
        List<String> flaggedProfiles = messageService.getFlaggedProfiles();
        return flaggedProfiles;
    }

    /**
     * Get Flagged Profiles
     * 
     * @return
     */
    @RequestMapping(value = BLOCKED_PROFILES, method = RequestMethod.GET)
    public @ResponseBody List<String> blockedProfiles() {
        LOGGER.debug("Getting Blocked Profiles");
        List<String> blockedProfiles = messageService.getBlockedProfiles();
        return blockedProfiles;
    }

    /**
     * Used to FLAG a user for sending SPAM
     * 
     * @param message
     * @return
     */
    @RequestMapping(value = FLAG_USER + "{sourceProfileId}", method = RequestMethod.POST)
    public @ResponseBody boolean flagSourceProfile(@PathVariable("sourceProfileId") String sourceProfileId) {
        LOGGER.debug("Flag User : " + sourceProfileId);
        // TODO
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
