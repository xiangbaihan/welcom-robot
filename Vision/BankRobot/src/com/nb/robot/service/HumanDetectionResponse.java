package com.nb.robot.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

// Response for human detection.
@XmlRootElement
public class HumanDetectionResponse {
    private int number;
    private int state;
    private long timestamp;

    public HumanDetectionResponse(int state, int number, long timestamp) {
    	this.state = state;
    	this.number = number;
    	this.timestamp = timestamp;
    }

    @XmlElement(name = "state")
    public int getState() {
        return state;
    }

    @XmlElement(name = "number")
    public int getNumber() {
        return number;
    }

    @XmlElement(name = "timestamp")
    public long getTimeStamp() {
        return timestamp;
    }
}
