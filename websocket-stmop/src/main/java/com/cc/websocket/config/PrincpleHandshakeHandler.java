package com.cc.websocket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

/**
 * Description
 *
 * @author wangchen
 * @createDate 2020/07/30
 */
@Slf4j
@Component
public class PrincpleHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        //是否携带认证 token
        if (request instanceof ServletServerHttpRequest){
            ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) request;
            HttpServletRequest servletRequest = servletServerHttpRequest.getServletRequest();
            String token = servletRequest.getParameter("token");
            if (StringUtils.isEmpty(token)){
                return null;
            }
            return new Principal() {
                @Override
                public String getName() {
                    return token;
                }
            };
        }
        return null;
    }
}
