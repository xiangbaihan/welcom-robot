package com.nb.robot.service;

import org.apache.log4j.Logger;
import org.eclipse.jetty.util.log.Log;
import org.json.JSONException;
import org.json.JSONObject;

import com.nb.robot.common.CommonUtils;
import com.nb.robot.common.FunctionModule;
import com.nb.robot.common.PropertiesUtil;
import com.nb.robot.serialComm.BatteryState;
import com.nb.robot.serialComm.ControlArm;
import com.nb.robot.serialComm.ControlBattery;
import com.nb.robot.serialComm.ControlBattery.batteryReadCallBack;
import com.nb.robot.server.SocketServerModule;
import com.nb.robot.serialComm.ControlExpression;

// Battery module.
// This is a singleton class.
public class BatteryModule implements FunctionModule  {
	private static Logger logger = Logger.getLogger(BatteryModule.class);

	private SocketServerModule socketServer = SocketServerModule.getInstance();
	private static volatile BatteryModule instance = null;
	private ControlBattery controlBattery;
	private BatteryState batteryState;

	public static BatteryModule getInstance() {
		if (instance == null) {
			synchronized (BatteryModule.class) {
				if (instance == null) {
					instance = new BatteryModule();
				}
			}
		}
		return instance;
	}

	private BatteryModule() {
	}

	@Override
	public boolean start() {
		String batteryPort = PropertiesUtil.getPropFromProperties("battery.portName");
		batteryPort = CommonUtils.getSymbolicLinkTarget(batteryPort);
		controlBattery = new ControlBattery(batteryPort);
		
		logger.info("BatteryModule started");
		return true;
	}

	@Override
	public void stop() {	
		controlBattery = null;
		logger.info("BatteryModule stopped");
	}

	@Override
	public boolean isHealthy() {
		return true;
	}

	@Override
	public String errorMessage() {
		return "";
	}
	
	// Returns current remaining battery percent (0 ~ 100), or -1 if unhealthy.
	public void getBatteryPercent() {
		if (!isHealthy()) {
			return ;
		}
		// TODO(Yang): implement this using serial communication.
		//must send a signal to Battery,then Battery can return back a signal 
		controlBattery.sendMsgToBattery();
		batteryState = new BatteryState();
		controlBattery.setReadCallBack(new batteryReadCallBack() {
			@Override
			public void readSuccess(BatteryState state) {
				// TODO Auto-generated method stub
				batteryState = state;
				String jsonString ="";
				try {
					jsonString = new JSONObject()
							.put("type",5)
							.put("soc", batteryState.getSoc()).toString();
				} catch (JSONException e) {
					e.printStackTrace();
				}catch (Exception e) {
					e.printStackTrace();
				}
				socketServer.sendMessage(jsonString);
				System.out.println(jsonString);
			}
		});
	}
}
