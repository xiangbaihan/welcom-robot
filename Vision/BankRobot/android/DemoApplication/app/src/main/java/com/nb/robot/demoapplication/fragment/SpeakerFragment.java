package com.nb.robot.demoapplication.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nb.robot.demoapplication.Consts;
import com.nb.robot.demoapplication.HttpClientConnector;
import com.nb.robot.demoapplication.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by roboot on 17-8-24.
 */

public class SpeakerFragment extends Fragment implements OnClickListener{

    private TextView speaker,tv_content,tv_speed,tv_volume,tv_pitch,tv_repeat;
    private EditText et_content_value,et_speed_value,et_volume_value,et_pitch_value,et_repeat_value;
    private Button btn_sendSpeaker;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_speaker,container,false);

        speaker = (TextView) view.findViewById(R.id.speaker);

        tv_content = (TextView) view.findViewById(R.id.tv_content);
        et_content_value = (EditText) view.findViewById(R.id.et_content_value);
        et_content_value.setText("您好，欢迎使用银行机器人！");

        tv_speed = (TextView) view.findViewById(R.id.tv_speed);
        et_speed_value = (EditText) view.findViewById(R.id.et_speed_value);


        tv_volume = (TextView) view.findViewById(R.id.tv_volume);
        et_volume_value = (EditText) view.findViewById(R.id.et_volume_value);

        tv_pitch = (TextView) view.findViewById(R.id.tv_pitch);
        et_pitch_value = (EditText) view.findViewById(R.id.et_pitch_value);

        tv_repeat = (TextView) view.findViewById(R.id.tv_repeat);
        et_repeat_value = (EditText) view.findViewById(R.id.et_repeat_value);

        et_speed_value.setText("35");
        et_volume_value.setText("50");
        et_pitch_value.setText("50");
        et_repeat_value.setText("1");


        btn_sendSpeaker = (Button) view.findViewById(R.id.btn_send_speaker);
        btn_sendSpeaker.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send_speaker:
                Map<String,Object> params=new HashMap<String, Object>();
                params.put("content",et_content_value.getText().toString());
                params.put("speed",Integer.valueOf(et_speed_value.getText().toString()));
                params.put("volume",Integer.valueOf(et_volume_value.getText().toString()));
                params.put("pitch",Integer.valueOf(et_pitch_value.getText().toString()));
                params.put("repeat",Integer.valueOf(et_repeat_value.getText().toString()));

                JSONObject object = new JSONObject(params);
                Log.e("post json 参数 --- ",object.toString());

                HttpClientConnector connector=new HttpClientConnector();
                connector.execute("post", Consts.SPEAKER_TALK,object.toString());

                break;

        }
    }
}
