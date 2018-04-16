package com.nb.robot.demoapplication;


import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.nb.robot.demoapplication.adapter.fragmentAdapter;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private fragmentAdapter adapter;

    private static Handler messageHandler;
    private SocketClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        viewPager= (ViewPager) findViewById(R.id.vp_view);
        tabLayout= (TabLayout) findViewById(R.id.tabs);
        adapter=new fragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


        messageHandler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Intent intent = new Intent();
                intent.setAction(Consts.BROADCAST_ACTION);
                intent.putExtra("msg",msg.obj.toString());//只获取msg的obj参数
                sendBroadcast(intent);
                //Toast.makeText(getApplicationContext(), "发送广播成功", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        //start thread to receive msaasges from Server
        final Thread thread=new Thread(){
            @Override
            public void run() {
                try {
                    client = new SocketClient(messageHandler);
                    client.connect(9090);
                    client.readMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

}
