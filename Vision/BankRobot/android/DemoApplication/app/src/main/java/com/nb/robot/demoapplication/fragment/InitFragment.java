package com.nb.robot.demoapplication.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.nb.robot.demoapplication.Consts;
import com.nb.robot.demoapplication.HttpClientConnector;
import com.nb.robot.demoapplication.R;
import com.nb.robot.demoapplication.SocketClient;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InitFragment extends Fragment  implements View.OnClickListener{
    private static final String TAG="InitFragment";

    private  Button bt_connect,bt_start,bt_stop;
    private Map<String,Object> params;
    private JSONObject object;
    private HttpClientConnector connector;
    public InitFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_init, container, false);

        bt_connect = (Button) view.findViewById(R.id.server_Connect);
        bt_connect.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
               // connect server
                connector.execute("get",Consts.SERVER_IP);
                Toast.makeText(getContext(),"connect Server is successed",Toast.LENGTH_SHORT);
            }
        });

        bt_start = (Button) view.findViewById(R.id.moudle_start);
        bt_start.setOnClickListener(this);

        bt_stop = (Button) view.findViewById(R.id.moudle_stop);
        bt_stop.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.moudle_start:
                //send message start HumanDetction and Speech moudle
                params =new HashMap<String, Object>();
                params.put("state",Boolean.valueOf(true));

                object = new JSONObject(params);

                Log.e("post json 参数 --- ",object.toString());
                connector = new HttpClientConnector();
                connector.execute("post",Consts.MOUDLE_CONTROL,object.toString());
                Log.e(TAG,"---start start---");
                break;
            case R.id.moudle_stop:
                //send message stop HumanDetction and Speech moudle
                params = new HashMap<String, Object>();
                params.put("state",Boolean.valueOf(false));

                object = new JSONObject(params);

                Log.e("post json 参数 --- ",object.toString());
                connector = new HttpClientConnector();
                connector.execute("post",Consts.MOUDLE_CONTROL,object.toString());
                Log.e(TAG,"---stop moudle--");
                break;

        }
    }
}
