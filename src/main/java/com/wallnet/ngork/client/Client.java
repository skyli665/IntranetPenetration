package com.wallnet.ngork.client;

import com.wallnet.ngork.core.Properties;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author skyli665
 */
@Slf4j
public class Client implements Runnable {

    @Override
    public void run() {
        EventLoopGroup nioEventLoopGroup = null;
        try {
            //客户端引导类
            Bootstrap bootstrap = new Bootstrap();
            //处理连接的线程池,事件线程组
            nioEventLoopGroup = new NioEventLoopGroup();
            //多线程处理
            bootstrap.group(nioEventLoopGroup)
                    //指定通道类型
                    .channel(NioSocketChannel.class)
                    //地址
                    .remoteAddress(new InetSocketAddress(Properties.SERVER_ADDR,
                            Properties.SERVER_REG_PORT))
                    //业务处理
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            //注册handler
                            //配置接收数据缓冲区大小，超出部分无法接收，不要设置过大，linux中会出问题
                            ch.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(Integer.MAX_VALUE));
                            ch.pipeline().addLast(new ClientHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect().sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                nioEventLoopGroup.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

