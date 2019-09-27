package com.like.demo.netty.thread.server;


import com.like.demo.netty.thread.handler.ServerHandler;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author Ke
 * @Date 2019/7/17 11:42
 * @Version 1.0
 */
public class NettyServer {
    public static void main(String[] args) {

        //服务类
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        //boss线程监听端口，worker线程负责数据读写
        ExecutorService boss = Executors.newCachedThreadPool();
        ExecutorService worker = Executors.newCachedThreadPool();


        //设置niosocket工厂
        serverBootstrap.setFactory(new NioServerSocketChannelFactory(boss, worker));
        //设置管道的工厂
        serverBootstrap.setPipelineFactory(new ChannelPipelineFactory() {

            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("decoder", new StringDecoder());
                pipeline.addLast("encoder", new StringEncoder());
                pipeline.addLast("helloHandler", new ServerHandler());
                return pipeline;
            }
        });
        serverBootstrap.bind(new InetSocketAddress(8000));


    }
}
