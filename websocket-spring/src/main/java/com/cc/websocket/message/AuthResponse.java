package com.cc.websocket.message;

import lombok.Builder;
import lombok.Data;

/**
 * 认证响应结果
 *
 * @author wangchen
 * @createDate 2020/7/29
 **/
@Data
@Builder
public class AuthResponse implements Message {

    public static final String TYPE = "AUTH_RESPONSE";

    /**
     * 响应状态码
     */
    private Integer code;
    /**
     * 响应提示
     */
    private String message;
    

}