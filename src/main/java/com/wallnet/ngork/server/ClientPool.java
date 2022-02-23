package com.wallnet.ngork.server;

import com.wallnet.ngork.core.ClientBean;
import com.wallnet.ngork.core.ClientType;
import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ClientPool {

    /**
     * 连接集合
     */
    private static Map<String, Channel> map = new ConcurrentHashMap<>();
    /**
     * 客户端列表
     */
    private static Map<String, ClientBean> textMap = new ConcurrentHashMap<>();

    /**
     * 向集合中添加新连接
     */
    public static void addChannel(String id, Channel channel) {
        map.put(id, channel);
    }

    public static List<ClientBean> getClientByType(ClientType type) {
        return textMap.values().stream().filter(c -> type.equals(c.getType())).collect(Collectors.toList());
    }

    /**
     * 替换客户端信息
     */
    public static void setClient(String id, ClientBean client) {
        textMap.replace(id, client);
    }

    /**
     * 获取连接集合
     */
    public static Map<String, Channel> getChannelList() {
        return map;
    }

    /**
     * 根据连接id获取连接
     */
    public static Channel getChannel(String id) {
        return map.get(id);
    }

    /**
     * 移除指定连接
     */
    public static void removeChannel(String id) {
        map.remove(id);
    }

    /**
     * 添加客户端
     */
    public static void addClient(String id, ClientBean client) {
        textMap.put(id, client);
    }

    /**
     * 根据id获取客户端
     */
    public static ClientBean getClient(String id) {
        return textMap.get(id);
    }

    /**
     * 根据客户端id获取连接
     */
    public static Channel getChannelByTextId(String id) {
        ClientBean text = textMap.get(id);
        return getChannel(text.getChannelId());
    }
}