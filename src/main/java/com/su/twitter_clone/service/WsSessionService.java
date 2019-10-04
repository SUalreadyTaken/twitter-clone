package com.su.twitter_clone.service;

import com.su.twitter_clone.model.Notification;
import com.su.twitter_clone.model.WsSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WsSessionService {

    private static final Logger log = LoggerFactory.getLogger(WsSessionService.class);
    private final WsSession wsSession;

    public WsSessionService(WsSession wsSession) {
        this.wsSession = wsSession;
    }
//    private final SimpMessageSendingOperations messagingTemplate;
//
//    public WsSessionService(WsSession wsSession, SimpMessageSendingOperations messagingTemplate) {
//        this.wsSession = wsSession;
//        this.messagingTemplate = messagingTemplate;
//    }


    /**
     * Add destinationId and sessionId to current session map
     *
     * @param destinationId destinationId
     * @param sessionId     sessionId
     */
    public void addSession(int destinationId, String sessionId) {
        ConcurrentHashMap<Integer, String> currentSession = wsSession.getCurrentSession();
        if (!currentSession.containsKey(destinationId)) {
            log.info("adding >>  destination : " + destinationId + " | sessionId : " + sessionId);
            currentSession.put(destinationId, sessionId);
        }
    }

    /**
     * Remove subscriber from current session map
     *
     * @param sessionId sessionId
     */
    public void removeSession(String sessionId) {
        ConcurrentHashMap<Integer, String> currentSession = wsSession.getCurrentSession();
        for (Map.Entry<Integer, String> entry : currentSession.entrySet()) {
            if (entry.getValue().equals(sessionId)) {
                // synced just remove and break should be ok without iteration
                log.info("removing >> destination : " + entry.getKey() + " | sessionId : " + entry.getValue());
                currentSession.remove(entry.getKey());
                break;
            }
        }
    }

    //todo what if blocking queue is bound ?? atm unbound

    public void sendNewTweet(List<Integer> followerIds) {
        wsSession.getBlockingQueue().add(new Notification("new_tweet", followerIds));
    }

    public void sendFollow(int id) {
        wsSession.getBlockingQueue().add(new Notification("follow", Collections.singletonList(id)));
    }

    public void sendUnFollow(int id) {
        wsSession.getBlockingQueue().add(new Notification("unfollow", Collections.singletonList(id)));
    }
}
