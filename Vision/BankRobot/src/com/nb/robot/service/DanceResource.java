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

@Path("dance")
public class DanceResource {

	private static Logger logger = Logger.getLogger(DanceResource.class);

	DanceControlModule danceControlModule = DanceControlModule.getInstance();

	@GET
	@Path("/")
	@Produces("text/plain")
	public String getHello() {
		return "Hello, dance!";
	}

	@POST
	@Path("danceFlag")
	@Produces(MediaType.APPLICATION_JSON)
	public Response stopOrResumeDance(DanceControlRequest danceControlRequest) {
		logger.debug("跳舞/停止");
		boolean danceFlag = danceControlRequest.getState();
		UtilStatus status = danceControlModule.controlStopOrResumeDance(danceFlag);
		return Response.status(Status.OK).entity(status).build();
	}

	@POST
	@Path("musicFlag")
	@Produces(MediaType.APPLICATION_JSON)
	public Response musicVoice(DanceControlRequest danceControlRequest) {
		logger.debug("音乐/静音");
		// music state = false => muteFlag = true.
		boolean muteFlag = danceControlRequest.getState();
		UtilStatus status = danceControlModule.controlDanceMusic(muteFlag);
		return Response.status(Status.OK).entity(status).build();
	}

	@POST
	@Path("control")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response controlDance(DanceRequest danceRequest) {
		logger.debug("总控制跳舞类型/时间/重复次数");
		long duration = danceRequest.getDuration();
		int type = danceRequest.getType();
		int repeat = danceRequest.getRepeat();
		boolean muteFlag = danceRequest.getMuteFlag();
		logger.debug("总控制跳舞"+type+"类型"+duration+"时间"+repeat+"重复次数"+muteFlag);
		UtilStatus status = danceControlModule.controlDance(type, repeat, duration, muteFlag);
		return Response.status(Status.OK).entity(status).build();
	}

}