package com.wallnet.ngork.server;

import com.wallnet.ngork.core.Properties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author skyli665
 */
public class WebServer implements Runnable {

    @Override
    public void run() {
        EventLoopGroup eventLoopGroup = null;
        try {
            //server引导类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            eventLoopGroup = new NioEventLoopGroup();
            serverBootstrap.group(eventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new WebServerHandler());
                        }
                    });
            ChannelFuture f1 = serverBootstrap.bind(Properties.SERVER_WEB_PORT);
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
