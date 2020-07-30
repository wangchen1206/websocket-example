package com.cc.websocket.endpoint;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cc.websocket.handler.MessageHandler;
import com.cc.websocket.message.AuthRequest;
import com.cc.websocket.message.Message;
import com.cc.websocket.util.WebSocketUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 消息处理
 *
 * @author wangchen
 * @createDate 2020/07/30
 */
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler implements InitializingBean {

    /**
     * 消息类型与 MessageHandler 的映射
     *
     * 无需设置成静态变量
     */
    private final Map<String, MessageHandler> HANDLERS = new HashMap();

    @Autowired
    private ApplicationContext applicationContext;


    /**
     * 打开连接事件
     *
     * @param [session]
     * @author wangchen
     * @createDate 2020/7/30
     **/
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("[afterConnectionEstablished][session({}) 接入]",session);
        //解析token
        String accessToken = (String) session.getAttributes().get("accessToken");
        //创建AuthRequest消息
        AuthRequest authRequest = new AuthRequest();
        authRequest.setAccessToken(accessToken);
        //获取消息处理器
        MessageHandler messageHandler = HANDLERS.get(AuthRequest.TYPE);
        if (messageHandler == null){
            log.error("[onOpen][认证消息类型，不存在消息处理器]");
            return;
        }
        messageHandler.execute(session,authRequest);
    }

    /**
     * 处理 收到消息事件
     *
     * @param [session, message]
     * @author wangchen
     * @createDate 2020/7/30
     **/
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        log.info("[handleMessage][session({}) 接收到一条消息({})]", session, textMessage); // 生产环境下，请设置成 debug 级别
        try {
            //获取消息类型
            JSONObject jsonMessage = JSON.parseObject(textMessage.getPayload());
            String messageType = jsonMessage.getString("type");
            //获取消息处理器
            MessageHandler messageHandler = HANDLERS.get(messageType);
            if (messageHandler == null) {
                log.error("[onMessage][消息类型({}) 不存在消息处理器]", messageType);
                return;
            }
            //解析消息
            Class<? extends Message> messageClass = this.getMessageClass(messageHandler);
            Message message = JSON.parseObject(jsonMessage.getString("body"),messageClass);
            //处理消息
            messageHandler.execute(session,message);
        } catch (Throwable e) {
            log.info("[onMessage][session({}) message({}) 发生异常]", session, e);
        }
    }

    /**
     * 连接关闭事件
     *
     * @param [session, status]
     * @author wangchen
     * @createDate 2020/7/30
     **/
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("[afterConnectionClosed][session({}) 连接关闭。关闭原因是({})}]", session, status);
        WebSocketUtil.removeSession(session);
    }

    /**
     * 连接异常事件
     *
     * @param [session, exception]
     * @author wangchen
     * @createDate 2020/7/30
     **/
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.info("[handleTransportError][session({}) 发生异常 {}]", session, exception);
    }

    /**
     *
     *
     * @param []
     * @author wangchen
     * @createDate 2020/7/30
     **/
    @Override
    public void afterPropertiesSet() throws Exception {
        //通过ApplicationContext获取所有的MessageHandler
        applicationContext.getBeansOfType(MessageHandler.class).values()
                .forEach(messageHandler -> HANDLERS.put(messageHandler.getType(),messageHandler));
        log.info("消息处理器的数量：{}",HANDLERS.size());
    }

    private Class<? extends Message> getMessageClass(MessageHandler handler) {
        // 获得 Bean 对应的 Class 类名。因为有可能被 AOP 代理过。
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(handler);
        // 获得接口的 Type 数组
        Type[] interfaces = targetClass.getGenericInterfaces();
        Class<?> superclass = targetClass.getSuperclass();
        while ((Objects.isNull(interfaces) || 0 == interfaces.length) && Objects.nonNull(superclass)) { // 此处，是以父类的接口为准
            interfaces = superclass.getGenericInterfaces();
            superclass = superclass.getSuperclass();
        }
        if (Objects.nonNull(interfaces)) {
            // 遍历 interfaces 数组
            for (Type type : interfaces) {
                // 要求 type 是泛型参数
                if (type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    // 要求是 MessageHandler 接口
                    if (Objects.equals(parameterizedType.getRawType(), MessageHandler.class)) {
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        // 取首个元素
                        if (Objects.nonNull(actualTypeArguments) && actualTypeArguments.length > 0) {
                            return (Class<Message>) actualTypeArguments[0];
                        } else {
                            throw new IllegalStateException(String.format("类型(%s) 获得不到消息类型", handler));
                        }
                    }
                }
            }
        }
        throw new IllegalStateException(String.format("类型(%s) 获得不到消息类型", handler));
    }
}
