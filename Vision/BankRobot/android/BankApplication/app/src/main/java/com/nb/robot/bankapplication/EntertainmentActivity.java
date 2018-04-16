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
import android.widget.Toast;

import java.util.Random;

/**
 * Entertainment UI.
 */
public class EntertainmentActivity extends AppCompatActivity {
    private static final String TAG = EntertainmentActivity.class.getName();

    private BroadcastReceiver mReceiver;
    private boolean isActive = false;
    private Random random = new Random();

    private ServerApiService mServerApiService;
    boolean mIsBound = false;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mServerApiService = ((ServerApiService.LocalBinder)service).getService();
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

        setContentView(R.layout.activity_entertainment);

        findViewById(R.id.buttonBackMain_Ent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtils.startMainActivity(EntertainmentActivity.this);
            }
        });
        findViewById(R.id.buttonDance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Type range: 1 ~ 3.
                int danceType = random.nextInt(3) + 1;
                mServerApiService.dance(danceType);
            }
        });
        findViewById(R.id.buttonSmile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Type range: 1 ~ 9.
                int emotionType = random.nextInt(9) + 1;
                mServerApiService.expression(emotionType, 5000, 1);
            }
        });

        registerBroadcastReceiver();
        doBindService();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        hide();
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
    public void onResume() {
        super.onResume();
        isActive = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isActive = false;
    }


    private void doBindService() {
        bindService(new Intent(EntertainmentActivity.this, ServerApiService.class), mConnection, Context.BIND_AUTO_CREATE);
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
                if (messageParser.shouldGoIdle()) {
                    ActivityUtils.startAdsActivity(EntertainmentActivity.this);
                    return;
                }
                if (messageParser.shouldSayGoodbye()) {
                    // 道别
                    mServerApiService.expression(3, 3000, 1);
                    mServerApiService.speak(Constants.SPEAKER_GOODBYE, 1);
                    return;
                }
                if (!messageParser.isSpeechRecognitionMessage()) {
                    return;
                }
                if (messageParser.isSpeechRecognized()) {
                    if (messageParser.isSpeechPattern(Constants.SPEECH_KEYWORD_DANCE)) {
                        // Type range: 1 ~ 3.
                        int danceType = random.nextInt(3) + 1;
                        mServerApiService.dance(danceType);
                    } else if (messageParser.isSpeechPattern(Constants.SPEECH_KEYWORD_SMILE)) {
                        // Type range: 1 ~ 9.
                        int emotionType = random.nextInt(9) + 1;
                        mServerApiService.expression(emotionType, 5000, 1);
                    } else if (messageParser.isSpeechPattern(Constants.SPEECH_KEYWORD_RETURN)) {
                        ActivityUtils.startMainActivity(EntertainmentActivity.this);
                    }
                } else {
                    Toast.makeText(EntertainmentActivity.this, R.string.speech_unrecognized, Toast.LENGTH_SHORT).show();
                }

            }
        };
        registerReceiver(mReceiver, intentFilter);
    }

    private void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
}
