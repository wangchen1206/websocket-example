package com.cc.websocket.message;

import lombok.Data;

/**
 * 发送给所有人
 *
 * @author wangchen
 * @createDate 2020/7/29
 **/
@Data
public class SendToAllRequest implements Message {

    public static final String TYPE = "SEND_TO_ALL_REQUEST";

    /**
     * 消息编号
     */
    private String msgId;
    /**
     * 内容
     */
    private String content;
    

}