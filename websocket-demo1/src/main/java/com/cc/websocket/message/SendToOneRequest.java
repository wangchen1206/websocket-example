package com.cc.websocket.message;

import lombok.Data;

/**
 * 发送给指定人
 *
 * @author wangchen
 * @createDate 2020/7/29
 **/
@Data
public class SendToOneRequest implements Message {

    public static final String TYPE = "SEND_TO_ONE_REQUEST";

    /**
     * 发送给的用户
     */
    private String toUser;
    /**
     * 消息编号
     */
    private String msgId;
    /**
     * 内容
     */
    private String content;
    
}