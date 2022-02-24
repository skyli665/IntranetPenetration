package com.wallnet.ngork.client;

import com.wallnet.ngork.core.ClientBean;
import com.wallnet.ngork.core.ClientType;
import com.wallnet.ngork.core.FormatBytes;
import com.wallnet.ngork.core.SocketUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;

@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 发送数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("连服务器，发送数据");
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().localAddress();
        int port = socketAddress.getPort();
        String address = socketAddress.getAddress().getHostAddress();
        ClientBean bean = new ClientBean();
        bean.setMethod("BUILD");
        bean.setType(ClientType.CLIENT);
        bean.setLanAddr(address);
        bean.setLanPort(port);
        log.info("连服务器，发送数据[{}]...", bean);
        //写出数据
        ctx.writeAndFlush(Unpooled.copiedBuffer(FormatBytes.write(bean)));
    }

    /**
     * 接收数据
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("client读取server数据");
        //接收服务端发送的数据
        ByteBuf buf = (ByteBuf) msg;
        ByteBuf resp = null;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        ClientBean bean = FormatBytes.read(req);
        log.info("客户端接收的数据为" + bean.toString());
        switch (bean.getMethod()) {
            //代理连接方法
            case "GET": {
                log.info("执行GET方法，代理TCP连接");
                try {
                    byte[] result =
                            SocketUtils.doSocket("10.10.10.2", 6379, bean.getBytes()).getBytes();
                    bean.setBytes(result);
                    byte[] bytes = FormatBytes.write(bean);
                    log.info("返回数据长度为[{}]", bytes.length);
                    resp = Unpooled.copiedBuffer(bytes);
                } catch (IOException e) {
                    log.error("网络连接故障", e);
                }
                ctx.write(resp);
                break;
            }
            //连接建立完成方法
            case "BUILD": {
                log.info("服务端确认连接建立");
                bean.setMethod("READY");
                resp = Unpooled.copiedBuffer(FormatBytes.write(bean));
                ctx.write(resp);
                break;
            }
            default:
                break;
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    /**
     * 发生异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("连接发生异常[{}]", cause);
        //释放资源
        ctx.close();
    }
}