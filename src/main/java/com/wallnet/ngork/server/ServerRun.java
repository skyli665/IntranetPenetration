package com.wallnet.ngork.server;

/**
 * @author skyli665
 */
public class ServerRun {
    public static void main(String[] args) {
        new Thread(new ClientServer()).start();
        new Thread(new WebServer()).start();
    }
}
