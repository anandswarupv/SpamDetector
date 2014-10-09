package org.anand.assignment.spamdetector.controller;

import java.util.Set;

import org.anand.assignment.spamdetector.model.Message;
import org.anand.assignment.spamdetector.queues.MessageBuilder;
import org.junit.Assert;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

public class MessageControllerIT {

    private static final String MESSAGE = "/service/message";
    private static final String FLAG_USER = "/service/flag/";
    private static final String FLAGGED_PROFILES = "/service/flagged";
    private static final String BLOCKED_PROFILES = "/service/blocked";

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
                .post(MESSAGE);
    }

    @Test
    public void shouldAddProfileToFlaggedQueue() throws Exception {
        Message message = MessageBuilder.aMessage().build();
        for (int i = 0; i < 51; i++) {
            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(message)
                    .expect()
                    .statusCode(200)
                    .log().ifError()
                    .when()
                    .post(MESSAGE);
        }
        @SuppressWarnings("unchecked")
        Set<String> sourceProfiles = RestAssured.given()
                .contentType(ContentType.JSON)
                .expect()
                .statusCode(200)
                .log().ifError()
                .when()
                .get(FLAGGED_PROFILES)
                .as(Set.class);
        Assert.assertTrue(sourceProfiles.contains(message.getSourceProfileId()));
    }

    @Test
    public void shouldAddProfileToBlockedQueue() throws Exception {
        Message message = MessageBuilder.aMessage().build();
        for (int i = 0; i < 151; i++) {
            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(message)
                    .expect()
                    .statusCode(200)
                    .log().ifError()
                    .when()
                    .post(MESSAGE);
        }
        @SuppressWarnings("unchecked")
        Set<String> sourceProfiles = RestAssured.given()
                .contentType(ContentType.JSON)
                .expect()
                .statusCode(200)
                .log().ifError()
                .when()
                .get(BLOCKED_PROFILES)
                .as(Set.class);
        Assert.assertTrue(sourceProfiles.contains(message.getSourceProfileId()));
    }

    @Test
    public void shouldAddProfileToBlockedQueueForGettingFlaggedThreeTimes() throws Exception {
        String sourceProfileId = "112233";
        Message message = MessageBuilder.aMessage().withSourceProfileId(sourceProfileId).build();
        for (int i = 0; i < 51; i++) {
            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(message)
                    .expect()
                    .statusCode(200)
                    .log().ifError()
                    .when()
                    .post(MESSAGE);
        }
        // Flag Twice
        flagProfile(sourceProfileId);
        flagProfile(sourceProfileId);

        @SuppressWarnings("unchecked")
        Set<String> blockedProfiles = RestAssured.given()
                .contentType(ContentType.JSON)
                .expect()
                .statusCode(200)
                .log().ifError()
                .when()
                .get(BLOCKED_PROFILES)
                .as(Set.class);
        Assert.assertTrue(blockedProfiles.contains(sourceProfileId));
    }

    private void flagProfile(String sourceProfileId) {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .expect()
                .statusCode(200)
                .log().ifError()
                .when()
                .post(FLAG_USER + sourceProfileId);
    }

    @Test
    public void shouldNotAddProfileToBlockedQueueJustFlagTwice() throws Exception {
        String sourceProfileId = "998877";
        Message message = MessageBuilder.aMessage().withSourceProfileId(sourceProfileId).build();
        for (int i = 0; i < 101; i++) {
            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(message)
                    .expect()
                    .statusCode(200)
                    .log().ifError()
                    .when()
                    .post(MESSAGE);
        }
        @SuppressWarnings("unchecked")
        Set<String> blockedProfiles = RestAssured.given()
                .contentType(ContentType.JSON)
                .expect()
                .statusCode(200)
                .log().ifError()
                .when()
                .get(BLOCKED_PROFILES)
                .as(Set.class);
        Assert.assertFalse(blockedProfiles.contains(sourceProfileId));
        @SuppressWarnings("unchecked")
        Set<String> flaggedProfiles = RestAssured.given()
                .contentType(ContentType.JSON)
                .expect()
                .statusCode(200)
                .log().ifError()
                .when()
                .get(FLAGGED_PROFILES)
                .as(Set.class);
        Assert.assertTrue(flaggedProfiles.contains(sourceProfileId));
    }

}
