package com.nb.robot.lidar;

public class MeasurementNode {
	int sync_quality;
	// Unit: degree (360)
	float angle;
	// Unit: millimeter
	float distance;
	
	MeasurementNode(int sync_quality, float angle, float distance) {
		this.sync_quality = sync_quality;
		this.angle = angle;
		this.distance = distance;
	}
	
	public int getSyncQuality() {
		return this.sync_quality;
	}
	
	public float getAngle() {
		return this.angle;
	}
	
	public float getDistance() {
		return this.distance;
	}
	
	@Override
	public String toString() {
		return "angle: " + angle + "; distance: " + distance + "; sync_quality: " + sync_quality;
	}
	

}
