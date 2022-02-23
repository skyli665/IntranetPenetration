package com.wallnet.ngork.client;

import com.wallnet.ngork.core.FormatBytes;
import com.wallnet.ngork.core.SocketUtils;
import com.wallnet.ngork.core.Text;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    /**
     * 发送数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("连服务器，发送数据");
        Text text = new Text();
        text.setMethod("FIND_PORT");
        //要发送的消息
        byte[] req = FormatBytes.mywriteMethod(text);
        //消息包
        ByteBuf firstMessage = Unpooled.buffer(req.length);
        //发送
        firstMessage.writeBytes(req);
        //刷新
        ctx.writeAndFlush(firstMessage);
    }

    /**
     * 接收数据
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("client 读取server数据");
        ByteBuf buf = (ByteBuf) msg;
        ByteBuf resp = null;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        Text text = FormatBytes.myreadMethod(req);
        System.out.println("接受的数据为" + text.toString());
        switch (text.getMethod()) {
            case "GET": {
                SocketUtils socket = new SocketUtils();
                byte[] result = socket.doSocket("10.10.10.2", 6379, text.getContext());
                text.setContext(result);
                byte[] bytes = FormatBytes.mywriteMethod(text);
                System.out.println(bytes.length);
                resp = Unpooled.copiedBuffer(bytes);
                ctx.writeAndFlush(resp);
                break;
            }
            case "FIND_PORT": {
                break;
            }
            default:
                break;
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
            throws Exception {
        ctx.flush();
    }

    /**
     * 发生异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("发生异常");
        cause.printStackTrace();
        //释放资源
        ctx.close();
    }
}