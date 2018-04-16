package com.nb.robot.lidar;

public class CachedMeasurementNode {
	private MeasurementNode node;
	// In milliseconds.
	private long timestamp;
	
	public CachedMeasurementNode(MeasurementNode node, long timestamp) {
		this.node = node;
		this.timestamp = timestamp;
	}
	
	public MeasurementNode getNode() {
		return node;
	}
	
	public long getTimestamp(){
		return timestamp;
	}
}
