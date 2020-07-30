package com.cc.websocket.handler;

import com.cc.websocket.message.SendResponse;
import com.cc.websocket.message.SendToAllRequest;
import com.cc.websocket.message.SendToUserRequest;
import com.cc.websocket.util.WebSocketUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

/**
 * <Description>
 *
 * @author wangchen
 * @createDate 2020/07/29
 */
@Component
public class SendToAllHandler implements MessageHandler<SendToAllRequest> {
    @Override
    public void execute(WebSocketSession session, SendToAllRequest message) {
        //这里假装直接成功
        SendResponse sendResponse = new SendResponse();
        sendResponse.setCode(0);
        sendResponse.setMsgId(message.getMsgId());
        WebSocketUtil.send(session,SendResponse.TYPE,sendResponse);

        //创建转发的消息
        SendToUserRequest sendToUserRequest = new SendToUserRequest();
        sendToUserRequest.setContent(message.getContent());
        sendToUserRequest.setMsgId(message.getMsgId());
        //广播发送
        WebSocketUtil.broadcast(SendToUserRequest.TYPE,sendToUserRequest);
    }

    @Override
    public String getType() {
        return SendToAllRequest.TYPE;
    }
}
