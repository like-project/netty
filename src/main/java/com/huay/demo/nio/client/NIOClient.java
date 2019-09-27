package com.huay.demo.nio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOClient {

    private Selector selector;


    public static void main(String[] args) {
        String host = "127.0.0.1";
        NIOClient nioClient = new NIOClient();
        nioClient.init(host, 8000);
        nioClient.listen();
    }

    private void listen() {

        while (true) {
            try {
                selector.select();
                //获取所有selectionkey
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                //遍历所有selectionkey
                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isConnectable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        if (channel.isConnectionPending()) {
                            channel.finishConnect();//如果正在连接，则完成连接
                        }
                        channel.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) { //有可读数据事件。
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        channel.read(buffer);
                        byte[] data = buffer.array();
                        String message = new String(data);
                        System.out.println("recevie message from server:, size:"
                                + buffer.position() + " msg: " + message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        try {
//            selector.select();
//            //获取所有selectionkey
//            Set<SelectionKey> selectionKeys = selector.selectedKeys();
//            //遍历所有selectionkey
//            Iterator<SelectionKey> iterator = selectionKeys.iterator();
//            SelectionKey key = null;
//            while (iterator.hasNext()) {
//                key = iterator.next();
//                //获取之后删除
//                iterator.remove();
//                try {
//                    //处理该selectionkey
//                    handleInput(key);
//                } catch (Exception e) {
//                    if (key != null) {
//                        //取消selectionkey
//                        key.cancel();
//                        if (key.channel() != null) {
//                            //关闭该通道
//                            key.channel().close();
//                        }
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

//    private void handleInput(SelectionKey key) throws IOException {
//        //若该selectorkey可用
//        if (key.isValid()) {
//            //将key转型为SocketChannel
//            SocketChannel sc = (SocketChannel) key.channel();
//            //判断是否连接成功
//            if (key.isConnectable()) {
//                //若已经建立连接
//                if (sc.finishConnect()) {
//                    //向多路复用器注册可读事件
//                    sc.register(selector, SelectionKey.OP_READ);
//                    //向管道写数据
//                    doWrite(sc);
//                }else {
//                    //连接失败 进程退出
//                    System.exit(1);
//                }
//            }
//
//            //若是可读的事件
//            if (key.isReadable()) {
//                //创建一个缓冲区
//                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
//                System.out.println("before  :  "+readBuffer);
//                //从管道中读取数据然后写入缓冲区中
//                int readBytes = sc.read(readBuffer);
//                System.out.println("after :  "+readBuffer);
//                //若有数据
//                if (readBytes > 0) {
//                    //反转缓冲区
//                    readBuffer.flip();
//                    System.out.println(readBuffer);
//
//                    byte[] bytes = new byte[readBuffer.remaining()];
//                    //获取缓冲区并写入字节数组中
//                    readBuffer.get(bytes);
//                    //将字节数组转换为String类型
//                    String body = new String(bytes);
//                    System.out.println(body.length());
//                    System.out.println("Now is : " + body + "!");
//                } else if (readBytes < 0) {
//                    key.cancel();
//                    sc.close();
//                } else {
//                    sc.register(selector, SelectionKey.OP_READ);
//                }
//            }
//        }
    //}

    private void doWrite(SocketChannel socketChannel) {
        byte[] bytes = "hello nio server".getBytes();
        //为字节缓冲区分配指定字节大小的容量
        ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
        writeBuffer.put(bytes);
        writeBuffer.flip();
        //System.out.println(writeBuffer.remaining());
        try {
            socketChannel.write(writeBuffer);
            if (!writeBuffer.hasRemaining()) {
                //若缓冲区中无可读字节，则说明成功发送给服务器消息
                System.out.println("Send order 2 server succeed.");
            }
            //writeBuffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init(String host, int port) {
        try {
            this.selector = Selector.open();
            // 获得一个ServerSocket通道
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);


            socketChannel.connect(new InetSocketAddress(host, port));
            socketChannel.register(selector, SelectionKey.OP_READ);
            while (!socketChannel.finishConnect()) {
                System.out.println("is not connect");
            }
            System.out.println("connected");
            doWrite(socketChannel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
