package com.su.twitter_clone.model;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class WsSession {

    private Map<Integer, String> currentSession = Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     *
     * @return Map|DestinationId,SessionId|
     */
    public Map<Integer, String> getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(Map<Integer, String> currentSession) {
        this.currentSession = currentSession;
    }
}
