package com.cc.websocket.handler;

import com.cc.websocket.message.SessionCloseRequest;
import com.cc.websocket.util.WebSocketUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

/**
 * <Description>
 *
 * @author wangchen
 * @createDate 2020/07/29
 */
@Component
public class SessionCloseHandler implements MessageHandler<SessionCloseRequest> {
    @Override
    public void execute(WebSocketSession session, SessionCloseRequest message) {
        WebSocketUtil.broadcast(SessionCloseRequest.TYPE,message);
    }

    @Override
    public String getType() {
        return SessionCloseRequest.TYPE;
    }
}
