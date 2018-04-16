package com.nb.robot.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.nb.robot.common.UtilStatus;

@Path("expression")
public class ExpressionResource {
	private static Logger logger = Logger.getLogger(ExpressionResource.class);
	ExpressionControlModule expressionControlModule = ExpressionControlModule.getInstance();

	@GET
	@Produces("text/plain")
	public String getHello() {
		return "Hello, expression!";
	}

	@POST
	@Path("staticExpresssion")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response controlStaticEmotion(ExpressionControlRequest emotionControlRequest) {
		try {
			int emotion = emotionControlRequest.getEmotion();
			logger.info("LED静态表情 ,emotion=" + emotion);

			return expressionControlModule.controlStaticExpresssion(emotion);
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new UtilStatus(-1, e.getMessage())).build();
		}

	}

	@POST
	@Path("dynamicExpresssion")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response controlDynamicEmotion(ExpressionControlRequest emotionControlRequest) {

		try {
			int emotion = emotionControlRequest.getEmotion();
			long emotionDuration = emotionControlRequest.getEmotionDuration();
			int emotionRepeat = emotionControlRequest.getEmotionRepeat();
			logger.info("LED动态表情 ,emotion=" + emotion + ",  emotionDuration=" + emotionDuration + ",  emotionRepeat="
					+ emotionRepeat);
			return expressionControlModule.controlDynamicExpresssion(emotion, emotionDuration, emotionRepeat);
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.OK).entity(new UtilStatus(-1, e.getMessage())).build();
		}
	}
}
