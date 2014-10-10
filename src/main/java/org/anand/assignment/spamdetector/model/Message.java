package org.anand.assignment.spamdetector.model;

import java.sql.Timestamp;

/**
 * Represents the message
 * 
 * @author anand
 *
 */
public class Message {

    private String sourceProfileId;
    private String targetProfileId;
    private String sourceClientId;
    private String messageId;
    private String type;
    private Timestamp timestamp;
    private String body;
    /**
     * @return the sourceProfileId
     */
    public String getSourceProfileId() {
        return sourceProfileId;
    }
    
    /**
     * @param sourceProfileId
     *            the sourceProfileId to set
     */
    public void setSourceProfileId(String sourceProfileId) {
        this.sourceProfileId = sourceProfileId;
    }

    /**
     * @return the targetProfileId
     */
    public String getTargetProfileId() {
        return targetProfileId;
    }
    
    /**
     * @param targetProfileId
     *            the targetProfileId to set
     */
    public void setTargetProfileId(String targetProfileId) {
        this.targetProfileId = targetProfileId;
    }
    
    /**
     * @return the sourceClientId
     */
    public String getSourceClientId() {
        return sourceClientId;
    }
    
    /**
     * @param sourceClientId
     *            the sourceClientId to set
     */
    public void setSourceClientId(String sourceClientId) {
        this.sourceClientId = sourceClientId;
    }
    
    /**
     * @return the messageId
     */
    public String getMessageId() {
        return messageId;
    }
    
    /**
     * @param messageId
     *            the messageId to set
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }
    
    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * @return the timestamp
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }
    
    /**
     * @param timestamp
     *            the timestamp to set
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }
    
    /**
     * @param body
     *            the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }
    
    /**
     * @return the serialversionuid
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    private static final long serialVersionUID = 8870346743976422215L;

}
