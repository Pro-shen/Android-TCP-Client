package com.example.clientsocket;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.example.clientsocket.tcpUtil.TCPClientService;

public class MainActivity extends AppCompatActivity {

    public TextView openClientSocket, clientSocketConnect, severSocketSendNumber, serverSocketSendInfo;

    public static int number = 0;

    final Handler mHandler = new Handler(Looper.myLooper()) {
        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void handleMessage(Message msg) throws RuntimeException {
            super.handleMessage(msg);
            if (msg.what == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openClientSocket.setVisibility(View.VISIBLE);
                    }
                });
            } else if (msg.what == 1) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clientSocketConnect.setVisibility(View.VISIBLE);
                    }
                });
            } else if (msg.what == 2) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        byte[] bytes = (byte[]) msg.obj;
                        severSocketSendNumber.setVisibility(View.VISIBLE);
                        serverSocketSendInfo.setVisibility(View.VISIBLE);
                        number++;
                        severSocketSendNumber.setText("服务端主动向客户端请求次数:" + number);
                        serverSocketSendInfo.setText("服务端主动向客户端发送的消息:" + bytes[0] + "  " + bytes[1] + "  " + bytes[2] + "  " + bytes[3]);
                    }
                });
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openClientSocket = findViewById(R.id.openClientSocket);
        clientSocketConnect = findViewById(R.id.clientSocketConnect);
        severSocketSendNumber = findViewById(R.id.severSocketSendNumber);
        serverSocketSendInfo = findViewById(R.id.serverSocketSendInfo);
        openClientSocket.setVisibility(View.GONE);
        clientSocketConnect.setVisibility(View.GONE);
        severSocketSendNumber.setVisibility(View.GONE);
        serverSocketSendInfo.setVisibility(View.GONE);
        SocketClientThread socketClientThread = new SocketClientThread();
        socketClientThread.start();
    }

    class SocketClientThread extends Thread {
        @Override
        public void run() {
            TCPClientService tcpClientService = new TCPClientService("192.168.0.99", 12345, mHandler);
            tcpClientService.startClient();
        }
    }

}