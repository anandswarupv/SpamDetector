package org.anand.assignment.spamdetector.utils;

import java.sql.Timestamp;

import org.anand.assignment.spamdetector.model.Message;

public class MessageBuilder {

    private static final String default_sourceProfileId = "35603735";
    private static final String default_targetProfileId = "36872220";
    private static final String default_sourceClientId = "undefined";
    private static final String default_messageId = "5EFFB930-4B28-4B80-861B-760787188D29";
    private static final String default_type = "text";
    private static final Timestamp default_timestamp = new Timestamp(1406047430609L);
    private static final String default_body = "Hello Ben!";

    private String sourceProfileId = default_sourceProfileId;
    private String targetProfileId = default_targetProfileId;
    private String sourceClientId = default_sourceClientId;
    private String messageId = default_messageId;
    private String type = default_type;
    private Timestamp timestamp = default_timestamp;
    private String body = default_body;

    private MessageBuilder() {
    }

    public static MessageBuilder aMessage() {
        return new MessageBuilder();
    }

    public MessageBuilder withSourceProfileId(String sourceProfileId) {
        this.sourceProfileId = sourceProfileId;
        return this;
    }

    public Message build() {
        Message message = new Message();
        message.setSourceProfileId(sourceProfileId);
        message.setTargetProfileId(targetProfileId);
        message.setSourceClientId(sourceClientId);
        message.setMessageId(messageId);
        message.setType(type);
        message.setTimestamp(timestamp);
        message.setBody(body);
        return message;
    }

}
