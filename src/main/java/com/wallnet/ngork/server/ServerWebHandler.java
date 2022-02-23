package com.wallnet.ngork.server;

import com.wallnet.ngork.core.FormatBytes;
import com.wallnet.ngork.core.Properties;
import com.wallnet.ngork.core.SocketUtils;
import com.wallnet.ngork.core.Text;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ServerWebHandler extends ChannelInboundHandlerAdapter {
    private static final Log LOG = LogFactory.getLog(ServerWebHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
        String uuid = ctx.channel().id().asLongText();
        System.out.println("server 读数据");
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        Text text = new Text();
        text.setChannleId(uuid);
        text.setMethod("GET");
        ClientPool.addText("2", text);
        ClientPool.addChannel(text.getChannleId(), ctx.channel());
        text.setContext(req);
        LOG.info("客户端发送数据->" + text);
        text.setFlag(false);
        ClientPool.getChannelByTextId("1").writeAndFlush(Unpooled.copiedBuffer(FormatBytes.mywriteMethod(text)));
        while (true) {
            if ((text = ClientPool.getText("2")).isFlag()) {
                ByteBuf resp = Unpooled.copiedBuffer(text.getContext());
                ctx.write(resp);
                break;
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