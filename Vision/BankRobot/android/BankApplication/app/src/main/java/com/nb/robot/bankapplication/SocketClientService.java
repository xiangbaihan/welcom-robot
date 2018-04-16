package com.nb.robot.bankapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketClientService extends Service {
    private static final String TAG = SocketClientService.class.getName();

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    private Socket socket;
    private ExecutorService executorService;
    private Thread thread;
    private Random random = new Random();

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        SocketClientService getService() {
            return SocketClientService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "SocketClientService created");
        socket = new Socket();
        executorService = Executors.newFixedThreadPool(5);
        thread = new Thread() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    // TODO(Yang): enable this part.
                    if (!socket.isConnected()) {
                        InetSocketAddress isa = new InetSocketAddress(Constants.SERVER_IP_ADDRESS, Constants.SERVER_PORT_SOCKET);
                        try {
                            socket.connect(isa);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                   handleSocketMessage();
                  // handleSocketMessageForTest();
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "SocketClientService received start id " + startId + ": " + intent);
        if (!thread.isAlive()) {
            thread.start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
        Log.d(TAG, "SocketClientService destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // TODO(Yang): test this method.
    private void handleSocketMessage() {
        Log.d(TAG, "监听服务器，等待接收数据。。。");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message = reader.readLine();
            Log.i(TAG,"接收服务器的数据->"+message);
            if(message != null) {
                Intent intent = new Intent();
                intent.setAction(Constants.BROADCAST_ACTION_SOCKET);
                intent.putExtra(Constants.BROADCAST_INTENT_SOCKET, message);
                sendBroadcast(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSocketMessageForTest() {
        try {
            Thread.sleep(8000);
            int state = random.nextInt(4);
            Log.d(TAG, "state: " + state);
            String jsonString = "";
            jsonString = new JSONObject().put("type", 2).put("state", state).put("number", 1)
                    .put("timestamp", 123456789).toString();

            Intent intent = new Intent();
            intent.setAction(Constants.BROADCAST_ACTION_SOCKET);
            intent.putExtra(Constants.BROADCAST_INTENT_SOCKET, jsonString);
            sendBroadcast(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
