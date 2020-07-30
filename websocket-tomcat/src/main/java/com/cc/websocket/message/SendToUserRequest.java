package com.cc.websocket.message;// SendResponse.java

import lombok.Data;

/**
 * 在服务端接收到发送消息的请求，需要转发消息给对应的人
 * 发送消息给一个用户的 Message
 *
 * @author wangchen
 * @createDate 2020/7/29
 **/
@Data
public class SendToUserRequest implements Message {

    public static final String TYPE = "SEND_TO_USER_REQUEST";

    /**
     * 消息编号
     */
    private String msgId;
    /**
     * 内容
     */
    private String content;
    

}