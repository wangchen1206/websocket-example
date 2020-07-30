package com.cc.websocket.util;

import com.alibaba.fastjson.JSONObject;
import com.cc.websocket.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.websocket.RemoteEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Session的会话管理
 *
 * @author wangchen
 * @createDate 2020/07/29
 */
@Slf4j
public class WebSocketUtil {

    //==================会话相关==================

    /**
     *  Session 与用户的映射
     **/
    private static final Map<WebSocketSession,String> SESSION_USER_MAP = new ConcurrentHashMap<>();

    /**
     *  用户与Session的映射
     **/
    private static final Map<String,WebSocketSession> USER_SESSION_MAP = new ConcurrentHashMap<>();

    /**
     * 添加Session和用户之间的映射
     *
     * @param [session, user]
     * @author wangchen
     * @createDate 2020/7/29
     **/
    public static void addSession(WebSocketSession  session,String user){
        SESSION_USER_MAP.put(session,user);
        USER_SESSION_MAP.put(user,session);
    }

    /**
     * 移除Session
     *
     * @param [session]
     * @author wangchen
     * @createDate 2020/7/29
     **/
    public static void removeSession(WebSocketSession session){
        //从SESSION_USER_MAP中移除
        String user = SESSION_USER_MAP.remove(session);
        //从USER_SESSION_MAP中移除
        if (!StringUtils.isEmpty(user)){
            USER_SESSION_MAP.remove(user);
        }
    }

    public static String getUser(WebSocketSession  session){
        return SESSION_USER_MAP.get(session);
    }


    //================消息相关=================

    /**
     * 广播发送消息给所有在线用户
     *
     * @param [type, message]
     * @author wangchen
     * @createDate 2020/7/29
     **/
    public static <T extends Message> void broadcast(String type,T message){
        //创建消息
        TextMessage messageText = buildTextMessage(type,message);
        //遍历SESSION_USER_MAP，进行逐个发送
        for (WebSocketSession  session: SESSION_USER_MAP.keySet()){
            sendTextMessage(session,messageText);
        }
    }

    /**
     * 发送消息给单个session
     *
     * @param [session, type, message]
     * @author wangchen
     * @createDate 2020/7/29
     **/
    public static <T extends Message> void send(WebSocketSession  session,String type,T message){
        //创建消息
        TextMessage messageText = buildTextMessage(type,message);
        sendTextMessage(session,messageText);
    }




    /**
     * 发送消息给指定用户
     *
     * @param [user, type, message]
     * @author wangchen
     * @createDate 2020/7/29
     **/
    public static <T extends Message> boolean send(String user,String type,T message){
        //获得用户的session
        WebSocketSession  session = USER_SESSION_MAP.get(user);
        if (session == null){
            log.error("[send][user({}) 不存在对应的 session]", user);
            return false;
        }
        //发送消息
        send(session,type,message);
        return true;
    }

    /**
     * 构建完整消息
     *
     * @param [type, message]
     * @author wangchen
     * @createDate 2020/7/29
     **/
    private static <T extends Message> TextMessage buildTextMessage(String type, T message) {
        JSONObject messageObject = new JSONObject();
        messageObject.put("type",type);
        messageObject.put("body",message);
        return new TextMessage(messageObject.toJSONString());
    }

    /**
     * 真正发送消息
     *
     * @param [session, messageText]
     * @author wangchen
     * @createDate 2020/7/29
     **/
    private static void sendTextMessage(WebSocketSession session, TextMessage messageText) {
        if (session == null){
            log.error("[sendTextMessage][session 为 null]");
            return;
        }

        try {
            session.sendMessage(messageText);
        } catch (IOException e) {
            log.error("[sendTextMessage][session({}) 发送消息{}) 发生异常",
                    session, messageText, e);
        }
    }

}
