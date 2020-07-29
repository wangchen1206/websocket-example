package com.cc.websocket.message;

import lombok.Builder;
import lombok.Data;

/**
 * 用户成功认证之后，会广播用户加入群聊的通知 Message
 *
 * @author wangchen
 * @createDate 2020/7/29
 **/
@Data
@Builder
public class UserJoinNoticeRequest implements Message {

    public static final String TYPE = "USER_JOIN_NOTICE_REQUEST";

    /**
     * 昵称
     */
    private String nickname;
    
}