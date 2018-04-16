package com.nb.robot.service;

import org.apache.log4j.Logger;
import com.nb.robot.common.FunctionModule;
import com.nb.robot.common.UtilStatus;

// Dance controller module. It relies on MotionAndLedControlModule.
// This is a singleton class.
public class DanceControlModule implements FunctionModule {

	private static Logger logger = Logger.getLogger(DanceControlModule.class);

	private static volatile DanceControlModule instance = null;
	private static DanceController danceController = null;
	private static MotionAndLedControlModule motionContrlModule=MotionAndLedControlModule.getInstance();;
	public static DanceControlModule getInstance() {
		if (instance == null) {
			synchronized (MotionAndLedControlModule.class) {
				if (instance == null) {
					instance = new DanceControlModule();
					logger.trace("DanceControlModule---getInstance");
				}
			}
		}
		return instance;
	}

	private DanceControlModule() {
	}

	// 舞蹈类型，重复次数，表演时长，静音标志...
	public UtilStatus controlDance(int type, int repeat, long duration, boolean muteFlag) {
		if (type <= 0 || type > 3) {
			return new UtilStatus(-1, "Dance type should be 1 ~ 3.");
		}
		danceController.controlDance(type, repeat, duration, muteFlag);
		return new UtilStatus();
	}

	// 控制静音
	public UtilStatus controlDanceMusic(boolean muteFlag) {
		danceController.setMuteFlag(muteFlag);
		return new UtilStatus();
	}

	// 控制跳舞停止
	public UtilStatus controlStopOrResumeDance(boolean danceFlag) {
		danceController.stopOrResume(danceFlag);
		return new UtilStatus();
	}

	@Override
	public boolean start() {
		if (!motionContrlModule.isHealthy()) {
			logger.error("MotionAndLedControlModule is not healthy.");
			return false;
		}
		danceController = new DanceController();
	    return true;
	}

	@Override
	public void stop() {
		danceController.stopOrResume(false);
	}

	@Override
	public boolean isHealthy() {
		return motionContrlModule.isHealthy();
	}

	@Override
	public String errorMessage() {
		if (!motionContrlModule.isHealthy()) {
			return "The required MotionAndLedControlModule is not healthy: " + motionContrlModule.errorMessage();
		}
		return "";
	}
}