package com.nb.robot.service;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.nb.robot.common.CommonUtils;
import com.nb.robot.common.UtilStatus;

@Path("speech")
public class SpeechResource {
	private static Logger logger = Logger.getLogger(SpeechResource.class);
	SpeechRecognitionModule speechRecognitionModule = SpeechRecognitionModule.getInstance();

	@GET
	@Produces("text/plain")
	public String getHello() {
		return "Hello, speech!";
	}

	@POST
	@Path("control")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response control(SpeechControlRequest controlRequest) {
		boolean shouldRun = controlRequest.getState();
		if (shouldRun) {
			speechRecognitionModule.start();
		} else {
			speechRecognitionModule.stop();
		}
		return Response.status(Status.OK).entity(new UtilStatus()).build();
	}

	@POST
	@Path("upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadFile(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		String bnfFilePath = "./config/" + fileDetail.getFileName();
		UtilStatus utilStatus = CommonUtils.writeToFile(uploadedInputStream, bnfFilePath);
		logger.trace("Save file to " + bnfFilePath + ". Result: " + utilStatus.toString());
		if (!utilStatus.isOK()) {
			return Response.status(Status.OK).entity(utilStatus).build();
		}
		speechRecognitionModule.stop();
		speechRecognitionModule.setBnfFilePath(bnfFilePath);
		speechRecognitionModule.start();
		if (!speechRecognitionModule.isHealthy()) {
			utilStatus = new UtilStatus(-1,  speechRecognitionModule.errorMessage());
		}
		return Response.status(Status.OK).entity(utilStatus).build();
	}
}
