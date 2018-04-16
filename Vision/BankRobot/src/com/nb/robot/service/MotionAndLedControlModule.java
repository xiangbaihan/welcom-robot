package com.nb.robot.service;

import org.apache.log4j.Logger;

import com.nb.robot.common.CommonUtils;
import com.nb.robot.common.Constants;
import com.nb.robot.common.FunctionModule;
import com.nb.robot.common.PropertiesUtil;
import com.nb.robot.common.UtilStatus;
import com.nb.robot.serialComm.ChasisMotionState;
import com.nb.robot.serialComm.ControlMotion;
import com.nb.robot.serialComm.ControlArm;
import com.nb.robot.serialComm.ControlMotion.readCallBack;
import com.nb.robot.server.SocketServerModule;

// Motion control and LED control module. It operates arm, chasis and LED light.
// LED control is in the same module due to hardware limitation. It shares the
// same serial communication port with chasis.
// This is a singleton class.
public class MotionAndLedControlModule implements FunctionModule {
	private static Logger logger = Logger.getLogger(MotionAndLedControlModule.class);

	private static volatile MotionAndLedControlModule instance = null;
	private SocketServerModule socketServer = SocketServerModule.getInstance();
	private static ControlMotion controlMotion;
	private static ControlArm controlArm;
	private ChasisMotionState chasisMotionState = new ChasisMotionState();
	// Last time when notification message on chasis location is sent via
	// socket.
	private long lastChasisLocationNotificationTime = 0;

	// Cached motion control request and LED control request.
	private MotionControlRequest lastMotionControlRequest = new MotionControlRequest();
	private long lastMotionControlRequestTimestamp = 0;
	private int curLedState = Constants.DEFAULT_LED_STATE;

	public static MotionAndLedControlModule getInstance() {
		if (instance == null) {
			synchronized (MotionAndLedControlModule.class) {
				if (instance == null) {
					instance = new MotionAndLedControlModule();
				}
			}
		}
		return instance;
	}

	private MotionAndLedControlModule() {
	}

	@Override
	public boolean start() {
		String chassisPort = PropertiesUtil.getPropFromProperties("chassis.portName");
		chassisPort = CommonUtils.getSymbolicLinkTarget(chassisPort);
		controlMotion = new ControlMotion(chassisPort);

		String armPort = PropertiesUtil.getPropFromProperties("arm.portName");
		armPort = CommonUtils.getSymbolicLinkTarget(armPort);
		controlArm = new ControlArm(armPort);

		// 开启socketServer的监听，连接成功后向Android发送底盘的坐标信息
		try {
			// controlMotion.initialChasisMotion();
			controlMotion.setReadCallBack(new readCallBack() {
				@Override
				public void readSuccess(ChasisMotionState state) {
					if (System.currentTimeMillis()
							- lastChasisLocationNotificationTime < Constants.CHASIS_LOCATION_NOTIFICATION_TIME_INTERVAL) {
						return;
					}
					lastChasisLocationNotificationTime = System.currentTimeMillis();
					chasisMotionState = state;
					String message = "底盘X,Y,TH: " + chasisMotionState.getX() + "," + chasisMotionState.getY() + ","
							+ chasisMotionState.getTh();
					logger.trace(message);
					socketServer.sendMessage(message);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("MotionControlModule started");
		return true;
	}

	@Override
	public void stop() {
		controlMotion = null;
		controlArm = null;
		// Reset cached motion control request and LED control request.
		lastMotionControlRequest = new MotionControlRequest();
		curLedState = Constants.DEFAULT_LED_STATE;
		logger.info("MotionControlModule stopped");
	}

	@Override
	public boolean isHealthy() {
		return true;
	}

	@Override
	public String errorMessage() {
		return "";
	}

	// Controls chasis movement.
	public UtilStatus controlChasis(int lineSpeed, int angularSpeed, long motionDuration) {
		if (lineSpeed < -10 || lineSpeed > 10 || angularSpeed > 180 || angularSpeed < -180) {
			return new UtilStatus(-1, "The linespeed range is -10~10 and the angularspeed range is -180~180! ");
		}
		lastMotionControlRequest = new MotionControlRequest(lineSpeed, angularSpeed, motionDuration);
		lastMotionControlRequestTimestamp = System.currentTimeMillis();
		try {
			controlMotion.setChasisMotion(lastMotionControlRequest.getMotionLineSpeed(),
					lastMotionControlRequest.getMotionAngularSpeed(), lastMotionControlRequest.getMotionDuration(),
					curLedState);
		} catch (Exception e) {
			e.printStackTrace();
			return new UtilStatus(-1, e.getMessage());
		}
		return new UtilStatus();

	}

	// Resets coordinate of chassis including x, y, theta, to 0.
	public UtilStatus resetChassisCoordinate() {
		try {
			controlMotion.resetChassisCoordinate();
		} catch (Exception e) {
			e.printStackTrace();
			return new UtilStatus(-1, e.getMessage());
		}
		return new UtilStatus();
	}

	// Controls arm movement.
	public UtilStatus controlArm(int armPart, int armPosition, int armSpeed) {
		logger.debug("control Arm");
		try {
			controlArm.setArmMotion(armPart, armPosition, armSpeed);
		} catch (Exception e) {
			e.printStackTrace();
			return new UtilStatus(-1, e.getMessage());
		}
		return new UtilStatus();
	}

	// Returns current ChasisMotionState read from underlying serial port.
	public ChasisMotionState getChasisMotionState() {
		return chasisMotionState;
	}

	// Controls LED light state.
	public UtilStatus controlLed(int ledState) {
		if (ledState < 1 || ledState > 9) {
			return new UtilStatus(-1, "The valid LED state is 1 ~ 9.");
		}
		curLedState = ledState;
		try {
			long lastMotionControlRequestLeftTime = lastMotionControlRequestTimestamp
					+ lastMotionControlRequest.getMotionDuration() - System.currentTimeMillis();
			if (lastMotionControlRequestLeftTime > 0) {
				controlMotion.setChasisMotion(lastMotionControlRequest.getMotionLineSpeed(),
						lastMotionControlRequest.getMotionAngularSpeed(), lastMotionControlRequestLeftTime,
						curLedState);
			} else {
				// Set line and angular speed to 0, duration to some value
				// greater than CHASIS_LED_SERIAL_COM_TIME_INTERVAL.
				controlMotion.setChasisMotion(0, 0, 2 * Constants.CHASIS_LED_SERIAL_COM_TIME_INTERVAL, curLedState);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new UtilStatus(-1, e.getMessage());
		}
		return new UtilStatus();
	}
}
