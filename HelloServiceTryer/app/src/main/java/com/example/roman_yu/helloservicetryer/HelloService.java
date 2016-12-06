package com.example.roman_yu.helloservicetryer;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static android.content.ContentValues.TAG;

/**
 * Created by roman-yu on 06/12/2016.
 */

public class HelloService extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                Thread.sleep(5000);
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            stopSelf(msg.arg1);
            Log.d(TAG, "stopSelf: currentTime: " + getCurrentTime());
        }
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand: currentTime: " + getCurrentTime());

        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    private String getCurrentTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm:ss a");
// you can get seconds by adding  "...:ss" to it
        date.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));

        String localTime = date.format(currentLocalTime);

        return localTime;
    }
}
