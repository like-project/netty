package com.huay.demo.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;

public class NIOServer {

    // 通道管理器
    private Selector selector;


    public static void main(String[] args) {
        NIOServer nioServer = new NIOServer();
        nioServer.initServer(8001);
        nioServer.listen();
    }

    /**
     * 采用轮询的方式监听selector上是否有需要处理的事件，如果有，则进行处理
     */
    /**
     * 5、启动IO线程，在循环体中执行Selector.select()方法，轮询就绪的通道
     */
    private void listen() {
        System.out.println("nio server is already start...");
        // 轮询访问selector
        while (true) {
            try {
                if (selector.select(10000) == 0) {
                    //在等待信道准备的同时，也可以异步地执行其他任务，  这里打印*
                    System.out.print("*/");
                    continue;
                }

                selector.select(1000);
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey next = iterator.next();
                    iterator.remove();
                    handler(next);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handler(SelectionKey key) {
        if (key.isAcceptable()) {
            handlerAccept(key);
            // 获得了可读的事件
        } else if (key.isReadable()) {
            handelerRead(key);
        }
    }

    private void handelerRead(SelectionKey key) {
        try {
            // 服务器可读取消息:得到事件发生的Socket通道
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int read = channel.read(buffer);
            if (read > 0) {
                byte[] data = buffer.array();
                String msg = new String(data).trim();
                System.out.println("server receive message：" + msg);
            } else {
                System.out.println("client is closed...");
                key.cancel();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 6、当轮询到处于就绪的通道时，需要进行判断操作位，如果是ACCEPT状态，说明是新的客户端介入，则调用accept方法接受新的客户端。
     *
     * @param key
     */
    private void handlerAccept(SelectionKey key) {

        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        try {
            // 获得和客户端连接的通道
            SocketChannel channel = server.accept();
            // 设置成非阻塞
            channel.configureBlocking(false);
            // 在这里可以给客户端发送信息哦
            System.out.println("new Client is connected...");
            channel.write(ByteBuffer.wrap(new String(new Date() + ":   nio client,channel is build").getBytes()));

            // 在和客户端连接成功之后，为了可以接收到客户端的信息，需要给通道设置读的权限。
            channel.register(this.selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得一个ServerSocket通道，并对该通道做一些初始化的工作
     * 1、创建ServerSocketChannel,为它配置非阻塞模式
     * 2、绑定监听，配置TCP参数，录入backlog大小等
     * 3、创建一个独立的IO线程，用于轮询多路复用器Selector
     * 4、创建Selector，将之前的ServerSocketChannel注册到Selector上，并设置监听标识位SelectionKey.ACCEPT
     *
     * @param port
     */
    private void initServer(int port) {

        try {
            // 获得一个ServerSocket通道
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            //设置socket通道为非阻塞
            serverChannel.configureBlocking(false);
            // 将该通道对应的ServerSocket绑定到port端口
            serverChannel.socket().bind(new InetSocketAddress(port));
            // 获得一个通道管理器
            this.selector = Selector.open();

            // 将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件,注册该事件后，
            // 当该事件到达时，selector.select()会返回，如果该事件没到达selector.select()会一直阻塞。
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
