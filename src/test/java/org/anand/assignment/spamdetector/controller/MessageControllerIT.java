package org.anand.assignment.spamdetector.controller;

import org.anand.assignment.spamdetector.model.Message;
import org.anand.assignment.spamdetector.queues.MessageBuilder;
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
                .post("/service/message");
    }

    @Test
    public void shouldAddProfileToFlaggedQueue() throws Exception {
        for (int i = 0; i < 55; i++) {
            Message message = MessageBuilder.aMessage().build();
            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(message)
                    .expect()
                    .statusCode(200)
                    .log().ifError()
                    .when()
                    .post("/service/message");
        }
    }
}
