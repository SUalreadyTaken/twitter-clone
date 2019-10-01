package com.su.twitter_clone.service;

import com.su.twitter_clone.model.Notification;
import com.su.twitter_clone.model.WsSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.*;

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
     *
     * @param destinationId destinationId
     * @param sessionId     sessionId
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
     *
     * @param sessionId sessionId
     */
    public void removeSession(String sessionId) {
        Map<Integer, String> currentSession = wsSession.getCurrentSession();
        synchronized (wsSession.getCurrentSession()) {
            for (Map.Entry<Integer, String> entry : currentSession.entrySet()) {
                if (entry.getValue().equals(sessionId)) {
                    // synced just remove and break should be ok without iteration
                    log.info("removing >> destination : " + entry.getKey() + " | sessionId : " + entry.getValue());
                    currentSession.remove(entry.getKey());
                    break;
                }
            }
        }
    }

//    /**
//     * Send message of new_tweet to followers who have subscribed to websocket
//     * @param followerIds follower ids of new tweet owner
//     */
//    private void newTweetMessage(Set<Integer> followerIds) {
//        Map<Integer, String> currentSession = wsSession.getCurrentSession();
//        synchronized (wsSession.getCurrentSession()) {
//            for (int destinationId : currentSession.keySet()) {
//                if (followerIds.contains(destinationId)) {
//                    // todo ? just mark to send.. send in new thread.. frees currentSession map.. wont wait for convertAndSend.. mem vs
//                     perf
//                    log.info("Sending new_tweet message to > " + destinationId);
//                    messagingTemplate.convertAndSend("/notification/" + destinationId, new Notification("new_tweet", true));
//                }
//            }
//        }
//    }


    /**
     * Send message of new_tweet to followers who have subscribed to websocket
     *
     * @param followerIds follower ids of new tweet owner
     */
    private void newTweetMessage(Set<Integer> followerIds) {
        Map<Integer, String> currentSession = wsSession.getCurrentSession();
        List<Integer> toSend = new ArrayList<>();
        synchronized (wsSession.getCurrentSession()) {
            if (currentSession.size() > followerIds.size()) {
                sessionBiggerMarkForNewTweet(toSend, currentSession, followerIds);
            } else {
                sessionSmallerMarkForNewTweet(toSend, currentSession, followerIds);
            }
        }
        if (!toSend.isEmpty()) {
            new Thread(() -> {
                for (Integer destinationId : toSend) {
                    messagingTemplate.convertAndSend("/notification/" + destinationId, new Notification("new_tweet", true));
                }
            }).start();
        }
    }

    private void sessionSmallerMarkForNewTweet(List<Integer> toSend, Map<Integer, String> currentSession, Set<Integer> followerIds) {
        for (Integer destinationId : currentSession.keySet()) {
            if (followerIds.contains(destinationId)) {
                toSend.add(destinationId);
            }
        }
    }

    private void sessionBiggerMarkForNewTweet(List<Integer> toSend, Map<Integer, String> currentSession, Set<Integer> followerIds) {
        for (Integer followerId : followerIds) {
            if (currentSession.containsKey(followerId)) {
                toSend.add(followerId);
            }
        }
    }

    public void sendNewTweet(List<Integer> followerIds) {
        new Thread(() -> {
            Set<Integer> s = new HashSet<>(followerIds);
            newTweetMessage(s);
        }).start();
    }

    // todo send message on new follower

    // todo send message on un follow


}
