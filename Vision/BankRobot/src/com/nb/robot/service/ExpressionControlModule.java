package com.nb.robot.service;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.nb.robot.common.CommonUtils;
import com.nb.robot.common.FunctionModule;
import com.nb.robot.common.PropertiesUtil;
import com.nb.robot.common.UtilStatus;
import com.nb.robot.serialComm.ControlExpression;

// Expression control module.
// This is a singleton class.
public class ExpressionControlModule implements FunctionModule  {
	private static Logger logger = Logger.getLogger(ExpressionControlModule.class);

	private static volatile ExpressionControlModule instance = null;
	public static ControlExpression controlExpression;

	public static ExpressionControlModule getInstance() {
		if (instance == null) {
			synchronized (ExpressionControlModule.class) {
				if (instance == null) {
					instance = new ExpressionControlModule();
				}
			}
		}
		return instance;
	}

	private ExpressionControlModule() {
	}

	@Override
	public boolean start() {
		String emotionPort = PropertiesUtil.getPropFromProperties("emotion.portName");
		emotionPort = CommonUtils.getSymbolicLinkTarget(emotionPort);
		controlExpression = new ControlExpression(emotionPort);
		logger.info("ExpressionControlModule started");
		return true;
	}

	@Override
	public void stop() {
		controlExpression = null;		
		logger.info("ExpressionControlModule stopped");
	}

	@Override
	public boolean isHealthy() {
		return true;
	}

	@Override
	public String errorMessage() {
		return "";
	}

	// 静态表情请求
	public Response controlStaticExpresssion(int emotion) {
		logger.debug("control static expression");
		try {
			controlExpression.sendStaticExpression((byte) emotion);
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new UtilStatus(-1, e.getMessage())).build();
		}
		return Response.status(Status.OK).entity(new UtilStatus()).build();
	}

	// 动态表情请求
	public Response controlDynamicExpresssion(int emotion, long emotionDuration, int emotionRepeat) {
		logger.debug("control dynamic expression");
		try {
			controlExpression.setDynamicExpression(emotion, emotionDuration, emotionRepeat);
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new UtilStatus(-1, e.getMessage())).build();
		}
		return Response.status(Status.OK).entity(new UtilStatus()).build();
	}

}
