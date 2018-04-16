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
import android.widget.TextView;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class BankServiceActivity extends AppCompatActivity {
    private static final String TAG = BankServiceActivity.class.getName();

    private TextView textViewContent;
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

        setContentView(R.layout.activity_bank_service);

        findViewById(R.id.buttonBackMain_Bank).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtils.startMainActivity(BankServiceActivity.this);
            }
        });

        textViewContent = (TextView)findViewById(R.id.textViewBank);
        textViewContent.setVisibility(View.GONE);
        findViewById(R.id.buttonBalance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewContent.setVisibility(View.VISIBLE);
                textViewContent.setText(R.string.bank_balance_text);
            }
        });
        findViewById(R.id.buttonTransaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewContent.setVisibility(View.VISIBLE);
                textViewContent.setText(R.string.bank_transaction_text);
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
        bindService(new Intent(BankServiceActivity.this, ServerApiService.class), mConnection, Context.BIND_AUTO_CREATE);
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
                    Log.d(TAG, "startAdsActivity");
                    ActivityUtils.startAdsActivity(BankServiceActivity.this);
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
                    if (messageParser.isSpeechPattern(Constants.SPEECH_KEYWORD_BALANCE)) {
                        textViewContent.setVisibility(View.VISIBLE);
                        textViewContent.setText(R.string.bank_balance_text);
                    } else if (messageParser.isSpeechPattern(Constants.SPEECH_KEYWORD_TRANSACTION)) {
                        textViewContent.setVisibility(View.VISIBLE);
                        textViewContent.setText(R.string.bank_transaction_text);
                    } else if (messageParser.isSpeechPattern(Constants.SPEECH_KEYWORD_RETURN)) {
                        ActivityUtils.startMainActivity(BankServiceActivity.this);
                    }
                } else {
                    Toast.makeText(BankServiceActivity.this, R.string.speech_unrecognized, Toast.LENGTH_SHORT).show();
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
