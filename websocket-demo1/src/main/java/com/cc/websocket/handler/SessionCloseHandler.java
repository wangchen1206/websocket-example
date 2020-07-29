package com.cc.websocket.handler;

import com.cc.websocket.message.SessionCloseRequest;
import com.cc.websocket.util.WebSocketUtil;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.UUID;

/**
 * <Description>
 *
 * @author wangchen
 * @createDate 2020/07/29
 */
@Component
public class SessionCloseHandler implements MessageHandler<SessionCloseRequest> {
    @Override
    public void execute(Session session, SessionCloseRequest message) {
        WebSocketUtil.broadcast(SessionCloseRequest.TYPE,message);
    }

    @Override
    public String getType() {
        return SessionCloseRequest.TYPE;
    }
}
