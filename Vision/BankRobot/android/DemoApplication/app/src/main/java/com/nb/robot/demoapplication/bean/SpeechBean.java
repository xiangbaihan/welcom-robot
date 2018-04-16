package com.nb.robot.demoapplication.bean;

import java.util.List;

/**
 * Created by mj on 17-8-4.
 */

public class SpeechBean {

    public String fullText;
    public List<Keyword> keywords;
    public boolean isRecognition;
    public long timestamp;

    public String getFullText() {
        return fullText;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public boolean isRecognition() {
        return isRecognition;
    }

    public long getTimestamp() {
        return timestamp;
    }




}
class Keyword{

    public String slot;
    public String word;

    public String getSlot() {
        return slot;
    }

    public String getWord() {
        return word;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Override
    public String toString()
    {

        return "Keyword [slot=" +slot+ ", word=" +word+ "]";
    }
}