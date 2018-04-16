package com.nb.robot.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.nb.robot.common.UtilStatus;

@Path("led")
public class LedResource {
	MotionAndLedControlModule ledControlModule = MotionAndLedControlModule.getInstance();

	@GET
	@Produces("text/plain")
	public String getHello() {
		return "Hello, LED!";
	}

	@POST
	@Path("control")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response controlLed(LedControlRequest ledControlRequest) {
		UtilStatus status = ledControlModule.controlLed(ledControlRequest.getLedState());
		return Response.status(Status.OK).entity(status).build();
	}
}
