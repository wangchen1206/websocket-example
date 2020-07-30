package com.cc.websocket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Description
 *
 * @author wangchen
 * @createDate 2020/07/30
 */
@Controller
public class GreetingController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 客户端发送消息到/app/change-notice,然后再进行转发消息。
     *
     * @param [value]
     * @author wangchen
     * @createDate 2020/7/30
     **/
    @MessageMapping("/change-notice")
    @SendTo("/topic/notice")  //转发消息到指定的地址
    public String greeting(String value){
        //广播发送，谁订阅了/topic/notice,谁收到消息
        return value;
    }

    /**
     * 客户端发送消息到/app/change-notice,然后再进行转发消息。
     *
     * @param [value]
     * @author wangchen
     * @createDate 2020/7/30
     **/
    @MessageMapping("/change-notice1")
    public void greeting1(String value){
        //指定user发送，并指定客户端订阅路径。user是Princple中的name
        this.simpMessagingTemplate.convertAndSendToUser("ck","/topic/notice",value);
    }

    /**
     * 客户端发送消息到/app/change-notice,然后再进行转发消息。
     *
     * @param [value]
     * @author wangchen
     * @createDate 2020/7/30
     **/
    @MessageMapping("/change-notice2")
    public void greeting2(String value){
        //指定user发送，并指定客户端订阅路径。user是Princple中的name
        this.simpMessagingTemplate.convertAndSendToUser("qq","/topic/notice",value);
    }

    @RequestMapping("/index")
    public String index(){
        return "index";
    }

    @RequestMapping("/index2")
    public String index2(){
        return "index2";
    }

    @RequestMapping("/notice")
    public String notice(){
        return "notice";
    }

    @RequestMapping("/notice2")
    public String notice2(){
        return "notice2";
    }
}
