package com.nb.robot.demoapplication;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by linshangjun on 2017/7/7.
 */

public class SocketClient{
    private static final String TAG="SocketClient";

    private Handler messageHandler;
    private Socket socket;
    private static final String SERVER_IP="192.168.1.110";//"192.168.1.100";
    private ExecutorService executorService;
    private  JSONObject json;

    public SocketClient(Handler handler) throws IOException {
        this.messageHandler=handler;

        socket=new Socket();
        executorService= Executors.newFixedThreadPool(5);
    }

    public void connect(int port) throws IOException {
        InetSocketAddress isa=new InetSocketAddress(SERVER_IP,port);
        socket.connect(isa);
    }

    public void sendMessage(String message){
        executorService.execute(new sendMessageTask(message));
    }

    public void readMessage(){
        executorService.execute(new readMessageTask());
    }

    public class readMessageTask implements Runnable{

        @Override
        public void run() {
            Log.e("listening Message --- ","监听服务器，等待接收数据。。。");

            try{
                String index;
                BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while ((index=reader.readLine())!=null){
//                    Log.e("reading Message --- ", "客户端接收到来自服务器端 [ " + socket.getRemoteSocketAddress() + " ] 的数据 ---- "
//                           + index + " , 数据长度 = " + index.length());
                    // TODO: 对接收的数据index进行处理操作。这里将字符串呈现在界面的textView上
                     Log.e(TAG,"---"+index);
                     int type;

                     if(index.indexOf("type")==-1){//indexOf()没有到匹配字符串返回-1
                          type = 1;
                      }else{
                          json = new JSONObject(index);
                          type = json.getInt("type");
                      }
                    Log.e(TAG,"---"+type);
                    Message msg=messageHandler.obtainMessage(type,index);
                    messageHandler.sendMessage(msg);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class sendMessageTask implements Runnable{
        private String message;

        public sendMessageTask(String msg){
            message=msg;
        }

        @Override
        public void run() {
            try {
                OutputStream outputStream=socket.getOutputStream();
                PrintWriter writer=new PrintWriter(outputStream);

                writer.println(message);
                writer.flush();

                Log.e("sendMessage Task --- "," msg = "+message);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}