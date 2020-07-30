package com.cc.websocket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

/**
 * Description
 *
 * @author wangchen
 * @createDate 2020/07/30
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private PrincpleHandshakeHandler princpleHandshakeHandler;

    @Autowired
    private WebSocketDecoratorFactory webSocketDecoratorFactory;

    /**
     * 添加一个服务端点来接收客户端的连接
     *
     * @param [registry]
     * @author wangchen
     * @createDate 2020/7/30
     **/
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/socket")//客户端通过这个端点进行连接
                .setAllowedOrigins("*") //允许跨域
                .setHandshakeHandler(princpleHandshakeHandler) //设置拦截，进行扩展session
                .withSockJS();//开启SockJs支持
    }

    /**
     * 定义消息代理，设置消息连接请求的各种规范信息
     *
     * @param [registry]
     * @author wangchen
     * @createDate 2020/7/30
     **/
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");//表示客户端订阅地址的前缀信息。
        registry.setApplicationDestinationPrefixes("/app");//服务端接收消息的前缀
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.addDecoratorFactory(webSocketDecoratorFactory);
    }
}
