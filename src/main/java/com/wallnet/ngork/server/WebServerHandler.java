package com.wallnet.ngork.server;

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
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class WebServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
        log.info("server读取网络端数据...");
        String uuid = ctx.channel().id().asLongText();
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        ClientBean bean = new ClientBean();
        bean.setChannelId(uuid);
        bean.setMethod("GET");
        bean.setType(ClientType.NET);
        ClientPool.addClient(uuid, bean);
        ClientPool.addChannel(bean.getChannelId(), ctx.channel());
        bean.setBytes(req);
        log.info("向客户端请求数据[{}]", bean);
        //获取客户端
        List<ClientBean> clients = ClientPool.getClientByType(ClientType.CLIENT);
        ClientBean clientBean = clients.get(0);
        log.info("获取一个客户端[{}]", clientBean);
        clientBean.setMethod("GET");
        clientBean.setBytes(req);
        ClientBean res = SocketUtils.doSocket(clientBean.getWanAddr(),
                clientBean.getWanPort(),
                FormatBytes.write(clientBean));
        clientBean = FormatBytes.read(res.getBytes());
        //返回读到的数据
        log.info("客户端返回消息，内容为:[\r\n{}\r\n]", new String(clientBean.getBytes(),
                StandardCharsets.UTF_8));
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