package com.wallnet.ngork.client;

public class ClientRun {
    public static void main(String[] args) {

        new Thread(new Client()).start();
    }

}
