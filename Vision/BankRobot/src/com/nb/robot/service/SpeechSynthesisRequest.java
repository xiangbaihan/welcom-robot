package com.nb.robot.service;

import javax.xml.bind.annotation.XmlElement;

public class SpeechSynthesisRequest {
	// Speech content.
    private String content;
    private int speed;
    private int volume;
    private int pitch;
    // How many times to repeat the speech.
    private int repeat;
    
    public SpeechSynthesisRequest() {
    	
    }
    
    public SpeechSynthesisRequest(String content, int speed, int volume, int pitch, int repeat) {
    	this.content = content;
    	this.speed = speed;
    	this.volume = volume;
    	this.pitch = pitch;
    	this.repeat = repeat;
    }

    @XmlElement(name = "content")
    public String getContent() {
        return content;
    }

    @XmlElement(name = "speed")
    public int getSpeed() {
        return speed;
    }

    @XmlElement(name = "volume")
    public int getVolume() {
        return volume;
    }

    @XmlElement(name = "pitch")
    public int getPitch() {
        return pitch;
    }

    @XmlElement(name = "repeat")
    public int getRepeat() {
        return repeat;
    }

}
