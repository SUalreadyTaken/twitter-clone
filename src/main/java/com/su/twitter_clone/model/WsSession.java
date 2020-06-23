package com.su.twitter_clone.model;

import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

@Component
public class WsSession {

//    private Map<Integer, String> currentSession = Collections.synchronizedMap(new LinkedHashMap<>());
    private ConcurrentHashMap<Integer, String> currentSession = new ConcurrentHashMap<>();
    private BlockingQueue<Notification> blockingQueue = new LinkedBlockingDeque<>();


    /**
     *
     * @return Map|DestinationId,SessionId|
     */
    public ConcurrentHashMap<Integer, String> getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(ConcurrentHashMap<Integer, String> currentSession) {
        this.currentSession = currentSession;
    }

    public BlockingQueue<Notification> getBlockingQueue() {
        return blockingQueue;
    }

    public void setBlockingQueue(BlockingQueue<Notification> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }
}
