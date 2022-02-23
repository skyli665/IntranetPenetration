package com.wallnet.ngork.server;

import com.wallnet.ngork.core.ClientBean;
import com.wallnet.ngork.core.ClientType;
import com.wallnet.ngork.core.FormatBytes;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class ClientServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        log.info("服务端收到客户端消息，长度[{}]", req.length);
        ClientBean bean = FormatBytes.read(req);
        ByteBuf resp = null;
        switch (bean.getMethod()) {
            case "BUILD": {
                log.info("进入build阶段，准备建立连接...");
                //获取远程客户端地址
                InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
                int port = socketAddress.getPort();
                String address = socketAddress.getAddress().getHostAddress();
                String uuid = ctx.channel().id().asLongText();
                bean.setChannelId(uuid);
                bean.setWanPort(port);
                bean.setWanAddr(address);
                bean.setType(ClientType.CLIENT);
                resp = Unpooled.copiedBuffer(FormatBytes.write(bean));
                ctx.write(resp);
                ClientPool.addClient(bean.getChannelId(), bean);
                ClientPool.addChannel(bean.getChannelId(), ctx.channel());
                break;
            }
            case "READY": {
                log.info("进入ready阶段，替换客户端信息，等待客户端开放端口...");
                bean.setMethod("ALREADY");
                resp = Unpooled.copiedBuffer(FormatBytes.write(bean));
                ctx.write(resp);
                ClientPool.setClient(bean.getChannelId(), bean);
                break;
            }
            default: {
                log.info("协议方法异常");
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        log.info("结束");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("连接发生异常，请检查连接[{}]", cause);
        ctx.close();
    }
}