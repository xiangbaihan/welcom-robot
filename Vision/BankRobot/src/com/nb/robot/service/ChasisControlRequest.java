package com.nb.robot.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

// This is deprecated.
@XmlRootElement
public class ChasisControlRequest {
	private int motionAction;
	private int motionSpeed;
	private int motionDuration;

	@XmlElement(name = "motionAction")
	public int getMotionAction() {
		return motionAction;
	}

	@XmlElement(name = "motionSpeed")
	public int getMotionSpeed() {
		return motionSpeed;
	}

	@XmlElement(name = "motionDuration")
	public int getMotionDuration() {
		return motionDuration;
	}
}
