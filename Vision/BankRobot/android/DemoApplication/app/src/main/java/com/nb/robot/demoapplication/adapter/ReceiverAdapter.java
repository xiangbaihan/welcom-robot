package com.nb.robot.demoapplication.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

/**
 * Created by mj on 17-8-4.
 */

public class ReceiverAdapter extends BroadcastReceiver{

    private static final String TAG = "ReceiverAdapter";

    private Handler handler;
    public  ReceiverAdapter(Handler handler){
        this.handler=handler;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = intent.getExtras().getString("msg");
        Log.e(TAG,msg);
        try{
            JSONObject jsonObject = new JSONObject(msg);
            int what = jsonObject.getInt("type");
            Message message = handler.obtainMessage(what,msg);
            handler.sendMessage(message);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

}
