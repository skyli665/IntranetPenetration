package com.wallnet.ngork.core;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

/**
 * @author skyli665
 */
public class SocketUtils {
    public static ClientBean doSocket(String host, int port, byte[] input) throws IOException {
        /*创建一个返回的数据结构
        由于此工具类需要与其他socket进行交互，因此只能使用byte[]进行数据传输
        但在连接建立阶段，需要获取客户端的内网信息，因此需要进行包装
         */
        ClientBean clientBean = new ClientBean();
        //创建一个socket
        Socket socket = new Socket(host, port);
        //获取地址信息
        InetAddress socketInetAddress = socket.getInetAddress();
        clientBean.setLanAddr(socketInetAddress.getHostAddress());
        clientBean.setLanPort(socket.getLocalPort());
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
        //包装返回数据
        clientBean.setBytes(res);
        bis.close();
        socket.close();
        return clientBean;
    }

    private static byte[] concat(byte[] first, byte[] second, int len) {
        byte[] result = Arrays.copyOf(first, first.length + len);
        System.arraycopy(second, 0, result, first.length, len);
        return result;
    }
}
