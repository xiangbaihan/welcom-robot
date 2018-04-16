package com.nb.robot.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BatteryResponse {
	public final static int OK_STATUS = 0;

	// Remaining battery percent.
	@XmlElement(name = "percent")
	private int percent;

	public BatteryResponse() {

	}

	public BatteryResponse(int percent) {
		super();
		this.percent = percent;
	}

	public float getPercent() {
		return percent;
	}
	
	@Override
	public String toString() {
		return percent + ": " + percent;
	}
	
	

}