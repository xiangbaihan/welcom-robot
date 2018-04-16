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

@Path("motion")
public class MotionResource {
	private static Logger logger = Logger.getLogger(MotionResource.class);

	MotionAndLedControlModule motionControlModule = MotionAndLedControlModule.getInstance();

	@GET
	@Produces("text/plain")
	public String getHello() {
		return "Hello, motion!";
	}

	@POST
	@Path("arm")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response controlArm(ArmControlRequest armControlRequest) {
		int armPart = armControlRequest.getArmPart();
		int armPosition = armControlRequest.getArmPosition();
		int armSpeed = armControlRequest.getArmSpeed();
		
		UtilStatus status = motionControlModule.controlArm(armPart, armPosition, armSpeed);
		return Response.status(Status.OK).entity(status).build();
	}

	@POST
	@Path("chasis")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response controlChasisMotion(MotionControlRequest motionControlRequest) {
		int motionLineSpeed = motionControlRequest.getMotionLineSpeed();
		int motionAngularSpeed = motionControlRequest.getMotionAngularSpeed();
		long motionDuration = motionControlRequest.getMotionDuration();
		logger.info("控制底盘运动,motionLineSpeed=" + motionLineSpeed + ",  motionAngularSpeed=" + motionAngularSpeed + ",  motionDuration="
				+ motionDuration);
		UtilStatus status = motionControlModule.controlChasis(motionLineSpeed, motionAngularSpeed, motionDuration);
		return Response.status(Status.OK).entity(status).build();

	}

	@POST
	@Path("resetCoordinate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response resetChassisCoordinate(ChassisResetRequest request) {
		logger.debug("reset coordinate: " + request.getReset());
		UtilStatus status = new UtilStatus();
		if (!request.getReset()) {
			return Response.status(Status.OK).entity(status).build();
		}
		status = motionControlModule.resetChassisCoordinate();
		return Response.status(Status.OK).entity(status).build();
	}

}
