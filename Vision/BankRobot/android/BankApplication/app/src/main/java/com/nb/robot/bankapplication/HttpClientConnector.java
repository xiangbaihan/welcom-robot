package com.nb.robot.bankapplication;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nb.robot.bankapplication.bean.HumanDetectionBean;
import com.nb.robot.bankapplication.bean.UtilStatusBean;

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
    private static final String TAG = HttpClientConnector.class.getName();
    private final String twoHyphens = "--"; // 前缀
    private final String LINE_END = "\r\n"; // 换行
    private final String BOUNDARY = UUID.randomUUID().toString(); // 边界标识

    protected String doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        String requestMethod = params[0].toString();//http post or get
        String requestUrl = params[1].toString();
        try {
            URL url = new URL(requestUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            if (requestMethod.trim().toLowerCase().equals("post")) {
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                if (params[2] != null) {
                    PrintWriter writer = new PrintWriter(urlConnection.getOutputStream());
                    writer.write(params[2].toString());
                    writer.flush();
                    writer.close();
                }
            } else {
                urlConnection.setRequestMethod("GET");
//              urlConnection.setRequestProperty("Accept","text/plain");
                urlConnection.setRequestProperty("Accept", "application/json");
            }
            int statusCode = urlConnection.getResponseCode();
            Log.d(TAG, "statusCode: " + statusCode);
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

        if (content == null) {
            Log.i(TAG, "No Http response.");
            return;
        }
        // TODO: display error message in case of errors.
        Gson gson = new Gson();
        UtilStatusBean utilStatus =
                gson.fromJson(content, new TypeToken<UtilStatusBean>(){}.getType());
        if (utilStatus!=null) {
            Log.i(TAG, "status:" + utilStatus.toString());
        } else {
            Log.i(TAG, "Http response:" + content);
        }
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
