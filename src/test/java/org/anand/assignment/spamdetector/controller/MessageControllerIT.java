package org.anand.assignment.spamdetector.controller;

import java.util.Set;

import org.anand.assignment.spamdetector.model.Message;
import org.anand.assignment.spamdetector.queues.MessageBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

public class MessageControllerIT {

    @Before
    public void setup() {

    }

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
                .post("/service/message");
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
                    .post("/service/message");
        }
        @SuppressWarnings("unchecked")
        Set<String> sourceProfiles = RestAssured.given()
                .contentType(ContentType.JSON)
                .expect()
                .statusCode(200)
                .log().ifError()
                .when()
                .get("/service/flagged")
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
                    .post("/service/message");
        }
        @SuppressWarnings("unchecked")
        Set<String> sourceProfiles = RestAssured.given()
                .contentType(ContentType.JSON)
                .expect()
                .statusCode(200)
                .log().ifError()
                .when()
                .get("/service/blocked")
                .as(Set.class);
        Assert.assertTrue(sourceProfiles.contains(message.getSourceProfileId()));
    }

    @Test
    public void shouldNotAddProfileToBlockedQueueJustFlagTwice() throws Exception {
        Message message = MessageBuilder.aMessage().withSourceProfileId("998877").build();
        for (int i = 0; i < 101; i++) {
            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(message)
                    .expect()
                    .statusCode(200)
                    .log().ifError()
                    .when()
                    .post("/service/message");
        }
        @SuppressWarnings("unchecked")
        Set<String> blockedProfiles = RestAssured.given()
                .contentType(ContentType.JSON)
                .expect()
                .statusCode(200)
                .log().ifError()
                .when()
                .get("/service/blocked")
                .as(Set.class);
        Assert.assertFalse(blockedProfiles.contains("998877"));
        @SuppressWarnings("unchecked")
        Set<String> flaggedProfiles = RestAssured.given()
                .contentType(ContentType.JSON)
                .expect()
                .statusCode(200)
                .log().ifError()
                .when()
                .get("/service/flagged")
                .as(Set.class);
        Assert.assertTrue(flaggedProfiles.contains("998877"));
    }

}
