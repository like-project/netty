package com.huay.demo.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class IOThreadServer {

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(8000);

            new Thread(() -> {
                while (true) {
                    try {
                        Socket socket = serverSocket.accept();
                        System.out.println("new Client is connected...");
                        new Thread(() -> {
                            byte[] bytes = new byte[1024];
                            try {
                                InputStream inputStream = socket.getInputStream();
                                while (true) {
                                    int len;
                                    while ((len = inputStream.read(bytes)) != -1) {
                                        System.out.println(new String(bytes, 0, len));
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
