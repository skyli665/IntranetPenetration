package com.wallnet.ngork.core;

import java.io.Serializable;

public class Text implements Serializable {

    private String channleId;
    private String method;
    private int port;
    private String addr;
    private byte[] context;
    private boolean flag;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getChannleId() {
        return channleId;
    }

    public void setChannleId(String channleId) {
        this.channleId = channleId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public byte[] getContext() {
        return context;
    }

    public void setContext(byte[] context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "Text{" +
                "channleId='" + channleId + '\'' +
                ", method='" + method + '\'' +
                ", port=" + port +
                ", addr='" + addr + '\'' +
                '}';
    }
}