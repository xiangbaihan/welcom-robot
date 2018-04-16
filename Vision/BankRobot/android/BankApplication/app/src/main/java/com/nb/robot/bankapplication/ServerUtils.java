package com.nb.robot.bankapplication;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

// Utility methods for interacting with Bank Server. Please use methods in ServerApiService to
// access these Server APIs from activity.
public class ServerUtils {
    private static final String TAG = ServerUtils.class.getName();

    // Returns full URI for a given API.
    static private String getServerURI(String apiUri) {
        Log.d(TAG, "http://" + Constants.SERVER_IP_ADDRESS + ":" + Constants.SERVER_PORT_SERVICE + "/" + apiUri);
        return "http://" + Constants.SERVER_IP_ADDRESS + ":" + Constants.SERVER_PORT_SERVICE + "/" + apiUri;
    }

    static void speak(String content, int repeat) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("content", content);
            jsonObject.put("speed", Constants.SPEAKER_SPEED);
            jsonObject.put("volume", Constants.SPEAKER_VOLUME);
            jsonObject.put("pitch", Constants.SPEAKER_PITCH);
            jsonObject.put("repeat", repeat);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, jsonObject.toString());
        HttpClientConnector connector = new HttpClientConnector();
        connector.execute("post", getServerURI(Constants.SPEAKER_TALK_URI), jsonObject.toString());
    }

    static void expression(int emotionId, long duration, int repeat) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("emotion", emotionId);
            jsonObject.put("emotionDuration", duration);
            jsonObject.put("emotionRepeat", repeat);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, jsonObject.toString());
        HttpClientConnector connector = new HttpClientConnector();
        connector.execute("post", getServerURI(Constants.EXPRESSION_DYNAMIC_URI), jsonObject.toString());
    }

    static void dance(int danceType) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", danceType);
            jsonObject.put("repeat", 1);
            jsonObject.put("duration", 0);
            jsonObject.put("muteFlag", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, jsonObject.toString());
        HttpClientConnector connector = new HttpClientConnector();
        connector.execute("post", getServerURI(Constants.DANCE_URI), jsonObject.toString());
    }

    // Controls arm up and down.
    // armPart: 1-左手臂，2-右手臂，<=0或>=3-两只手臂
    static void armUpDown(int armPart) {
        JSONObject jsonObject_up = new JSONObject();
        try {
            jsonObject_up.put("armPart", armPart);
            jsonObject_up.put("armPosition", 100);
            jsonObject_up.put("armSpeed", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, jsonObject_up.toString());
        HttpClientConnector connector_up = new HttpClientConnector();
        connector_up.execute("post", getServerURI(Constants.ARM_URI), jsonObject_up.toString());

        // Put arm down after 5s.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject_dowm = new JSONObject();
        try {
            jsonObject_dowm.put("armPart", armPart);
            jsonObject_dowm.put("armPosition", 135);
            jsonObject_dowm.put("armSpeed", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, jsonObject_dowm.toString());
        HttpClientConnector connector_down = new HttpClientConnector();
        connector_down.execute("post", getServerURI(Constants.ARM_URI), jsonObject_dowm.toString());
    }

    // Puts both arms down.
    static void armsDown() {
        //左臂
        JSONObject jsonObject_left = new JSONObject();
        try {
            jsonObject_left.put("armPart", 1);
            jsonObject_left.put("armPosition", 180);
            jsonObject_left.put("armSpeed", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, jsonObject_left.toString());
        HttpClientConnector connector_left = new HttpClientConnector();
        connector_left.execute("post", getServerURI(Constants.ARM_URI), jsonObject_left.toString());

        //右臂。手臂存在~50度的误差。
        JSONObject jsonObject_right = new JSONObject();
        try {
            jsonObject_right.put("armPart", 2);
            jsonObject_right.put("armPosition", 135);
            jsonObject_right.put("armSpeed", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, jsonObject_right.toString());
        HttpClientConnector connector_right = new HttpClientConnector();
        connector_right.execute("post", getServerURI(Constants.ARM_URI), connector_right.toString());

    }
}
