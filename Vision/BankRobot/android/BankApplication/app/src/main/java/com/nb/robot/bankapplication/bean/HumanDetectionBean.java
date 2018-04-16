package com.nb.robot.bankapplication.bean;

import java.util.List;

public class HumanDetectionBean {
    // 0: No humans. Default.
    // 1: Humans appear in the large range.
    // 2: Humans appear in the small range.
    // 3: Humans appear in the large range.
    public int state;
    public int number;
    public long timestamp;

    public int getNumber() {
        return number;
    }

    public int getState() {
        return state;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
