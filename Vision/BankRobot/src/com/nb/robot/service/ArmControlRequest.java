package com.nb.robot.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ArmControlRequest {
	// 1: left arm
	// 2: right arm
	// <=0 or >=3: both arms
	private int armPart;
	// 0 - 360 degree
	private int armPosition;
	// 1 - 10
	private int armSpeed;
    
	public ArmControlRequest(){ }
	
	public ArmControlRequest(int armPart, int armPosition, int armSpeed) {
		this.armPart = armPart;
		this.armPosition = armPosition;
		this.armSpeed = armSpeed;
	}

	@XmlElement(name="armPart")
	public int getArmPart() {
		return armPart;
	}
	
	@XmlElement(name="armPosition")
	public int getArmPosition() {
		return armPosition;
	}
	
	@XmlElement(name="armSpeed")
	public int getArmSpeed() {
		return armSpeed;
	}

	public void setArmPart(int armPart) {
		this.armPart = armPart;
	}

	public void setArmPosition(int armPosition) {
		this.armPosition = armPosition;
	}

	public void setArmSpeed(int armSpeed) {
		this.armSpeed = armSpeed;
	}
	
}
