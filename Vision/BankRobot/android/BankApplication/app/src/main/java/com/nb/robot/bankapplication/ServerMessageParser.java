package com.nb.robot.bankapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nb.robot.bankapplication.bean.HumanDetectionBean;
import com.nb.robot.bankapplication.bean.SpeechRecognitionBean;
import com.nb.robot.bankapplication.bean.SpeechRecognitionKeyword;

import org.json.JSONException;
import org.json.JSONObject;

public class ServerMessageParser {
    private static final String TAG = ServerMessageParser.class.getName();
    // 2: human detection; 3: speech recognition.
    private int message_type;
    // Human detection result.
    private HumanDetectionBean humanDetectionBean;
    // Speech recognition result.
    private SpeechRecognitionBean speechRecognitionBean;

    public ServerMessageParser(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return;
        }
        message_type = 0;
        if (extras.containsKey(Constants.BROADCAST_INTENT_SOCKET)) {
            parseSocketMessage(extras.getString(Constants.BROADCAST_INTENT_SOCKET));
        }
    }

    private void parseSocketMessage(String message) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(message);
            if (!jsonObject.has("type")) {
                return;
            }
            message_type = jsonObject.getInt("type");
            switch (message_type) {
                case 2: {
                    Gson gson = new Gson();
                    humanDetectionBean =
                            gson.fromJson(message, new TypeToken<HumanDetectionBean>(){}.getType());
                    break;
                }
                case 3: {
                    Gson gson = new Gson();
                    speechRecognitionBean =
                            gson.fromJson(message, new TypeToken<SpeechRecognitionBean>(){}.getType());
                    break;
                }
                default: break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isHumanDetectionMessage() {
        return message_type == 2;
    }

    // Whether to go into idle mode because no humans are present.
    public boolean shouldGoIdle() {
        return message_type == 2 && humanDetectionBean.getState() == 0;
    }

    // Whether to say hi to users when they approach to the robot.
    public boolean shouldSayHi() {
        return message_type == 2 && humanDetectionBean.getState() == 1;
    }

    // Whether to go to MainActivity when user is in front of the robot.
    public boolean shouldGoMainActivity() {
        return message_type == 2 && humanDetectionBean.getState() == 2;
    }

    // Whether to say goodbye to users when they leave the robot.
    public boolean shouldSayGoodbye() {
        return message_type == 2 && humanDetectionBean.getState() == 3;
    }

    public HumanDetectionBean getHumanDetectionBean() {
        return humanDetectionBean;
    }

    public boolean isSpeechRecognitionMessage() {
        return message_type == 3;
    }

    public SpeechRecognitionBean getSpeechRecognitionBean() {
        return speechRecognitionBean;
    }

    // Whether some speech was recognized.
    public boolean isSpeechRecognized() {
        if (message_type != 3) {
            return false;
        }
        SpeechRecognitionBean bean = getSpeechRecognitionBean();
        Log.i(TAG,"获得bean.isRegognized="+bean.isRecognized);
        return bean.isRecognized;
    }

    // Whether the given pattern is recognized. Pattern format: <keyword1>,<keyword2>
    public boolean isSpeechPattern(String pattern) {
        if (!isSpeechRecognized()) {
            return false;
        }
        SpeechRecognitionBean bean = getSpeechRecognitionBean();
        String[] expectedKeywords = pattern.split(",");
        if (expectedKeywords.length == 0) {
            return false;
        }
        boolean isFound = false;//对比获取的语音关键字是否相等
        for (String expectedKeyword : expectedKeywords) {
            for (SpeechRecognitionKeyword recognizedKeyword : bean.getKeywords()) {
                if (recognizedKeyword.getWord().equals(expectedKeyword)) {
                    isFound = true;
                    return isFound;
                }
            }
            if (!isFound) {
                return false;
            }
        }
        return isFound;
    }
}
