package com.su.twitter_clone.config;

import com.su.twitter_clone.runnable.MessageRunnable;
import com.su.twitter_clone.model.WsSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

// TODO dunno if in right place
// how many to make and start
@Configuration
public class MessageRunnableConfiguration {

    @Value("${messagerunnable.count}")
    private int count;

    private final WsSession wsSession;
    private final SimpMessageSendingOperations messagingTemplate;

    public MessageRunnableConfiguration(WsSession wsSession, SimpMessageSendingOperations messagingTemplate) {
        this.wsSession = wsSession;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        for (int i = 0; i < count; i++) {
            Thread thread = new Thread(new MessageRunnable(wsSession.getBlockingQueue(), wsSession, messagingTemplate));
            thread.start();
        }
    }

}
