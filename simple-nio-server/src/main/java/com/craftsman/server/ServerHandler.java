package com.craftsman.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @author chenfanglin
 * @desc netty server
 */
@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)  {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("服务器收到消息:"+in.toString(CharsetUtil.UTF_8));
        //接收到的消息写给发送者
        ctx.write(in);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)  {
        //将未决消息冲刷到远程节点，并且在完成之后关闭该节点
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)  {
        cause.printStackTrace();
        ctx.close();
    }
}
