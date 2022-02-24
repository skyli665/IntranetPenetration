package com.wallnet.ngork.server;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import com.wallnet.ngork.core.ClientBean;
import com.wallnet.ngork.core.ClientType;
import com.wallnet.ngork.core.FormatBytes;
import com.wallnet.ngork.core.SocketUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.beans.BeanCopier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class WebServerHandler extends ChannelInboundHandlerAdapter {

    private static BeanCopier beanCopier = BeanCopier.create(ClientBean.class, ClientBean.class,
            false);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
        log.info("server读取网络端数据...");
        String uuid = ctx.channel().id().asLongText();
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        ClientPool.addChannel(uuid, ctx.channel());
        //获取客户端
        List<ClientBean> clients = ClientPool.getClientByType(ClientType.CLIENT);

        ClientBean clientBean = new ClientBean();
        //做属性拷贝
        beanCopier.copy(clients.get(0), clientBean, null);
        //设置方法
        clientBean.setMethod("GET");
        clientBean.setBytes(req);
        //获取连接
        Channel channel = ClientPool.getChannel(clientBean.getChannelId());
        //设置返回的连接名
        clientBean.setChannelId(uuid);
        clientBean.setFlag(false);
        log.info("获取一个客户端[{}]", clientBean);
        channel.writeAndFlush(Unpooled.copiedBuffer(FormatBytes.write(clientBean)));
        ClientPool.addClient(clientBean.getChannelId(), clientBean);
        while (!ClientPool.getClient(uuid).isFlag()) {
        }
        clientBean = ClientPool.getClient(uuid);
        ClientPool.removeClient(uuid);
        ClientPool.removeChannel(uuid);
        ctx.write(Unpooled.copiedBuffer(clientBean.getBytes()));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}