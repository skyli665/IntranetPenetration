package com.wallnet.ngork;

import com.wallnet.ngork.server.ClientServer;
import com.wallnet.ngork.server.WebServer;

/**
 * @author skyli665
 */
public class Application {

    public static void main(String[] args) {
        new Thread(new ClientServer()).start();
        new Thread(new WebServer()).start();
    }
}


