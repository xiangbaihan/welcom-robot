package com.nb.robot.demoapplication.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.nb.robot.demoapplication.Consts;
import com.nb.robot.demoapplication.HttpClientConnector;
import com.nb.robot.demoapplication.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServoFragment extends Fragment {

    private EditText et_part,et_position,et_speed;
    private Button btn_servo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_servo, container, false);

        et_part= (EditText) view.findViewById(R.id.et_part);
        et_position= (EditText) view.findViewById(R.id.et_position);
        et_speed= (EditText) view.findViewById(R.id.et_speed);
        btn_servo= (Button) view.findViewById(R.id.btn_servo);

        et_part.setText("3");
        et_position.setText("240");
        et_speed.setText("3");

        btn_servo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Integer> params=new HashMap<String, Integer>();
                params.put("armPart",Integer.valueOf(et_part.getText().toString()));
                params.put("armPosition",Integer.valueOf(et_position.getText().toString()));
                params.put("armSpeed",Integer.valueOf(et_speed.getText().toString()));

                JSONObject object = new JSONObject(params);

                Log.e("post json 参数 --- ",object.toString());
                HttpClientConnector connector=new HttpClientConnector();
                connector.execute("post", Consts.ARM_CONTROL,object.toString());
            }
        });

        return view;
    }

}
