package com.nb.robot.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class ChassisResetRequest {
	// Whether to reset Chassis coordinates.
    private boolean reset;

    public ChassisResetRequest() {
    }

    @XmlElement(name = "reset")
    public boolean getReset() {
        return reset;
    }

}
