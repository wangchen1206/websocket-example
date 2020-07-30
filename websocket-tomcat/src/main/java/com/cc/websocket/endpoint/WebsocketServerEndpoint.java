package com.cc.websocket.endpoint;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cc.websocket.handler.MessageHandler;
import com.cc.websocket.message.AuthRequest;
import com.cc.websocket.message.Message;
import com.cc.websocket.message.SessionCloseRequest;
import com.cc.websocket.util.WebSocketUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * WebsocketEndpoint
 *
 * @author wangchen
 * @createDate 2020/07/29
 */
@Controller  //保证创建一个 WebsocketServerEndpoint Bean
@ServerEndpoint("/") //标记这是一个 WebSocket EndPoint ，路径为 /
@Slf4j
public class WebsocketServerEndpoint implements InitializingBean {

    /**
     *  消息类型与MessageHandler映射
     *
     *  这里设置成静态的。虽然WebsocketServerEndpoint是单例，但是Spring boot还是会为每个WebSocket创建一个WebsocketServerEndpoint Bean
     **/
    private static final Map<String, MessageHandler> HANDLERS = new HashMap<>();

    @Autowired
    private ApplicationContext applicationContext;


    /**
     * 避免手动配置MessageHandler与消息类型的映射
     * 获取MessageHandler与消息类型的映射
     *
     * @param []
     * @author wangchen
     * @createDate 2020/7/29
     **/
    @Override
    public void afterPropertiesSet() throws Exception {
        //通过ApplicationContext获取所有的MessageHandler Bean
        applicationContext.getBeansOfType(MessageHandler.class).values() //获取所有的MessageHandler Bean
                .forEach(messageHandler -> HANDLERS.put(messageHandler.getType(),messageHandler)); //添加到HANDLERS中
        HANDLERS.keySet().stream().forEach(System.out::println);
        log.info("[afterPropertiesSet][消息处理器数量：{}]", HANDLERS.size());
    }


    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig){
        log.info("[onOpen][sesison({}) 接入]",session);
        //1.解析accessToken
        List<String> accessTokens = session.getRequestParameterMap().get("accessToken");
        String accessToken = !CollectionUtils.isEmpty(accessTokens)?accessTokens.get(0):null;
        //2.创建AuthRequest
        AuthRequest authRequest = new AuthRequest();
        authRequest.setAccessToken(accessToken);
        //3.获取消息处理器
        MessageHandler<AuthRequest> messageHandler = HANDLERS.get(AuthRequest.TYPE);
        if (messageHandler == null){
            log.error("[onOpen][认证消息类型，不存在消息处理器]");
            return;
        }
        //4.认证
        messageHandler.execute(session,authRequest);
    }

    @OnMessage
    public void onMessage(Session session, String message){
        log.info("[onMessage][sesison({}) 收到一条消息 {}]",session,message);
        try {
            //1.获取消息类型
            JSONObject jsonMessage = JSON.parseObject(message);
            String messageType = jsonMessage.getString("type");
            //2.获取消息处理器
            MessageHandler messageHandler = HANDLERS.get(messageType);
            if (messageHandler == null){
                log.error("[onMessage][消息类型({}) 不存在消息处理器]",messageType);
                return;
            }
            //3.解析消息
            Class<? extends Message> messageClass = this.getMessageClass(messageHandler);
            //4.处理消息
            Message messageObj = JSON.parseObject(jsonMessage.getString("body"),messageClass);
            messageHandler.execute(session,messageObj);
        }catch (Throwable throwable){
            log.info("[onMessage][session({}) message({}) 发生异常]", session, throwable);
        }
    }



    @OnClose
    public void onClose(Session session, CloseReason closeReason){
        log.info("[onClose][sesison({})连接关闭，关闭原因是：{}]",session,closeReason);
        //先广播通知所有人 session 下线  。。这个不可行，因为已经下线。。。
//        MessageHandler<SessionCloseRequest> sessionCloseRequestMessageHandler = HANDLERS.get(SessionCloseRequest.TYPE);
//        SessionCloseRequest sessionCloseRequest = new SessionCloseRequest();
//        sessionCloseRequest.createCloseContent(session);
//        sessionCloseRequest.setMsgId(UUID.randomUUID().toString());
//        sessionCloseRequestMessageHandler.execute(session,sessionCloseRequest);
        WebSocketUtil.removeSession(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable){
        log.info("[onError][sesison({}) 发生异常 {}]",session,throwable);
    }

    /**
     * 获取指定MessageHandler处理的消息类型。获取
     *
     * @param [messageHandler]
     * @author wangchen
     * @createDate 2020/7/29
     **/
    private Class<? extends Message> getMessageClass(MessageHandler messageHandler) {
        //获得Bean对应的Class 类名。因为有可能被AOP代理过
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(messageHandler);
        //获得接口的Type数组
        Type[] interfaces = targetClass.getGenericInterfaces();
        Class<?> superClass = targetClass.getSuperclass();
        while ((Objects.isNull(interfaces)||interfaces.length == 0) && Objects.nonNull(superClass)){ // 此处，是以父类的接口为准
            interfaces = superClass.getGenericInterfaces();
            superClass = superClass.getSuperclass();
//            superClass = targetClass.getSuperclass();
        }
        if (Objects.nonNull(interfaces)){
            //遍历 interfaces数组
            for (Type type : interfaces){
                //要求Type是泛型参数
                if(type instanceof ParameterizedType){
                    ParameterizedType parameterizedType = (ParameterizedType)type;
                    //要求是MessageHandler接口
                    if (Objects.equals(parameterizedType.getRawType(),MessageHandler.class)){
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        //取首个元素
                        if (Objects.nonNull(actualTypeArguments) && actualTypeArguments.length > 0){
                            return (Class<Message>)actualTypeArguments[0];
                        }else {
                            throw new IllegalStateException(String.format("类型(%s)获取不到消息类型",messageHandler));
                        }
                    }
                }
            }
        }
        throw new IllegalStateException(String.format("类型(%s) 获得不到消息类型", messageHandler));
    }

}
