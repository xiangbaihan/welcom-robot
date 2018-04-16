package com.nb.robot.service;

import java.util.List;

import com.nb.robot.common.Pair;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//Response for speech recognition.
@XmlRootElement
public class SpeechRecognitionResponse {
    private String fullText;
    List<Pair<String, String>> keywords;
    // If true, fullText and keywords must be set; if false， fullText and keywords are empty,
    // meaning there is some voice detected but no confident recognition result. 
    private boolean isRecognized;
    private long timestamp;
    private int score;

    public SpeechRecognitionResponse(String fullText, List<Pair<String, String>> keywords,
    		boolean isRecognized, int score, long timestamp) {
    	this.fullText = fullText;
    	this.keywords = keywords;
    	this.isRecognized = isRecognized;
    	this.timestamp = timestamp;
    	this.score = score;
    }

    @XmlElement(name = "fullText")
    public String getFullText() {
        return fullText;
    }

    @XmlElement(name = "fullText")
    public List<Pair<String, String>> getKeywords() {
        return keywords;
    }

    @XmlElement(name = "isRecognized")
    public boolean getIsRecognized() {
        return isRecognized;
    }

    @XmlElement(name = "timestamp")
    public long getTimeStamp() {
        return timestamp;
    }
        
    public int getScore() {
        return score;
    }

}
