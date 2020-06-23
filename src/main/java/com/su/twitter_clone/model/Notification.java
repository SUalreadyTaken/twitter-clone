package com.su.twitter_clone.model;

import java.util.List;

public class Notification {

    private String message;
    private List<Integer> destinationIds;

    public Notification(String message, List<Integer> destinationIds) {
        this.message = message;
        this.destinationIds = destinationIds;
    }

    public List<Integer> getDestinationIds() {
        return destinationIds;
    }

    public void setDestinationIds(List<Integer> destinationIds) {
        this.destinationIds = destinationIds;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
