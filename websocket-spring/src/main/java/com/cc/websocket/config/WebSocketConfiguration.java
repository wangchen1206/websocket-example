package com.cc.websocket.config;

import com.cc.websocket.endpoint.WebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocketConfiguration
 *
 * @author wangchen
 * @createDate 2020/07/29
 */
@Configuration
@EnableWebSocket  //开启Spring Websocket配置
public class WebSocketConfiguration implements WebSocketConfigurer {


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(this.webSocketHandler(),"/*") //配置处理器
                .addInterceptors(this.webSocketShakeInterceptor()) //配置拦截器
                .setAllowedOrigins("*"); //解决跨域问题
    }

    @Bean
    public WebSocketHandler webSocketHandler(){
        return new WebSocketHandler();
    }

    @Bean
    public WebSocketShakeInterceptor webSocketShakeInterceptor(){
        return new WebSocketShakeInterceptor();
    }
}
