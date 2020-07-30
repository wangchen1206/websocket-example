package com.cc.websocket.handler;

import com.cc.websocket.message.AuthRequest;
import com.cc.websocket.message.AuthResponse;
import com.cc.websocket.message.UserJoinNoticeRequest;
import com.cc.websocket.util.WebSocketUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;


/**
 * <Description>
 *
 * @author wangchen
 * @createDate 2020/07/29
 */
@Component
public class AuthMessageHandler implements MessageHandler<AuthRequest> {
    @Override
    public void execute(WebSocketSession session, AuthRequest message) {
        //如果未传递 accessToken
        if (StringUtils.isEmpty(message.getAccessToken())){
            WebSocketUtil.send(session, AuthResponse.TYPE,AuthResponse.builder().code(1).message("认证 accessToken 为传入").build());
            return;
        }
        //添加到WebSocketUtil
        WebSocketUtil.addSession(session,message.getAccessToken()); //考虑到代码简化，我们先直接使用accessToken作为user

        //判断是否认证成功。这里假装直接成功。
        WebSocketUtil.send(session,AuthResponse.TYPE,AuthResponse.builder().code(0).build());

        //通知所有人某个人加入了。这个是可选逻辑，仅为了演示
        WebSocketUtil.broadcast(UserJoinNoticeRequest.TYPE,UserJoinNoticeRequest.builder().nickname(message.getAccessToken()).build());
    }

    @Override
    public String getType() {
        return AuthRequest.TYPE;
    }
}
