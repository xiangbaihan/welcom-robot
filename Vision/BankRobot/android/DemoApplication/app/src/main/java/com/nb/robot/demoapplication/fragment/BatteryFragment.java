package com.nb.robot.demoapplication.fragment;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.nb.robot.demoapplication.Consts;
import com.nb.robot.demoapplication.HttpClientConnector;
import com.nb.robot.demoapplication.R;
import com.nb.robot.demoapplication.adapter.ReceiverAdapter;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by roboot on 17-11-20.
 */

public class BatteryFragment extends Fragment implements View.OnClickListener,AdapterView.OnItemSelectedListener {
    private static final String TAG="BatteryFragment";
    private TextView show_battery, battery_value;
    private Button getBattery;
    private ReceiverAdapter receiverAdapter;
    private Handler messagehandler;

    private JSONObject jsonObject;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_battery, container, false);
        //初始化
        show_battery = (TextView) view.findViewById(R.id.tv_battery);
        battery_value = (TextView) view.findViewById(R.id.battery_value);

        getBattery = (Button) view.findViewById(R.id.get_battery);
        getBattery.setOnClickListener(this);

        messagehandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 5:
                        String msgStr = msg.obj.toString();
                        try {
                            Log.e(TAG,"接收到了what=5的参数-- ");
                            jsonObject = new JSONObject(msgStr);
                            int soc =  jsonObject.getInt("soc");
                            battery_value.setText(soc + "%");
                            if(soc<=20){
                                battery_value.append("电量过低，请及时充电！！");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };

        receiverAdapter = new ReceiverAdapter(messagehandler);

        //register BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Consts.BROADCAST_ACTION);
        getActivity().registerReceiver(receiverAdapter, intentFilter);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_battery:
                Log.e("get  参数 --- ","get battery vol");

                HttpClientConnector connector=new HttpClientConnector();
                connector.execute("get", Consts.BATTERY_CONTROL);
                Log.e(TAG,"点击按钮发送请求"+Consts.BATTERY_CONTROL);
                break;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
