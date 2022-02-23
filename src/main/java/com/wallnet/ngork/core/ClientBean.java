package com.wallnet.ngork.core;

import lombok.Data;

import java.io.Serializable;

/**
 * 客户端实体类
 *
 * @author skyli
 */
@Data
public class ClientBean implements Serializable {

    /**
     * 连接UUID
     */
    private String channelId;
    /**
     * 方法名
     */
    private String method;
    /**
     * 客户端内网端口
     */
    private int lanPort;
    /**
     * 客户端外网端口
     */
    private int wanPort;
    /**
     * 客户端内网ip
     */
    private String lanAddr;
    /**
     * 客户端外网ip
     */
    private String wanAddr;
    /**
     * 传输的数据
     */
    private byte[] bytes;
    /**
     * 是否可以读取数据
     */
    private boolean flag;

    private ClientType type;

    @Override
    public String toString() {
        return "ClientBean{" +
                "channelId='" + channelId + '\'' +
                ", method='" + method + '\'' +
                ", lanPort=" + lanPort +
                ", wanPort=" + wanPort +
                ", lanAddr='" + lanAddr + '\'' +
                ", wanAddr='" + wanAddr + '\'' +
                '}';
    }
}