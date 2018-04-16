package com.nb.robot.demoapplication.fragment;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import com.nb.robot.demoapplication.Consts;
import com.nb.robot.demoapplication.HttpClientConnector;
import com.nb.robot.demoapplication.R;
import com.nb.robot.demoapplication.SocketClient;
import com.nb.robot.demoapplication.adapter.ReceiverAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by roboot on 17-7-28.
 */

public class HumanDectionFragment extends Fragment {

    private TextView tv_showInfo,tv_history;
    private Date date;
    private SimpleDateFormat simpleDateFormat;

    private ReceiverAdapter receiverAdapter;
    private Handler messagehandler;

    private static final String[] humanState = {"没有人","人走进","人在面前","人走远"};

    // send msg params
    private Map<String,Object> params;
    private HttpClientConnector connector;
    //get msg params
    private JSONObject jsonObject,object;
    private int number,state;
    private long timestamp;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_face, container, false);

        tv_showInfo = (TextView) view.findViewById(R.id.tv_show);
        tv_history = (TextView) view.findViewById(R.id.tv_history);
        tv_history.setMovementMethod(ScrollingMovementMethod.getInstance());//scrollerBar


        messagehandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 2:
                        String msgStr = msg.obj.toString();
                        try {
                            jsonObject = new JSONObject(msgStr);
                            number = jsonObject.getInt("number");
                            state = jsonObject.getInt("state");
                            timestamp = jsonObject.getLong("timestamp");

                            date = new Date(timestamp);
                            try {
                                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            tv_history.append("当前人脸数目:"+number+";\n");
                            tv_history.append("人物状态:"+humanState[state]+";\n");
                            tv_history.append("时间:"+simpleDateFormat.format(date)+";\n");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }

            }
        };

        receiverAdapter  = new ReceiverAdapter(messagehandler);

        //register BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Consts.BROADCAST_ACTION);
        getActivity().registerReceiver(receiverAdapter,intentFilter);


        return view;
    }

}
