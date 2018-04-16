package com.nb.robot.bankapplication.bean;

import java.util.List;

public class SpeechRecognitionBean {

    public String fullText;
    public List<SpeechRecognitionKeyword> keywords;
    public boolean isRecognized;
    public long timestamp;

    public String getFullText() {
        return fullText;
    }

    public List<SpeechRecognitionKeyword> getKeywords() {
        return keywords;
    }

    public boolean isRecognition() {
        return isRecognized;
    }

    public long getTimestamp() {
        return timestamp;
    }

}

