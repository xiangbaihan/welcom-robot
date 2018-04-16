package com.nb.robot.xf;

import org.apache.log4j.Logger;

import com.nb.robot.common.Constants;
import com.nb.robot.common.FunctionModule;

// Module for login and logout of Xunfei MSC SDK.
// This is a singleton class.
public class MscModule implements FunctionModule {
	private static Logger logger = Logger.getLogger(MscModule.class);
	
	private static volatile MscModule instance = null;
	private AsrNative asrObj;
	private String errorMessage = "";
	
	public static MscModule getInstance() {
		if (instance == null) {
			synchronized (MscModule.class) {
				if (instance == null) {
					instance = new MscModule();
				}
			}
		}
		return instance;
	}

	private MscModule() {
	}

	@Override
	public boolean start() {
		asrObj = new AsrNative();
		int loginError = asrObj.login();
		if (loginError !=  Constants.DEFAULT_ERROR_CODE_OK) {
			errorMessage = "Failed to login. Error code: " + loginError;
			logger.error(errorMessage());
			return false;			
		}
		return true;
	}

	@Override
	public void stop() {
		if (asrObj != null) {
			int errorCode = asrObj.logout();
			if (errorCode != 0) {
				errorMessage = "Failed to logout. Error code: " + errorCode;
				logger.error(errorMessage());
			}
		}
		asrObj = null;		
	}

	@Override
	public boolean isHealthy() {
		return errorMessage.isEmpty();
	}

	@Override
	public String errorMessage() {
		return errorMessage;
	};

}
