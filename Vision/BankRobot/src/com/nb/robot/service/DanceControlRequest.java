package com.nb.robot.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DanceControlRequest {
	// Whether the dance or music should be running.
    private boolean state;

    public DanceControlRequest() {
    }

    @XmlElement(name = "state")
    public boolean getState() {
        return state;
    }

}
