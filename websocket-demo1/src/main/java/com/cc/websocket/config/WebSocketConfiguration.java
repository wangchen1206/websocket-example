package com.cc.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocketConfiguration
 *
 * @author wangchen
 * @createDate 2020/07/29
 */
@Configuration
//@EnableWebSocket  //这个是Spring Websocket配置 ，我们使用Tomcat WebSocket
public class WebSocketConfiguration {

    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();  //该 Bean 的作用，是扫描添加有 @ServerEndpoint 注解的 Bean 。
    }
}
