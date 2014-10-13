package org.anand.assignment.spamdetector.controller;

import java.util.Set;

import org.anand.assignment.spamdetector.model.Message;
import org.anand.assignment.spamdetector.utils.MessageBuilder;
import org.anand.assignment.spamdetector.utils.SystemPropertiesForTests;
import org.junit.Assert;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

public class MessageControllerIT {

    @Test
    public void shouldAddMessageToQueue() throws Exception {
        Message message = MessageBuilder.aMessage().build();
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(message)
                .log().everything()
                .expect()
                .statusCode(200)
                .log().ifError()
                .when()
                .post(SystemPropertiesForTests.MESSAGE);
    }

    @Test
    public void shouldAddProfileToFlaggedQueueForPostingMoreThan50MessagesIn10Secs() throws Exception {
        Message message = MessageBuilder.aMessage().build();
        int numbersOfMessages = 51;
        postMessages(message, numbersOfMessages);
        Set<String> sourceProfiles = getFlaggedProfiles();
        Assert.assertTrue(sourceProfiles.contains(message.getSourceProfileId()));
    }

    @Test
    public void shouldNotAddProfileToFlaggedQueue() throws Exception {
        String sourceProfileId = "9999";
        int numberOfMessages = 50;
        Message message = MessageBuilder.aMessage().withSourceProfileId(sourceProfileId).build();
        postMessages(message, numberOfMessages);
        Set<String> sourceProfiles = getFlaggedProfiles();
        Assert.assertFalse(sourceProfiles.contains(message.getSourceProfileId()));
    }

    @Test
    public void shouldAddProfileToBlockedQueueForSendingMoreThan50MessagesIn10SecThreeTimes() throws Exception {
        Message message = MessageBuilder.aMessage().build();
        int numberOfMessages = 151;
        postMessages(message, numberOfMessages);
        Set<String> sourceProfiles = getBlockedProfiles();
        Assert.assertTrue(sourceProfiles.contains(message.getSourceProfileId()));
    }

    @Test
    public void shouldNotAddProfileToBlockedQueue() throws Exception {
        String sourceProfileId = "88888";
        Message message = MessageBuilder.aMessage().withSourceProfileId(sourceProfileId).build();
        int numberOfMessages = 101;
        postMessages(message, numberOfMessages);
        Set<String> sourceProfiles = getBlockedProfiles();
        Assert.assertFalse(sourceProfiles.contains(message.getSourceProfileId()));
    }

    @Test
    public void shouldNotFlagTheProfile() throws Exception {
        String sourceProfileId = "223344";
        Message message = MessageBuilder.aMessage().withSourceProfileId(sourceProfileId).build();
        int numberOfMessages = 49;
        postMessages(message, numberOfMessages);
        Thread.sleep(11000);
        postMessages(message, numberOfMessages);
        Set<String> sourceProfiles = getFlaggedProfiles();
        Assert.assertFalse(sourceProfiles.contains(message.getSourceProfileId()));
    }

    @Test
    public void shouldAddProfileToBlockedQueueForGettingFlaggedThreeTimes() throws Exception {
        String sourceProfileId = "112233";
        int numberOfMessages = 51;
        Message message = MessageBuilder.aMessage().withSourceProfileId(sourceProfileId).build();
        postMessages(message, numberOfMessages);
        // Flag Twice
        flagProfile(sourceProfileId);
        flagProfile(sourceProfileId);

        Set<String> blockedProfiles = getBlockedProfiles();
        Assert.assertTrue(blockedProfiles.contains(sourceProfileId));
        Set<String> flaggedProfiles = getFlaggedProfiles();
        Assert.assertFalse(flaggedProfiles.contains(sourceProfileId));
    }

    @Test
    public void shouldNotAddProfileToBlockedQueueJustFlagTwice() throws Exception {
        String sourceProfileId = "998877";
        int numberOfMessages = 101;
        Message message = MessageBuilder.aMessage().withSourceProfileId(sourceProfileId).build();
        postMessages(message, numberOfMessages);
        Set<String> blockedProfiles = getBlockedProfiles();
        Set<String> flaggedProfiles = getFlaggedProfiles();

        Assert.assertFalse(blockedProfiles.contains(sourceProfileId));
        Assert.assertTrue(flaggedProfiles.contains(sourceProfileId));
    }

    private void flagProfile(String sourceProfileId) {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .expect()
                .statusCode(200)
                .log().ifError()
                .when()
                .post(SystemPropertiesForTests.FLAG_USER + sourceProfileId);
    }

    private void postMessage(Message message) {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(message)
                .expect()
                .statusCode(200)
                .log().ifError()
                .when()
                .post(SystemPropertiesForTests.MESSAGE);
    }

    @SuppressWarnings("unchecked")
    private Set<String> getFlaggedProfiles() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .expect()
                .statusCode(200)
                .log().ifError()
                .when()
                .get(SystemPropertiesForTests.FLAGGED_PROFILES)
                .as(Set.class);
    }

    @SuppressWarnings("unchecked")
    private Set<String> getBlockedProfiles() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .expect()
                .statusCode(200)
                .log().ifError()
                .when()
                .get(SystemPropertiesForTests.BLOCKED_PROFILES)
                .as(Set.class);
    }

    private void postMessages(Message message, int numberOfMessages) {
        for (int i = 0; i < numberOfMessages; i++) {
            postMessage(message);
        }
    }
}

