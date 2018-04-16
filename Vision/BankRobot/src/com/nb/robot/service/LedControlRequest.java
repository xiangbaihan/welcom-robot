package com.nb.robot.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LedControlRequest {
	private int ledState;

	public LedControlRequest() {
		ledState = 0;
	}

	public LedControlRequest(int ledState) {
		this.ledState = ledState;
	}

	@XmlElement(name = "ledState")
	public int getLedState() {
		return ledState;
	}
}
