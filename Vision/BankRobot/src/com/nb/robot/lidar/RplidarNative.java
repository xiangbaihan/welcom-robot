package com.nb.robot.lidar;

public class RplidarNative {
    public native void hello();
    
	// Creates RPLIDAR driver and returns pointer address.
    public native long createDriver();
    
    // Opens the specified serial port and connects to the target RPLIDAR device.
    public native boolean connect(long driver, String portPath);
    
	// Disposes RPLIDAR driver.
    public native void disposeDriver(long driver);
    
    // Returns if the RPLIDAR driver is healthy.
    public native boolean checkHealth(long driver);
    
    // Starts motor and scanning.
    public native void startScan(long driver);
    
    // Stops motor and scanning.
    public native void stopScan(long driver);
    
    // Returns a list of MeasurementNode. The list size depends on the underlying RPLIDAR
    // scan result, which varies from one scan to another.
    public native MeasurementNode[] getScanData(long driver);
    
    static {
        System.loadLibrary("rplidar_native");
    }

}
