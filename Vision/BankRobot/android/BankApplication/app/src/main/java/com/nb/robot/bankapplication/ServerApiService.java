package com.nb.robot.bankapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ServerApiService extends Service {
    private static final String TAG = ServerApiService.class.getName();

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        ServerApiService getService() {
            return ServerApiService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "ServerApiService received start id " + startId + ": " + intent);
        return START_STICKY;
    }

    public void speak(final String content, final int repeat) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ServerUtils.speak(content, repeat);
            }
        }, "SpeakThread");
        thread.start();
    }

    public void expression(final int emotionId, final long duration, final int repeat) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ServerUtils.expression(emotionId, duration, repeat);
            }
        }, "ExpressionThread");
        thread.start();
    }

    public void dance(final int danceType) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ServerUtils.dance(danceType);
            }
        }, "DanceThread");
        thread.start();
    }

    public void armUpDown(final int armPart){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ServerUtils.armUpDown(armPart);
            }
        }, "ArmThread");
        thread.start();
    }

    public void armsDown(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ServerUtils.armsDown();
            }
        }, "ArmThread");
        thread.start();
    }
}
