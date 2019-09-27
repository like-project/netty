package com.like.demo.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class IOServer {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8000);
            System.out.println("oio server is already start...");
            while (true) {
                Socket socket = serverSocket.accept();
                handler(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handler(Socket socket) {
        System.out.println("new Client connected...");
        byte[] bytes = new byte[1024];
        try {
            InputStream inputStream = socket.getInputStream();
            while (true) {
                //读取数据（阻塞）
                int read = inputStream.read(bytes);
                if (read != -1) {
                    System.out.println(new String(bytes, 0, read));
                } else {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("socket is closed...");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
