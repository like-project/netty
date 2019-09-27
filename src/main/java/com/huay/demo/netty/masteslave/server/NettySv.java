package com.huay.demo.netty.masteslave.server;

import com.huay.demo.netty.masteslave.handler.ServerChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Date;

/**
 * @Author Ke
 * @Date 2019/7/24 10:50
 * @Version 1.0
 */
public class NettySv {
    public static void main(String[] args) {
        // 创建mainReactor
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();//boosGroup 用于 Accetpt 连接建立事件并分发请求
        // 创建工作线程组
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();//workerGroup 用于处理 I/O 读写事件和业务逻辑。
        //基于 ServerBootstrap(服务端启动引导类)，配置 EventLoopGroup、Channel 类型，连接参数、配置入站、出站事件 handler

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(boosGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024).childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        // 配置入站、出站事件channel
                        nioSocketChannel.pipeline().addLast("decoder", new StringDecoder());
                        nioSocketChannel.pipeline().addLast("encoder", new StringEncoder());
                        nioSocketChannel.pipeline().addLast("channelHandler", new ServerChannelHandler());
                    }
                });


        // 绑定端口,开始工作。
        int port = 8000;
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println(new Date() + ": 端口[" + port + "]绑定成功!");
            } else {
                System.err.println("端口[" + port + "]绑定失败!");
            }
        });
    }
}
