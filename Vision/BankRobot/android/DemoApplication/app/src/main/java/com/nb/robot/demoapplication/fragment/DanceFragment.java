package com.nb.robot.demoapplication.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.nb.robot.demoapplication.Consts;
import com.nb.robot.demoapplication.HttpClientConnector;
import com.nb.robot.demoapplication.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by roboot on 17-8-22.
 */

public class DanceFragment extends Fragment implements View.OnClickListener,AdapterView.OnItemSelectedListener{

    private static final String TAG="DanceFragment";

    private TextView tv_dance,tv_dance_time,tv_unit,tv_dance_repeat;
    private EditText et_dance_time,et_dance_repeat;
    private Spinner spinner_dance;
    private Button btnStop,btnSilent,btnStart;

    private List<String> list = new ArrayList<String>();
    private ArrayAdapter<String> adapter = null;

    private String[] danceType = {"类型一", "类型二", "类型三"};
    private int  choiceTypeNumber ;//选择了×舞蹈类型
    private boolean music_flag = false;//静音true
    private boolean dance_flag = true;//开始(true)暂停(false)跳舞模块

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dance,container,false);

        tv_dance  = (TextView) view.findViewById(R.id.tv_dance);
        tv_dance_time = (TextView) view.findViewById(R.id.tv_dance_time);
        tv_unit = (TextView) view.findViewById(R.id.tv_unit);
        tv_dance_repeat = (TextView) view.findViewById(R.id.tv_dance_repeat);

        et_dance_time = (EditText) view.findViewById(R.id.et_dance_time);
        et_dance_repeat = (EditText) view.findViewById(R.id.et_dance_repeat);

        btnStart = (Button) view.findViewById(R.id.bt_start);
        btnStart.setOnClickListener(this);

        btnStop = (Button) view.findViewById(R.id.bt_stop);
        btnStop.setOnClickListener(this);

        btnSilent = (Button) view.findViewById(R.id.bt_silent_mode);
        btnSilent.setOnClickListener(this);


        //第一步：添加下拉列表菜单项
        spinner_dance = (Spinner) view.findViewById(R.id.spinner_dance);
        setData();
        //第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list
        adapter = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_spinner_item,list);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        spinner_dance.setAdapter(adapter);
        spinner_dance.setOnItemSelectedListener(this);

        return view;
    }
    //设置下拉列表菜单项
    public void setData() {
        for (int i = 0; i < danceType.length; i++) {
            list.add(i,danceType[i]);
        }
    }

    //第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
           String adapter_value =  adapter.getItem(position);
           choiceTypeNumber =position+1;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.bt_silent_mode:
                music_flag = true;//静音
                Map<String,Object> params_music=new HashMap<String, Object>();
                params_music.put("state",music_flag);

                JSONObject object_music = new JSONObject(params_music);

                Log.e("post json 参数 --- ",object_music.toString());

                HttpClientConnector connector_musicFlag=new HttpClientConnector();
                connector_musicFlag.execute("post", Consts.DANCE_MUSICFLAG,object_music.toString());
                break;
            case R.id.bt_start:
                Map<String,Object> params=new HashMap<String, Object>();
                params.put("type",choiceTypeNumber);
                params.put("repeat",Integer.valueOf(et_dance_repeat.getText().toString()));
                params.put("duration",Integer.valueOf(et_dance_time.getText().toString()));
                params.put("muteFlag",music_flag);

                JSONObject object = new JSONObject(params);

                Log.e("post json 参数 --- ",object.toString());
                HttpClientConnector connector=new HttpClientConnector();
                connector.execute("post", Consts.DANCE_CONTROL,object.toString());
                break;

            case R.id.bt_stop:
                if(dance_flag){
                    dance_flag = false;//暂停
                    btnStop.setText("开始");
                }else{
                    dance_flag = true;//开始
                    btnStop.setText("暂停");
                }

                Map<String,Object> params_dance=new HashMap<String, Object>();
                params_dance.put("state",dance_flag);

                JSONObject object_dance = new JSONObject(params_dance);

                Log.e("post json 参数 --- ",object_dance.toString());

                HttpClientConnector connector_danceFlag=new HttpClientConnector();
                connector_danceFlag.execute("post", Consts.DANCE_DANCEFLAG,object_dance.toString());
                break;
        }
    }
}
