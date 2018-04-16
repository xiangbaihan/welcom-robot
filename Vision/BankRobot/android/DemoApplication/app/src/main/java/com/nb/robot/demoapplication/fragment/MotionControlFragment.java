package com.nb.robot.demoapplication.fragment;

import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.nb.robot.demoapplication.Consts;
import com.nb.robot.demoapplication.HttpClientConnector;
import com.nb.robot.demoapplication.R;
import com.nb.robot.demoapplication.SocketClient;
import com.nb.robot.demoapplication.adapter.ReceiverAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MotionControlFragment extends Fragment implements View.OnClickListener {
    private static final String TAG="MotionControlFragment";
    private TextView value_XYTH;
    private Button btn_motion,btn_reset,btn_led;
    private EditText et_lineSpeed,et_angularSpeed,et_duration,et_led;

    private ReceiverAdapter receiverAdapter;
    private static Handler messageHandler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_motion, container, false);
        et_lineSpeed = (EditText) view.findViewById(R.id.motion_lineSpeed);
        et_angularSpeed = (EditText) view.findViewById(R.id.motion_angularSpeed);
        et_duration = (EditText) view.findViewById(R.id.motion_duration);
        et_led = (EditText) view.findViewById(R.id.et_led);

        value_XYTH= (TextView) view.findViewById(R.id.tv_XYTH);
        value_XYTH.setMovementMethod(ScrollingMovementMethod.getInstance());//设置textview滚动 scrollerBar

        btn_reset= (Button) view.findViewById(R.id.button_reset);
        btn_reset.setOnClickListener(this);
        btn_motion = (Button) view.findViewById(R.id.button_motion);
        btn_motion.setOnClickListener(this);
        btn_led = (Button) view.findViewById(R.id.button_led);
        btn_led.setOnClickListener(this);

        messageHandler=new Handler(new Handler.Callback() {
           @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        value_XYTH.append(System.lineSeparator()+msg.obj.toString());
                        Log.e(TAG,msg.obj.toString());
                        String message=msg.obj.toString();
                        String[] array=message.split(",");
                        Log.e(TAG,"array 1"+array[0]+"--array[1]"+array[1]+"--array[2]"+array[2]);
                        value_XYTH.append("X: "+array[0]+";\n");
                        value_XYTH.append("Y: "+array[1]+";\n");
                        value_XYTH.append("TH: "+array[2]+";\n");
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_motion:

                Map<String, Integer> params = new HashMap<>();
                params.put("motionLineSpeed", Integer.valueOf(et_lineSpeed.getText().toString()));
                params.put("motionAngularSpeed", Integer.valueOf(et_angularSpeed.getText().toString()));
                params.put("motionDuration", Integer.valueOf(et_duration.getText().toString()));
                JSONObject obj = new JSONObject(params);

                Log.e("post json 参数 --- ",obj.toString());
                HttpClientConnector connector_motion = new HttpClientConnector();
                connector_motion.execute("post",Consts.MOTION_CONTROL, obj.toString());
                break;
            case R.id.button_reset:
               // connector.execute("get",Consts.MOTION_RESET);
                break;
            case R.id.button_led:
                Map<String, Integer> params_led = new HashMap<>();
                params_led.put("ledState", Integer.valueOf(et_led.getText().toString()));

                JSONObject obj_led = new JSONObject(params_led);

                Log.e("post json 参数 --- ",obj_led.toString());
                HttpClientConnector connector_led = new HttpClientConnector();
                connector_led.execute("post",Consts.LED_CONTROL, obj_led.toString());
                break;
        }
    }

}
