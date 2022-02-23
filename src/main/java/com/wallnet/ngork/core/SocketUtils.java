package com.wallnet.ngork.core;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

/**
 * @author skyli665
 */
public class SocketUtils {
    public byte[] doSocket(String host, int port, byte[] input) throws IOException {
        Socket socket = new Socket(host, port);
        //创建输出流
        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
        bos.write(input);
        bos.flush();
        //关闭输出流，准备接收返回数据
        socket.shutdownOutput();
        BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
        byte[] bytes = new byte[1024 * 100];
        byte[] res = new byte[0];
        int len;
        while ((len = bis.read(bytes)) != -1) {
            res = concat(res, bytes, len);
        }
        bis.close();
        socket.close();
        return res;
    }

    private static byte[] concat(byte[] first, byte[] second, int len) {
        byte[] result = Arrays.copyOf(first, first.length + len);
        System.arraycopy(second, 0, result, first.length, len);
        return result;
    }
}
