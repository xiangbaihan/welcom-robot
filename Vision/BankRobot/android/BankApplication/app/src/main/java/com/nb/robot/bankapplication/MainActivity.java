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

/**
 * Main user operation UI.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private static final int LOGIN_RET_CODE = 1;
    private BroadcastReceiver mReceiver;
    private boolean isActive = false;

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

        setContentView(R.layout.activity_main);

        findViewById(R.id.buttonBankServices).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, LOGIN_RET_CODE);
            }
        });

        findViewById(R.id.buttonEntertainments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EntertainmentActivity.class);
                startActivity(intent);
            }
        });

        registerBroadcastReceiver();
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
        isActive = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isActive = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(TAG, "requestCode: " + requestCode + " resultCode: " + resultCode);
        if (requestCode == LOGIN_RET_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "starting BankServiceActivity");
                Intent intent = new Intent(MainActivity.this, BankServiceActivity.class);
                startActivity(intent);
            }
        }
    }

    private void doBindService() {
        bindService(new Intent(MainActivity.this, ServerApiService.class), mConnection, Context.BIND_AUTO_CREATE);
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
                    ActivityUtils.startAdsActivity(MainActivity.this);
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
                    if (messageParser.isSpeechPattern(Constants.SPEECH_KEYWORD_BANK_SERVICE)) {
                        Log.i(TAG,"MainActivity-进入银行业务！！！");
                        Intent new_intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(new_intent);
                    } else if (messageParser.isSpeechPattern(Constants.SPEECH_KEYWORD_ENTERTAINMENT)) {
                        Log.i(TAG,"MainActivity-进入娱乐模式！！！");
                        Intent new_intent = new Intent(MainActivity.this, EntertainmentActivity.class);
                        startActivity(new_intent);
                    }
                } else {
                    Log.i(TAG,"MainActivity-无法识别语音！！！");
                    Toast.makeText(MainActivity.this, R.string.speech_unrecognized, Toast.LENGTH_SHORT).show();
                }
            }
        };
        registerReceiver(mReceiver, intentFilter);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void startAdsActivity() {
        Log.d(TAG, "startAdsActivity");
        Intent intent = new Intent(MainActivity.this, AdsActivity.class);
        startActivity(intent);
    }
}
