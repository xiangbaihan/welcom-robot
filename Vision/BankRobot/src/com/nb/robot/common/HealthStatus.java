package com.nb.robot.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//Represents status of a function module.
@XmlRootElement
public class HealthStatus {

	@XmlElement(name="moduleName")
	String moduleName;
	
	@XmlElement(name="isHealthy")
	boolean isHealthy;

	@XmlElement(name="errorMessage")
	String errorMessage;
	
	public HealthStatus(String moduleName, boolean isHealthy, String errorMessage) {
		this.moduleName = moduleName;
		this.isHealthy = isHealthy;
		this.errorMessage = errorMessage;
	}
}
