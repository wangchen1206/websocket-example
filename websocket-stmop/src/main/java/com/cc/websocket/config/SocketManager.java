package com.cc.websocket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class SocketManager {
    private static ConcurrentHashMap<String, WebSocketSession> manager = new ConcurrentHashMap<String, WebSocketSession>();
    private static ConcurrentHashMap<String, Integer> managerErrorSize = new ConcurrentHashMap<String, Integer>();

    public static void add(String key, WebSocketSession webSocketSession) {
        log.info("新添加webSocket连接 {} ", key);
        manager.put(key, webSocketSession);
        managerErrorSize.put(key, 0);
    }

    public static void remove(String key) {
        log.info("移除webSocket连接 {} ", key);
        manager.remove(key);
        managerErrorSize.remove(key);
    }

    public static WebSocketSession get(String key) {
        log.info("获取webSocket连接 {}", key);
        return manager.get(key);
    }

    public static ConcurrentHashMap<String, WebSocketSession> getManager() {
        return manager;
    }

    public static void errorConut(String key) {
        if (managerErrorSize.contains(key)) {
            Integer integer = managerErrorSize.get(key);
            if (integer.intValue() >= 12) {
                managerErrorSize.remove(key);
                manager.remove(key);
                return;
            }
            managerErrorSize.put(key, integer + 1);
        }
    }

}
