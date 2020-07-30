package com.cc.websocket.handler;

import com.cc.websocket.message.SendResponse;
import com.cc.websocket.message.SendToOneRequest;
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
public class SendToOneHandler implements MessageHandler<SendToOneRequest> {
    @Override
    public void execute(WebSocketSession session, SendToOneRequest message) {
        //这里假装直接成功
        SendResponse sendResponse = new SendResponse();
        sendResponse.setCode(0);
        sendResponse.setMsgId(message.getMsgId());
        WebSocketUtil.send(session,SendResponse.TYPE,sendResponse);

        //创建转发消息
        SendToOneRequest sendToOneRequest = new SendToOneRequest();
        sendToOneRequest.setContent(message.getContent());
        sendToOneRequest.setMsgId(message.getMsgId());
        //指定user发送
        WebSocketUtil.send(message.getToUser(),SendToOneRequest.TYPE,sendToOneRequest);
    }

    @Override
    public String getType() {
        return SendToOneRequest.TYPE;
    }
}
