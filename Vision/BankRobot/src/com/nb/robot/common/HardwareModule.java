package com.nb.robot.common;

public interface HardwareModule {
	// Initializes the hardware and returns whether the hardware is opened successfully.
	public boolean init();
	
	// Closes the hardware.
	public void close();

	// Returns if the hardware is healthy.
	public boolean isHealthy();

	// Returns latest error message of this hardware, if any.
	public String errorMessage();

}
