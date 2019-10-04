package com.su.twitter_clone.runnable;

import com.su.twitter_clone.model.Notification;
import com.su.twitter_clone.model.WsSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.*;
import java.util.concurrent.BlockingQueue;

public class MessageRunnable implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(MessageRunnable.class);
    private BlockingQueue<Notification> queue;
    private final WsSession wsSession;
    private final SimpMessageSendingOperations messagingTemplate;

    public MessageRunnable(BlockingQueue<Notification> queue, WsSession wsSession, SimpMessageSendingOperations messagingTemplate) {
        this.queue = queue;
        this.wsSession = wsSession;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void run() {
        // todo ? break while ? it needs to run forever so ?
        while (true) {
            try {
                // switch to poll if multiple MessageRunnables..
                Notification notification = this.queue.take();
                switch (notification.getMessage()) {
                    case "new_tweet":
                        newTweetSendToPplOnline(new HashSet<>(notification.getDestinationIds()), notification.getMessage());
                        break;
                    case "follow":
                        if (wsSession.getCurrentSession().containsKey(notification.getDestinationIds().get(0))) {
                            sendMessage(notification.getDestinationIds(), notification.getMessage());
                        }
                        break;
                    case "unfollow":
                        if (wsSession.getCurrentSession().containsKey(notification.getDestinationIds().get(0))) {
                            sendMessage(notification.getDestinationIds(), notification.getMessage());
                        }
                        break;
                    default:
                        break;

                }
            } catch (InterruptedException e) {
//                e.printStackTrace();
                log.error(e.getMessage());
            }
        }
    }

    private void newTweetSendToPplOnline(Set<Integer> followerIds, String message) {
        Map<Integer, String> currentSession = wsSession.getCurrentSession();
        List<Integer> toSend = new ArrayList<>();
        if (currentSession.size() > followerIds.size()) {
            markToSendSessionBigger(toSend, currentSession, followerIds);
        } else {
            markToSendSessionSmaller(toSend, currentSession, followerIds);
        }
        if (!toSend.isEmpty()) {
            sendMessage(toSend, message);
        }
    }

    private void markToSendSessionSmaller(List<Integer> toSend, Map<Integer, String> currentSession, Set<Integer> followerIds) {
        for (Integer destinationId : currentSession.keySet()) {
            if (followerIds.contains(destinationId)) {
                toSend.add(destinationId);
            }
        }
    }

    private void markToSendSessionBigger(List<Integer> toSend, Map<Integer, String> currentSession, Set<Integer> followerIds) {
        for (Integer followerId : followerIds) {
            if (currentSession.containsKey(followerId)) {
                toSend.add(followerId);
            }
        }
    }

    private void sendMessage(List<Integer> toSend, String message) {
        for (Integer destinationId : toSend) {
            messagingTemplate.convertAndSend("/notification/" + destinationId, message);
        }
    }
}
