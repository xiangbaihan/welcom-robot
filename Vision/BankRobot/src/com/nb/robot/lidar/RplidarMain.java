package com.nb.robot.lidar;

public class RplidarMain {

	public static void main(String[] args) {
		RplidarNative rplidar = new RplidarNative();
		long driver = rplidar.createDriver();
		if (driver == 0) {
			System.out.println("Failed to create driver...");
			return;
		}
		
		String serialPort = "/dev/ttyUSB0";
		boolean is_connected = rplidar.connect(driver, serialPort);
		if (!is_connected) {
			System.out.println("Failed to bind " + serialPort);
			rplidar.disposeDriver(driver);
			return;
		}
		
		boolean is_healthy = rplidar.checkHealth(driver);
		if (!is_healthy) {
			System.out.println("RpLidar is not healthy.");
			rplidar.disposeDriver(driver);
			return;
		}
		
		rplidar.startScan(driver);
		MeasurementNode[] nodes = rplidar.getScanData(driver);
		if (nodes == null) {
			System.out.println("No scan result.");			
		} else {
			for (MeasurementNode node : nodes) {
				System.out.println(node.angle + " " + node.distance + " " + node.sync_quality);
			}
		}
		rplidar.stopScan(driver);
		rplidar.disposeDriver(driver);
	}

}
