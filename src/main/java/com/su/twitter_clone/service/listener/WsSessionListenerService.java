package com.su.twitter_clone.service.listener;

import com.su.twitter_clone.service.WsSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Service
public class WsSessionListenerService {

    private static final Logger log = LoggerFactory.getLogger(WsSessionListenerService.class);
    private final WsSessionService wsSessionService;

    public WsSessionListenerService(WsSessionService wsSessionService) {
        this.wsSessionService = wsSessionService;
    }

    @EventListener(SessionDisconnectEvent.class)
    public void handleWebsocketDisconnectListener(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
//        log.info("disconnect : session id >> " + headers.getSessionId());
        this.wsSessionService.removeSession(headers.getSessionId());
    }

    @EventListener(SessionSubscribeEvent.class)
    public void handleWebsocketSubscribeListener(SessionSubscribeEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        if (headers.getDestination() != null) {
            int destinationId = Integer.parseInt(headers.getDestination().substring(headers.getDestination().lastIndexOf("/") + 1));
//            log.info("subscription  : destination >> " + destinationId + " | sessionId > " + headers.getSessionId());
            this.wsSessionService.addSession(destinationId, headers.getSessionId());
        }
    }

}
