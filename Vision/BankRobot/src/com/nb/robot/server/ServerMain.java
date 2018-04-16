package com.nb.robot.server;

import java.io.IOException;
import java.net.URI;

import org.apache.log4j.Logger;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.nb.robot.serialComm.ChasisMotionState;
import com.nb.robot.serialComm.ControlMotion;
import com.nb.robot.serialComm.ControlMotion.readCallBack;
import com.nb.robot.service.BatteryModule;
import com.nb.robot.service.BatteryResource;
import com.nb.robot.service.DanceControlModule;
import com.nb.robot.service.DanceResource;
import com.nb.robot.service.ExpressionControlModule;
import com.nb.robot.service.ExpressionResource;
import com.nb.robot.service.HealthResource;
import com.nb.robot.service.HumanDetectionModule;
import com.nb.robot.service.LedResource;
import com.nb.robot.service.MotionAndLedControlModule;
import com.nb.robot.service.MotionResource;
import com.nb.robot.service.RootResource;
import com.nb.robot.service.SpeakerResource;
import com.nb.robot.service.SpeechResource;
import com.nb.robot.service.SpeechSynthesisModule;
import com.nb.robot.xf.MscModule;
import com.nb.robot.service.SpeechRecognitionModule;

import org.glassfish.grizzly.http.server.HttpServer;

public class ServerMain {
	private static Logger logger = Logger.getLogger(ServerMain.class);

	public static void main(String[] args) {
		try {
			URI baseURI = UriUtils.getBaseURI();
			ResourceConfig resourceConfig = new ResourceConfig(RootResource.class, SpeechResource.class,
					SpeakerResource.class, MotionResource.class, ExpressionResource.class, LedResource.class,
					HealthResource.class, DanceResource.class, BatteryResource.class);
			resourceConfig.register(MultiPartFeature.class);
			final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseURI, resourceConfig, false);
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					// Close all modules when application exits.
					MotionAndLedControlModule.getInstance().stop();
					ExpressionControlModule.getInstance().stop();
					DanceControlModule.getInstance().stop();
					HumanDetectionModule.getInstance().stop();
					SpeechRecognitionModule.getInstance().stop();
					SpeechSynthesisModule.getInstance().stop();
					MscModule.getInstance().stop();
					BatteryModule.getInstance().stop();
					SocketServerModule.getInstance().stop();
					
					server.shutdownNow();
				}
			}));
			server.start();
			logger.info("Base URI: " + baseURI);

			SocketServerModule socketServer = SocketServerModule.getInstance();
			socketServer.start();
			
			BatteryModule batteryModule = BatteryModule.getInstance();
			batteryModule.start();

			// ExpressionControlModule expressionControlModule =
			// ExpressionControlModule.getInstance();
			// expressionControlModule.start();

			/*
			 * MotionAndLedControlModule motionControlModule =
			 * MotionAndLedControlModule.getInstance();
			 * motionControlModule.start();
			 */
			// 开启socketServer的监听，连接成功后向Android发送底盘的坐标信息

			// 测试舞蹈模块
			// DanceControlModule danceControlModule =
			// DanceControlModule.getInstance();
			// danceControlModule.start();
			// danceControlModule.stop();

			// HumanDetectionModule humanDetectionModule =
			// HumanDetectionModule.getInstance();
			// humanDetectionModule.start();

			// NOTE: MscModule must start before SpeechRecognitionModule and
			// SpeechSynthesisModule.
			// And the start can only be called once.
			MscModule mscModule = MscModule.getInstance();
			if (!mscModule.start()) {
				logger.fatal(mscModule.errorMessage());
			}

			SpeechRecognitionModule speechRecognitionModule = SpeechRecognitionModule.getInstance();
			speechRecognitionModule.start();

			// SpeechSynthesisModule speechSynthesisModule =
			// SpeechSynthesisModule.getInstance();
			// speechSynthesisModule.start();

			// Block until shutdown.
			Thread.currentThread().join();
			mscModule.stop();
		} catch (IOException | InterruptedException ex) {
			logger.error(ex);
		}
	}

}
