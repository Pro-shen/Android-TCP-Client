package com.example.clientsocket.tcpUtil;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


import java.io.IOException;
import java.net.Socket;

public class TCPClientService {

    private String ip;
    private Integer port;

    public static Socket socket;

    public static Handler handler;

    public TCPClientService(String ip, Integer port, Handler handler) {
        this.ip = ip;
        this.port = port;
        this.handler = handler;
    }

    public void startClient() {
        try {
            Message message = new Message();
            message.what = 0;
            handler.sendMessage(message);
            // 实例化Socket就是与远端计算机建立连接的过程。
            socket = new Socket(ip, port);
            socket.setSoTimeout(600 * 1000);
            // 启动用来读取服务端消息的线程
            ClientThread sh = new ClientThread(ip, port,handler);
            Thread thread = new Thread(sh);
            // 守护线程
            thread.setDaemon(true);
            thread.start();
        } catch (Exception e) {
            Log.e("TCPSocket", "TCPSocket连接服务端失败");
            int res = 0;
            while (res == 0) {
                try {
                    Log.e("TCPSocket", "重新创建socket服务");
                    Thread.sleep(5 * 1000);
                    socket = new Socket(ip, port);
                    socket.setSoTimeout(600 * 1000);
                    ClientThread sh = new ClientThread(ip, port,handler);
                    Thread thread = new Thread(sh);
                    thread.setDaemon(true);
                    thread.start();
                    res = 1;
                } catch (IOException | InterruptedException ex) {
                    Log.e("TCPSocket", "socket创建错误，重启TCPSocket");
                }
            }
        }
    }
}

