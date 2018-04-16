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

@Path("speaker")
public class SpeakerResource {
	SpeechSynthesisModule speechSynthesisModule = SpeechSynthesisModule.getInstance();

	@GET
	@Produces("text/plain")
	public String getHello() {
		return "Hello, speaker!";
	}

	@POST
	@Path("control")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response control(SpeakerControlRequest controlRequest) {
		boolean shouldRun = controlRequest.getState();
		if (shouldRun) {
			speechSynthesisModule.start();
		} else {
			speechSynthesisModule.stop();
		}
		return Response.status(Status.OK).entity(new UtilStatus()).build();
	}

	@POST
	@Path("talk")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response talk(SpeechSynthesisRequest request) {
		UtilStatus utilStatus = speechSynthesisModule.run(request);
		return Response.status(Status.OK).entity(utilStatus).build();
	}

}
