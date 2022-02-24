package com.wallnet.ngork.client;

import com.wallnet.ngork.core.ClientBean;
import com.wallnet.ngork.core.FormatBytes;
import com.wallnet.ngork.core.Properties;
import com.wallnet.ngork.core.SocketUtils;
import com.wallnet.ngork.server.ClientServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

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
    public Client(ClientBean bean) {
        this.clientBean = bean;
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
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(Integer.MAX_VALUE));
                            ch.pipeline().addLast(new ClientHandler());
                        }
                    });
            ChannelFuture f1 = serverBootstrap.bind(this.clientBean.getLanPort());
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
            f1.channel().closeFuture().sync();
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
}
