package com.cc.websocket.message;// SendResponse.java

import lombok.Data;

/**
 * 发送消息请求的响应结果
 * 在服务端接收到发送消息的请求，需要异步响应发送是否成功
 *
 * @author wangchen
 * @createDate 2020/7/29
 **/
@Data
public class SendResponse implements Message {

    public static final String TYPE = "SEND_RESPONSE";

    /**
     * 消息编号
     */
    private String msgId;
    /**
     * 响应状态码
     */
    private Integer code;
    /**
     * 响应提示
     */
    private String message;
    

}