package com.wallnet.ngork.client;

import com.wallnet.ngork.core.ClientBean;
import com.wallnet.ngork.core.FormatBytes;
import com.wallnet.ngork.core.Properties;
import com.wallnet.ngork.core.SocketUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author skyli665
 */
@Slf4j
public class ClientRun {
    public static void main(String[] args) throws IOException {
        /*
        build过程，主要是通知服务端准备建立连接，并获取客户端的端口信息
         */
        log.info("执行build过程");
        ClientBean clientBean = new ClientBean();
        clientBean.setMethod("BUILD");
        ClientBean res = SocketUtils.doSocket(Properties.SERVER_ADDR,
                Properties.SERVER_REG_PORT,
                FormatBytes.write(clientBean));
        //获取网络信息
        int port = res.getLanPort();
        String addr = res.getLanAddr();
        log.info("客户端即将绑定端口[{}]ip地址[{}]", port, addr);
        clientBean = FormatBytes.read(res.getBytes());
        clientBean.setLanPort(port);
        clientBean.setLanAddr(addr);
        /*
        ready阶段，打开服务器，并通知服务端可以连接
         */
        clientBean.setMethod("READY");
        new Thread(new Client(clientBean)).start();
    }

}
