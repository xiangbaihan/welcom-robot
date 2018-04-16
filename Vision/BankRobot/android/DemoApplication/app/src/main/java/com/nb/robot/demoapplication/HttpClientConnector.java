package com.nb.robot.demoapplication;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;


public class HttpClientConnector extends AsyncTask<String, Void, String> {

    private static final String TAG = "HttpClientConnector";
    private final String twoHyphens = "--"; // 前缀
    private final String LINE_END = "\r\n"; // 换行
    private final String BOUNDARY = UUID.randomUUID().toString(); // 边界标识

    protected String doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        String requestMethod=params[0].toString();//http post or get
        String requestUrl=params[1].toString();
        try {
            URL url = new URL(requestUrl);
            urlConnection= (HttpURLConnection) url.openConnection();
            if (requestMethod.trim().toLowerCase().equals("post")) {
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
                urlConnection.setRequestProperty("Accept","application/json");
                urlConnection.setDoOutput(true);
                if(params[2]!=null) {//这里？？？？？？
                    PrintWriter writer = new PrintWriter(urlConnection.getOutputStream());
                    writer.write(params[2].toString());
                    writer.flush();
                    writer.close();
                }
            } else if(requestMethod.trim().toLowerCase().equals("upload")){
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");/* 设置传送的method=POST */
                urlConnection.setRequestProperty("Charset", "utf-8"); /* setRequestProperty */
                // 设置内容类型及定义BOUNDARY
                urlConnection.setRequestProperty("Content-Type", "multipart/form-data"
                        + ";boundary=" + BOUNDARY);
                boolean sdCardExist = Environment.getExternalStorageState()
                        .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
                FileInputStream fs = new FileInputStream(new File(Environment.getExternalStorageDirectory()+"/"+params[2]));
                Log.d(TAG,Environment.getExternalStorageDirectory()+"/"+params[2]);
                DataOutputStream ds = new DataOutputStream(urlConnection.getOutputStream());
                ds.writeBytes(twoHyphens+BOUNDARY+LINE_END);
                ds.writeBytes("Content-Disposition: form-data; "
                        + "name=\"file\";filename=\"" + params[2]
                        + "\"" + LINE_END);
                ds.writeBytes(LINE_END);
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                int length = -1;
                 /* 从文件读取数据至缓冲区 */
                while ((length = fs.read(buffer)) != -1) {/* 将资料写入DataOutputStream中 */
                    ds.write(buffer, 0, length);
                }
                ds.writeBytes(LINE_END);
                ds.writeBytes(twoHyphens + BOUNDARY + twoHyphens + LINE_END);
                /* close streams */
                fs.close();
                ds.flush();

            } else {
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Accept","application/json");
            }
            int statusCode = urlConnection.getResponseCode();
            Log.e(TAG, "statusCode: " + statusCode);
             /* 200 represents HTTP OK */
            if (statusCode == 200) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                return readStream(in);
            }

        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        } finally {
            urlConnection.disconnect();
        }
        return "NULL";
    }

    protected void onPostExecute(String content) {
        Log.e(TAG, content);
    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }



}
