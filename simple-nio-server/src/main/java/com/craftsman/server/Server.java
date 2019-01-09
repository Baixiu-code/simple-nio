package com.craftsman.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author chenfanglin
 * @desc server 服务器
 */
public class Server {

    private final int port;

    public Server(int port){
        this.port=port;
    }


    public static void main(String[] args) throws InterruptedException {
        if(args.length!=1){
            System.out.println("error");
        }
        int port=Integer.parseInt(args[0]);
        new Server(port).start();
    }

    private void start() throws InterruptedException {

        final ServerHandler serverHandler=new ServerHandler();

        EventLoopGroup eventLoopGroup= new NioEventLoopGroup();
        ServerBootstrap serverBootstrap=new ServerBootstrap();
        //使用NIO传输，指定EventLoopGroup来接收和处理连接
        serverBootstrap.group(eventLoopGroup)
        .channel(NioServerSocketChannel.class)
        //绑定指定的端口
        .localAddress(new InetSocketAddress(this.port))
        //通过ChannelInitializer将新接入的channel添加的channel的管道尾部排队处理
        .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(serverHandler);
            }
        });
        try {
            //开始根据初始化好的serverBootstrap进行服务器初始化，sync的意思是线程将会阻塞到服务器完成绑定
            ChannelFuture f= serverBootstrap.bind().sync();
            //此处sync的意思是该线程将会阻塞，直至关闭
            f.channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully().sync();
        }
    }
}
