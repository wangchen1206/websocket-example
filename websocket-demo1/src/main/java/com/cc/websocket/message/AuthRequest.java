package com.cc.websocket.message;


import lombok.Data;

/**
 *
 *
 * @author wangchen
 * @createDate 2020/07/29
 */
@Data
public class AuthRequest implements Message {

    public static final String TYPE = "AUTH_REQUEST";

    /**
     * 认证token
     **/
    private String accessToken;
}
