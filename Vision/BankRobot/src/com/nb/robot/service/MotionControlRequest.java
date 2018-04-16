package com.nb.robot.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MotionControlRequest {	
	private int motionLineSpeed;
	private int motionAngularSpeed;
	// In milliseconds
	private long motionDuration;
	
	public MotionControlRequest(){
		motionLineSpeed = 0;
		motionAngularSpeed = 0;
		motionDuration = 0;
	}
	
	public MotionControlRequest(int motionLineSpeed, int motionAngularSpeed, long motionDuration) {
		this.motionLineSpeed = motionLineSpeed;
		this.motionAngularSpeed = motionAngularSpeed;
		this.motionDuration = motionDuration;
	}
	
	@XmlElement(name="motionLineSpeed")
	public int getMotionLineSpeed() {
		return motionLineSpeed;
	}
	
	@XmlElement(name="motionAngularSpeed")
	public int getMotionAngularSpeed() {
		return motionAngularSpeed;
	}
	
	@XmlElement(name="motionDuration")
	public long getMotionDuration() {
		return motionDuration;
	}

	public void setMotionLineSpeed(int motionLineSpeed) {
		this.motionLineSpeed = motionLineSpeed;
	}

	public void setMotionAngularSpeed(int motionAngularSpeed) {
		this.motionAngularSpeed = motionAngularSpeed;
	}

	public void setMotionDuration(int motionDuration) {
		this.motionDuration = motionDuration;
	}
}
