package com.cc.websocket.message;

import com.cc.websocket.util.WebSocketUtil;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

import javax.websocket.Session;

/**
 * SessionCloseRequest
 *
 * @author wangchen
 * @createDate 2020/07/29
 */
@Data
public class SessionCloseRequest implements Message {
    public static final String TYPE = "SESSION_CLOSE_REQUEST";

    /**
     * 消息编号
     */
    private String msgId;

    /**
     * 消息内容
     **/
    private String content = "掉线";

    public void createCloseContent(WebSocketSession session){
        String user = WebSocketUtil.getUser(session);
        this.content = user+"close !";
    }

}
