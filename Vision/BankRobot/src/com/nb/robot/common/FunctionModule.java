package com.nb.robot.common;

public interface FunctionModule {	
	// Starts running the module and returns whether the module launches successfully.
	public boolean start();
	
	// Stops running the module.
	public void stop();

	// Returns if the module is healthy, like hardware is functional,
	// configuration file is valid.
	boolean isHealthy();

	// Returns latest error message of this module, if any.
	public String errorMessage();
}
