package com.wallnet.ngork.server;

import com.wallnet.ngork.core.Properties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ClientServer implements Runnable {

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
                        protected void initChannel(Channel ch) throws Exception {
                            ch.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(Integer.MAX_VALUE));
                            ch.pipeline()
                                    .addLast(new ClientServerHandler());
                        }
                    });
            ChannelFuture f1 = serverBootstrap.bind(Properties.SERVER_REG_PORT);
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