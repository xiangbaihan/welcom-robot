package com.nb.robot.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SpeechControlRequest {
	// Whether the speech module should be running.
    private boolean state;

    public SpeechControlRequest() {
    }

    @XmlElement(name = "state")
    public boolean getState() {
        return state;
    }
}
