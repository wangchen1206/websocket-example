package com.cc.websocket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

import java.security.Principal;

/**
 * Description
 *
 * @author wangchen
 * @createDate 2020/07/30
 */
@Component
@Slf4j
public class WebSocketDecoratorFactory implements WebSocketHandlerDecoratorFactory {
    @Override
    public WebSocketHandler decorate(WebSocketHandler handler) {
        return new WebSocketHandlerDecorator(handler) {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                log.info("session 连接，sessionId :{}", session.getId());
                Principal principal = session.getPrincipal();
                if (principal != null) {
                    log.info("key = {} 存入", principal.getName());
                    SocketManager.add(principal.getName(), session);
                }else {
                    log.info("没有token,连接失败！");
                    session.close(CloseStatus.BAD_DATA);
                }
                super.afterConnectionEstablished(session);
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                log.info("session 退出连接，sessionID：{}", session.getId());
                Principal principal = session.getPrincipal();
                if (principal != null) {
                    //身份校验成功，移除session
                    SocketManager.remove(principal.getName());
                }
                super.afterConnectionClosed(session, closeStatus);
            }
        };
    }
}
