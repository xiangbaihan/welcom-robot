package com.nb.robot.bankapplication;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


/**
 * Ads promotion UI (default UI).
 */
public class AdsActivity extends AppCompatActivity {
    private static final String TAG = AdsActivity.class.getName();

    private boolean isActive = false;
    private List<ImageView> mImageViewList;
    private int currentImageViewIndex;
    private View mControlsView;
    private Thread mImageThread;
    private BroadcastReceiver mReceiver;
    private final Runnable mImageRunnable = new Runnable() {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                // The UI operations must runOnUiThread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nextImageView();
                    }
                });
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Log.e(TAG, ex.toString());
                }
            }
        }
    };
    private final View.OnClickListener mImageViewClickListerner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startMainActivity();
        }
    };

    private ServerApiService mServerApiService;
    boolean mIsBound = false;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mServerApiService = ((ServerApiService.LocalBinder) service).getService();
            Log.d(TAG, "ServerApiService connected.");
        }

        public void onServiceDisconnected(ComponentName className) {
            mServerApiService = null;
            Log.d(TAG, "ServerApiService disconnected.");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ads);

        mControlsView = findViewById(R.id.fullscreen_content_controls);

        mImageViewList = new ArrayList<ImageView>();
        ImageView imageView = (ImageView) findViewById(R.id.fullscreen_ad_1);
        imageView.setVisibility(View.GONE);
        imageView.setOnClickListener(mImageViewClickListerner);
        mImageViewList.add(imageView);
        imageView = (ImageView) findViewById(R.id.fullscreen_ad_2);
        imageView.setVisibility(View.GONE);
        imageView.setOnClickListener(mImageViewClickListerner);
        mImageViewList.add(imageView);
        imageView = (ImageView) findViewById(R.id.fullscreen_ad_3);
        imageView.setVisibility(View.GONE);
        imageView.setOnClickListener(mImageViewClickListerner);
        mImageViewList.add(imageView);
        currentImageViewIndex = 0;

        mImageThread = new Thread(mImageRunnable);

        registerBroadcastReceiver();

        // Start SocketClientService.
        Intent socketServiceIntent = new Intent(this, SocketClientService.class);
        startService(socketServiceIntent);
        // Start ServerApiService.
        Intent serverApiIntent = new Intent(this, ServerApiService.class);
        startService(serverApiIntent);
        doBindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        hide();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mImageThread.isAlive()) {
            mImageThread.start();
        }
        isActive = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isActive = false;
    }

    private void doBindService() {
        bindService(new Intent(AdsActivity.this, ServerApiService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.BROADCAST_ACTION_SOCKET);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!isActive) {
                    return;
                }
                ServerMessageParser messageParser = new ServerMessageParser(intent);
                if (messageParser.shouldSayHi()) {
                    Log.d(TAG, "Say hi!");
                    // 吃惊
                    mServerApiService.expression(1, 3000, 1);
                    mServerApiService.speak("你好", 1);
                    mServerApiService.armUpDown(2);
                    return;
                }
                if (messageParser.shouldGoMainActivity()) {
                    Log.d(TAG, "Go to MainActivity!");
                    startMainActivity();
                    // 大笑
                    mServerApiService.expression(2, 3000, 1);
                    mServerApiService.speak("欢迎使用银行机器人", 1);
                    mServerApiService.armsDown();
                    return;
                }
            }
        };
        registerReceiver(mReceiver, intentFilter);
    }

    private void nextImageView() {
        ImageView imageView = mImageViewList.get(currentImageViewIndex);
        imageView.setVisibility(View.GONE);

        currentImageViewIndex = (currentImageViewIndex + 1) % mImageViewList.size();
        imageView = mImageViewList.get(currentImageViewIndex);
        imageView.setVisibility(View.VISIBLE);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
    }

    private void startMainActivity() {
        Log.d(TAG, "startMainActivity");
        Intent intent = new Intent(AdsActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
