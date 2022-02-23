package com.wallnet.ngork.server;

import com.wallnet.ngork.core.FormatBytes;
import com.wallnet.ngork.core.Text;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final Log LOG = LogFactory.getLog(ServerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("server 读数据");
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        System.out.println(req.length);
        Text text = FormatBytes.myreadMethod(req);
        ByteBuf resp = null;
        switch (text.getMethod()) {
            case "FIND_PORT": {
                InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
                int port = socketAddress.getPort();
                String address = socketAddress.getAddress().getHostAddress();
                String uuid = ctx.channel().id().asLongText();
                text.setChannleId(uuid);
                text.setPort(port);
                text.setAddr(address);
                resp = Unpooled.copiedBuffer(FormatBytes.mywriteMethod(text));
                ctx.write(resp);
                ClientPool.addText("1", text);
                ClientPool.addChannel(text.getChannleId(), ctx.channel());
                break;
            }
            case "GET": {
                System.out.println(new String(text.getContext(), StandardCharsets.UTF_8));
                ClientPool.getText("2").setContext(text.getContext());
                ClientPool.getText("2").setFlag(true);
                break;
            }
            default: {
                LOG.info("协议方法异常");
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        LOG.info("结束");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}