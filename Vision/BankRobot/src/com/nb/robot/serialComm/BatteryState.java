package com.nb.robot.serialComm;

/**
 * @author mj
 *
 */
public class BatteryState {
private int soc;//电池容量

public BatteryState() {
	// TODO Auto-generated constructor stub
}

public BatteryState(int soc) {
	this.soc = soc;
}

public float getSoc() {
	return soc;
}


}
