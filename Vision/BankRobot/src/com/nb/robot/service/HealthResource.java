package com.nb.robot.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONArray;

import com.nb.robot.common.HealthStatus;
import com.nb.robot.server.SocketServerModule;
import com.nb.robot.xf.MscModule;

@Path("health")
public class HealthResource {
	SocketServerModule socketServer = SocketServerModule.getInstance();
	BatteryModule batteryModule = BatteryModule.getInstance();

	MotionAndLedControlModule motionAndLedControlModule = MotionAndLedControlModule.getInstance();
	ExpressionControlModule expressionControlModule = ExpressionControlModule.getInstance();
	DanceControlModule danceControlModule = DanceControlModule.getInstance();

	HumanDetectionModule humanDetectionModule = HumanDetectionModule.getInstance();

	MscModule mscModule = MscModule.getInstance();
	SpeechRecognitionModule speechRecognitionModule = SpeechRecognitionModule.getInstance();
	SpeechSynthesisModule speechSynthesisModule = SpeechSynthesisModule.getInstance();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getHealthInfo() {
		JSONArray array = new JSONArray();
		HealthStatus moduleStatus = new HealthStatus("SocketServerModule", socketServer.isHealthy(),
				socketServer.errorMessage());
		array.put(moduleStatus);
		moduleStatus = new HealthStatus("BatteryModule", batteryModule.isHealthy(), batteryModule.errorMessage());
		array.put(moduleStatus);
		moduleStatus = new HealthStatus("MotionAndLedControlModule", motionAndLedControlModule.isHealthy(),
				motionAndLedControlModule.errorMessage());
		array.put(moduleStatus);
		moduleStatus = new HealthStatus("DanceControlModule", danceControlModule.isHealthy(),
				danceControlModule.errorMessage());
		array.put(moduleStatus);
		moduleStatus = new HealthStatus("ExpressionControlModule", expressionControlModule.isHealthy(),
				expressionControlModule.errorMessage());
		array.put(moduleStatus);
		moduleStatus = new HealthStatus("HumanDetectionModule", humanDetectionModule.isHealthy(),
				humanDetectionModule.errorMessage());
		array.put(moduleStatus);
		moduleStatus = new HealthStatus("MscModule (Xunfei)", mscModule.isHealthy(), mscModule.errorMessage());
		array.put(moduleStatus);
		moduleStatus = new HealthStatus("SpeechRecognitionModule", speechRecognitionModule.isHealthy(),
				speechRecognitionModule.errorMessage());
		array.put(moduleStatus);
		moduleStatus = new HealthStatus("SpeechSynthesisModule", speechSynthesisModule.isHealthy(),
				speechSynthesisModule.errorMessage());
		array.put(moduleStatus);

		return Response.status(Status.OK).entity(array).build();
	}

}
