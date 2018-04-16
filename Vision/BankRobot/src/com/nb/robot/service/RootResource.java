package com.nb.robot.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import com.nb.robot.common.UtilStatus;
 
/**
 * Root resource (exposed at "/" path)
 */
@Path("")
public class RootResource {
	private static Logger logger = Logger.getLogger(RootResource.class);
	HumanDetectionModule humanDetectionModule = HumanDetectionModule.getInstance();
	SpeechRecognitionModule speechRecognitionModule = SpeechRecognitionModule.getInstance();

    @GET
    @Produces({MediaType.TEXT_HTML})
    public InputStream getHello() {
    	File f = new File("./html/api.html");
        try {
			return new FileInputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
        return null;
    }
    
    @POST
	@Path("control")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response control(RootControlRequest controlRequest) {
		boolean shouldRun = controlRequest.getState();
		if (shouldRun) {
			humanDetectionModule.start();
			speechRecognitionModule.start();
		} else {
			humanDetectionModule.stop();
			speechRecognitionModule.stop();
		}
		return Response.status(Status.OK).entity(new UtilStatus()).build();
	}
}