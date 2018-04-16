package com.nb.robot.demoapplication.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
 * Created by roboot on 17-7-17.
 */

public class ExpressionFragment extends Fragment {

    private TextView tv_First,tv_Two,tv_Three,tv_Four,tv_Five,tv_Six,tv_Seven,tv_Eight,tv_Nine;
    private EditText et_staticEmotion,et_dynamicEmotion,et_emotionDuration,et_emotionRepeat;
    private Button btn_Emotion;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_expression, container, false);
        //初始化
        tv_First = (TextView) view.findViewById(R.id.tv_first);
        tv_Two= (TextView) view.findViewById(R.id.tv_two);
        tv_Three = (TextView) view.findViewById(R.id.tv_three);
        tv_Four = (TextView) view.findViewById(R.id.tv_four);
        tv_Five = (TextView) view.findViewById(R.id.tv_five);
        tv_Six = (TextView) view.findViewById(R.id.tv_six);
        tv_Seven = (TextView) view.findViewById(R.id.tv_seven);
        tv_Eight = (TextView) view.findViewById(R.id.tv_eight);
        tv_Nine = (TextView) view.findViewById(R.id.tv_nine);
        et_staticEmotion = (EditText) view.findViewById(R.id.et_staticEmotion);
        et_dynamicEmotion = (EditText) view.findViewById(R.id.et_dynEmotion);
        et_emotionDuration =(EditText) view.findViewById(R.id.et_emotionDuration);
        et_emotionRepeat = (EditText) view.findViewById(R.id.et_emotionRepeat);
        btn_Emotion = (Button) view.findViewById(R.id.btn_emotion);

        et_staticEmotion.setText("5");

        btn_Emotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Integer> params=new HashMap<String, Integer>();
                if(et_staticEmotion.getText().toString().length()!=0){
                    params.put("emotion",Integer.valueOf(et_staticEmotion.getText().toString()));

                    JSONObject object = new JSONObject(params);

                    Log.e("post json 参数 --- ",object.toString());
                    HttpClientConnector connector=new HttpClientConnector();
                    connector.execute("post", Consts.STATIC_EXPRESSION_CONTROL,object.toString());

                }else{
                    params.put("emotion",Integer.valueOf(et_dynamicEmotion.getText().toString()));
                    params.put("emotionDuration",Integer.valueOf(et_emotionDuration.getText().toString()));
                    params.put("emotionRepeat",Integer.valueOf(et_emotionRepeat.getText().toString()));

                    JSONObject object = new JSONObject(params);
                    Log.e("post json 参数 --- ",object.toString());
                    HttpClientConnector connector=new HttpClientConnector();
                    connector.execute("post", Consts.DYNAMIC_EXPRESSION_CONTROL,object.toString());
                }


            }
        });


        return view;
    }
}
