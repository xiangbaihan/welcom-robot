package com.nb.robot.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

// Represents error status.
@XmlRootElement
public class UtilStatus {
	public final static int OK_STATUS = 0;
	
	@XmlElement(name="errorCode")
	int errorCode;

	@XmlElement(name="errorMessage")
	String errorMessage;
	
	public UtilStatus() {
		errorCode = OK_STATUS;
		errorMessage = "";
	}
	
	public UtilStatus(int errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	@XmlElement(name="okay")
	public boolean isOK() {
		return errorCode == OK_STATUS;
	}
	
	@Override
	public String toString() {
		return errorCode + ": " + errorMessage;
	}
}
