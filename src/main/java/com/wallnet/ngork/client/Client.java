package com.wallnet.ngork.client;

import com.wallnet.ngork.server.ClientServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author skyli665
 */
public class Client implements Runnable {

    private int port;

    /**
     * 监听指定端口
     */
    public Client(int port) {
        this.port = port;
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
            ChannelFuture f1 = serverBootstrap.bind(this.port);
            f1.channel().closeFuture().sync();
        } catch (InterruptedException e) {
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
