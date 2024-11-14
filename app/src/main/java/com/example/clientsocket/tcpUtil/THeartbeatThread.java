package com.example.clientsocket.tcpUtil;

import android.util.Log;

import com.example.clientsocket.tcpUtil.TCPClientService;

import java.io.OutputStream;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class THeartbeatThread {

    private Timer timer;

    public int counter = 0;

    public DataBackListener dataBackListener;


    public THeartbeatThread( DataBackListener dataBackListener) {
        this.dataBackListener = dataBackListener;
    }

    public void THeartbeatThreadStart() {
        timer = new Timer();
        Random random = new Random();
        try {
            Thread.sleep( random.nextInt(20) * 100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        timer.schedule(new THeartbeatThreadTask(), 500, 20 * 1000);
    }

    public void THeartbeatThreadStop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

    }

    public void THeartbeatThreadCounterTo0() {
        counter = 0;
    }

    class THeartbeatThreadTask extends TimerTask {
        @Override
        public void run() {
            try {
                Log.e("TCPSocket", "TimerTask");
                if (TCPClientService.socket != null) {
                    byte[] bytes = "NOOP".getBytes();
                    OutputStream outputStream = TCPClientService.socket.getOutputStream();
                    outputStream.write(bytes);
                    outputStream.flush();
                } else {
                    dataBackListener.resetTCPClient();
                }
            } catch (Exception e) {
                Log.e("TCPClientHeartbeatThreadTask", e + "");
                dataBackListener.resetTCPClient();
            }
        }
    }

    public interface DataBackListener {
        public void resetTCPClient();
    }

}
