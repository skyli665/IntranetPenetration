package com.wallnet.ngork.core;

/**
 * 客户端类型
 */
public enum ClientType {
    NET("网络来源"),
    CLIENT("代理客户端");

    ClientType(String name) {
        this.name = name;
    }

    private final String name;

    public String getName() {
        return this.name;
    }
}
