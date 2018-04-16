package com.nb.robot.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SpeakerControlRequest {
	// Whether the speaker module should be running.
    private boolean state;

    public SpeakerControlRequest() {
    }

    @XmlElement(name = "state")
    public boolean getState() {
        return state;
    }

}
