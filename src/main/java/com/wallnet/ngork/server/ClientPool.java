package com.wallnet.ngork.server;

import com.wallnet.ngork.core.Text;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientPool {

    private static Map<String, Channel> map = new ConcurrentHashMap<>();
    private static Map<String, Text> textMap = new ConcurrentHashMap<>();

    public static void addChannel(String id, Channel channel) {
        map.put(id, channel);
    }

    public static Map<String, Channel> getChannelList() {
        return map;
    }

    public static Channel getChannel(String id) {
        return map.get(id);
    }

    public static void removeChannel(String id) {
        map.remove(id);
    }

    public static void addText(String id, Text text) {
        textMap.put(id, text);
    }

    public static Text getText(String id) {
        return textMap.get(id);
    }

    public static Channel getChannelByTextId(String id) {
        Text text = textMap.get(id);
        return getChannel(text.getChannleId());
    }
}