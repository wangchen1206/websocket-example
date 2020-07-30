package com.cc.websocket.handler;

import com.cc.websocket.message.Message;
import org.springframework.web.socket.WebSocketSession;


/**
 * 消息处理接口
 *
 * @author wangchen
 * @createDate 2020/7/29
 **/
public interface MessageHandler<T extends Message> {

    /**
     * 执行处理消息
     *
     * @param [session, message]
     * @author wangchen
     * @createDate 2020/7/29
     **/
    void execute(WebSocketSession session, T message);

    /**
     * 获取消息类型，即每个Message实现类里的TYPE静态类字段
     *
     * @param []
     * @author wangchen
     * @createDate 2020/7/29
     **/
    String getType();
}
