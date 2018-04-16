package com.nb.robot.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RootControlRequest {
	// Whether all function modules should be running.
    private boolean state;

    public RootControlRequest() {
    }

    @XmlElement(name = "state")
    public boolean getState() {
        return state;
    }

}
