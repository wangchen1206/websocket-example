package com.cc.websocket.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 * 拦截器拦截请求参数，因为WebSocketSession获取不到请求路径上的参数
 *
 * @author wangchen
 * @createDate 2020/07/30
 */
public class WebSocketShakeInterceptor extends HttpSessionHandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        //先获得accessToken
        if (request instanceof ServletServerHttpRequest){
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
            attributes.put("accessToken",serverRequest.getServletRequest().getParameter("accessToken"));
        }
        //调用父方法，继续执行逻辑
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
}
