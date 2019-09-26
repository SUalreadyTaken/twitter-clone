package com.su.twitter_clone.model;

public class Notification {

    private String notification;
    private boolean value;

    public Notification(String notification, boolean value) {
        this.notification = notification;
        this.value = value;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
