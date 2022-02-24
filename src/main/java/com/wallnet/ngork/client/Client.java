package com.wallnet.ngork.client;

import com.wallnet.ngork.core.ClientBean;
import com.wallnet.ngork.core.FormatBytes;
import com.wallnet.ngork.core.Properties;
import com.wallnet.ngork.core.SocketUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author skyli665
 */
@Slf4j
public class Client implements Runnable {
    /**
     * 客户端配置
     */
    private ClientBean clientBean;

    /**
     * 监听指定端口
     */
    public Client() {
        try {
            this.clientBean = setup();
        } catch (Exception e) {
            log.error("初始化配置失败", e);
        }
    }

    @Override
    public void run() {
        EventLoopGroup eventLoopGroup = null;
        try {
            //server引导类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            eventLoopGroup = new NioEventLoopGroup();
            serverBootstrap.group(eventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress("localhost", this.clientBean.getLanPort())
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(Integer.MAX_VALUE));
                            ch.pipeline().addLast(new ClientHandler());
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            log.info("开始监听，端口为[{}]", channelFuture.channel().localAddress());

            //通知服务端
            ClientBean res = SocketUtils.doSocket(Properties.SERVER_ADDR,
                    Properties.SERVER_REG_PORT,
                    FormatBytes.write(clientBean));
            clientBean = FormatBytes.read(res.getBytes());
            if ("ALREADY".equals(clientBean.getMethod())) {
                log.info("已经建立连接");
            } else {
                log.error("连接建立失败，检查网络状态");
            }
            //开始服务
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                eventLoopGroup.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 与服务端交互，获取本地配置
     */
    public ClientBean setup() throws IOException {
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
        return clientBean;
    }
}
