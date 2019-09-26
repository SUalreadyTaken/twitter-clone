package com.su.twitter_clone.service;

import com.su.twitter_clone.model.Notification;
import com.su.twitter_clone.model.WsSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class WsSessionService {

    private static final Logger log = LoggerFactory.getLogger(WsSessionService.class);
    private final WsSession wsSession;
    private final SimpMessageSendingOperations messagingTemplate;

    public WsSessionService(WsSession wsSession, SimpMessageSendingOperations messagingTemplate) {
        this.wsSession = wsSession;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Add destinationId and sessionId to current session map
     * @param destinationId destinationId
     * @param sessionId sessionId
     */
    public void addSession(int destinationId, String sessionId) {
        Map<Integer, String> currentSession = wsSession.getCurrentSession();
        synchronized (wsSession.getCurrentSession()) {
            if (!currentSession.containsKey(destinationId)) {
                log.info("adding >>  destination : " + destinationId + " | sessionId : " + sessionId);
                currentSession.put(destinationId, sessionId);
            }
        }
    }

    /**
     * Remove subscriber from current session map
     * @param sessionId sessionId
     */
    public void removeSession(String sessionId) {
        Map<Integer, String> currentSession = wsSession.getCurrentSession();
        synchronized (wsSession.getCurrentSession()) {
            for (Map.Entry<Integer, String> entry : currentSession.entrySet()) {
                if (entry.getValue().equals(sessionId)) {
                    // synced just remove and break should be ok without iteration
                    log.info("removing >> " + entry.getValue());
                    currentSession.remove(entry.getKey());
                    break;
                }
            }
        }
    }

    /**
     * Send message of new_tweet to followers who have subscribed to websocket
     * @param followerIds follower ids of new tweet owner
     */
    public void sendNewTweetMessage(List<Integer> followerIds) {
        // todo fix .. what if i have millions of followers.. too expensive
        Map<Integer, String> currentSession = wsSession.getCurrentSession();
        synchronized (wsSession.getCurrentSession()) {
            for (int destinationId : currentSession.keySet()) {
                if (followerIds.contains(destinationId)) {
                    log.info("Sending new_tweet message to > " + destinationId);
                    messagingTemplate.convertAndSend("/notification/" + destinationId, new Notification("new_tweet", true));
                }
            }
        }
    }

    // todo send message on new follower

    // todo send message on un follow


}
