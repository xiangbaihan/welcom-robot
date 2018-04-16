package com.nb.robot.demoapplication.fragment;


import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nb.robot.demoapplication.HttpClientConnector;
import com.nb.robot.demoapplication.bean.SpeechBean;
import com.nb.robot.demoapplication.Consts;
import com.nb.robot.demoapplication.R;
import com.nb.robot.demoapplication.SocketClient;
import com.nb.robot.demoapplication.adapter.ReceiverAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;



/**
 * Created by roboot on 17-8-1.
 */

public class SpeechFragment extends Fragment implements View.OnClickListener{
    private static final String TAG="SpeechFragment";

    private static Handler messageHandler;
    private TextView tv_speech,tv_speechValue;
    private Button btn_update;
    private SocketClient client;
    private int offset;
    private SpeechBean speechBean;
    private ReceiverAdapter receiverAdapter ;

    private SimpleDateFormat simpleDateFormat;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_speech, container, false);

        tv_speech = (TextView) view.findViewById(R.id.tv_speech);
        tv_speechValue = (TextView) view.findViewById(R.id.tv_speechValue);
        tv_speechValue.setMovementMethod(ScrollingMovementMethod.getInstance());
        btn_update = (Button) view.findViewById(R.id.bt_update);
        btn_update.setOnClickListener(this);

        messageHandler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case 3:
                        String msgStr = msg.obj.toString();
                        Log.i(TAG,msgStr);
                        Gson gson = new Gson();
                        speechBean = gson.fromJson(msgStr,new TypeToken<SpeechBean>(){}.getType());

                        Date date = new Date(speechBean.getTimestamp());
                        try {
                            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        tv_speechValue.append("完整句子："+speechBean.getFullText()+";\n");
                        tv_speechValue.append("关键词:"+speechBean.getKeywords().toString()+";\n");
                        tv_speechValue.append("是否识别:"+speechBean.isRecognition()+";\n");
                        tv_speechValue.append("时间:"+simpleDateFormat.format(date)+";\n");
                        //Scrolling
                        offset=tv_speechValue.getLineCount()*tv_speechValue.getLineHeight();
                        if(offset>tv_speechValue.getHeight()){
                            tv_speechValue.scrollTo(0,offset-tv_speechValue.getHeight());
                        }

                        break;
                          }
                           return true;
                       }
        });

        receiverAdapter  = new ReceiverAdapter(messageHandler);

        //register BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Consts.BROADCAST_ACTION);
        getActivity().registerReceiver(receiverAdapter,intentFilter);

        return view;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.bt_update:

                HttpClientConnector connector=new HttpClientConnector();
                connector.execute("upload",Consts.SPEECH_UPLOAD,"bank2.bnf");

                break;
        }
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
        getActivity().unregisterReceiver(receiverAdapter);
    }
}
