package com.nb.robot.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.nb.robot.common.UtilStatus;

@Path("battery")
public class BatteryResource {

	BatteryModule batteryModule = BatteryModule.getInstance();

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBatteryPercent() {
		if(!batteryModule.isHealthy()) {
			return Response.status(Status.OK).entity(new UtilStatus(-1, "Battdery is not healthy"+batteryModule.errorMessage())).build();
		}else {
			batteryModule.getBatteryPercent();//触发发送信号
			return Response.status(Status.OK).entity(new UtilStatus()).build();
		}
	}
}
