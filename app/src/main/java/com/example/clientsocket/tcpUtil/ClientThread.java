package com.example.clientsocket.tcpUtil;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.clientsocket.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientThread implements Runnable {

    private String ip;

    private int port;

    private THeartbeatThread tHeartbeatThread;

    public static Handler handler;


    public ClientThread(String ip, int port, Handler handler) {
        Log.e("TCPSocket", "TCPSocket已连接服务端");
        this.ip = ip;
        this.port = port;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            tHeartbeatThread = new THeartbeatThread(new THeartbeatThread.DataBackListener() {
                @Override
                public void resetTCPClient() {
                    int res = 0;
                    while (res == 0) {
                        try {
                            tHeartbeatThread.THeartbeatThreadStop();
                            Log.e("TCPSocket", "重新创建socket服务");
                            Thread.sleep(5 * 1000);
                            TCPClientService.socket = new Socket(ip, port);
                            TCPClientService.socket.setSoTimeout(600 * 1000);
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
            });
            Message message = new Message();
            message.what = 1;
            message.obj = "1";
            handler.sendMessage(message);
            tHeartbeatThread.THeartbeatThreadStart();
            InputStream inputStream = TCPClientService.socket.getInputStream();
            while (true) {
                int len = 0;
                byte[] buffer = new byte[2048];
                while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                    Message message1 = new Message();
                    message1.what =2;
                    message1.obj = buffer;
                    handler.sendMessage(message1);
                    sendToTCPServer(buffer);
                }
            }
        } catch (Exception e) {
            Log.e("e", "" + e);
            int res = 0;
            while (res == 0) {
                try {
                    tHeartbeatThread.THeartbeatThreadStop();
                    Log.e("TCPSocket", "重新创建socket服务");
                    Thread.sleep(5 * 1000);
                    TCPClientService.socket = new Socket(ip, port);
                    TCPClientService.socket.setSoTimeout(600 * 1000);
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

    /**
     * 发送消息给服务器
     *
     * @param bytes
     */
    public static void sendToTCPServer(byte[] bytes) {
        try {
            if (TCPClientService.socket != null) {
                OutputStream outputStream = TCPClientService.socket.getOutputStream();
                outputStream.write(bytes);
                outputStream.flush();
            }
        } catch (Exception e) {
            Log.e("TCPClientSendToTCPServer", e + "");
        }
    }
}

