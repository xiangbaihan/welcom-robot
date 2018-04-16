package com.nb.robot.service;

import org.apache.log4j.Logger;

import com.nb.robot.common.Constants;
import com.nb.robot.common.FunctionModule;
import com.nb.robot.lidar.LidarRunnable;
import com.nb.robot.vision.FaceDetectionRunnable;
import com.nb.robot.vision.VideoFrame;

// Human detection module.
// This is a singleton class.
public class HumanDetectionModule implements FunctionModule {
	private static Logger logger = Logger.getLogger(HumanDetectionModule.class);
	
	private static volatile HumanDetectionModule instance = null;
	private FaceDetectionRunnable faceDetectionRunnable = null;
	private Thread faceDetectionThread = null;
	private LidarRunnable lidarRunnable = null;
	private Thread lidarThread = null;
	private HumanDetectionRunnable humanDetectionRunnable = null;
	private Thread humanDetectionThread = null;
	private VideoFrame videoFrame = null;
	private boolean isRunning = false;
	
	public static HumanDetectionModule getInstance() {
		if (instance == null) {
			synchronized (HumanDetectionModule.class) {
				if (instance == null) {
					instance = new HumanDetectionModule();
				}
			}
		}
		return instance;
	}

	private HumanDetectionModule() {
		isRunning = false;
	};
	
	@Override
	public synchronized boolean start() {
		if (isRunning) {
			return true;
		}

		faceDetectionRunnable = new FaceDetectionRunnable();
		if (!faceDetectionRunnable.init()) {
			logger.error("Failed to init faceDetectionRunnable: " + faceDetectionRunnable.errorMessage());
			return false;
		}		
		lidarRunnable = new LidarRunnable();
		if (!lidarRunnable.init()) {
			logger.error("Failed to init lidarRunnable: " + lidarRunnable.errorMessage());
			return false;
		}
		humanDetectionRunnable = new HumanDetectionRunnable(faceDetectionRunnable, lidarRunnable);
		
		faceDetectionThread = new Thread(faceDetectionRunnable);
		faceDetectionThread.start();
		lidarThread = new Thread(lidarRunnable);
		lidarThread.start();
		humanDetectionThread = new Thread(humanDetectionRunnable);
		humanDetectionThread.start();
		
		if (Constants.FACE_DETECTION_ENABLE_DISPLAY) {
			videoFrame = new VideoFrame(faceDetectionRunnable);
			videoFrame.setVisible(true);
		} else {
			videoFrame = null;
		}
		
		isRunning = true;
		logger.info("HumanDetectionModule started");
		return true;
	}

	@Override
	public synchronized void stop() {
		// Mark isRunning false in all threads.
		if (faceDetectionRunnable != null) {
			faceDetectionRunnable.close();
		}
		if (lidarRunnable != null) {
			lidarRunnable.close();
		}
		if (humanDetectionRunnable != null) {
			humanDetectionRunnable.close();
		}
		
		// Wait for all threads to stop.
		try {
			if (faceDetectionThread != null) {
				faceDetectionThread.interrupt();
				faceDetectionThread.join();
			}
			if (lidarThread != null) {
				lidarThread.interrupt();
				lidarThread.join();
			}
			if (humanDetectionThread != null) {
				humanDetectionThread.interrupt();
				humanDetectionThread.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (videoFrame != null) {
			videoFrame.setVisible(false);
			videoFrame.dispose();
		}

		lidarRunnable = null;
		lidarThread = null;
		faceDetectionRunnable = null;
		faceDetectionThread = null;
		humanDetectionRunnable = null;
		humanDetectionThread = null;
		videoFrame = null;
		
		isRunning = false;
		logger.info("HumanDetectionModule stopped");
	}

	@Override
	public synchronized boolean isHealthy() {
		if (faceDetectionRunnable == null || lidarRunnable == null) {
			return false;
		}
		return faceDetectionRunnable.isHealthy() && lidarRunnable.isHealthy();
	}

	@Override
	public synchronized String errorMessage() {
		if (faceDetectionRunnable == null) {
			return "Face detection thread is not initialized.";
		}
		if (lidarRunnable == null) {
			return "LIDAR thread is not initialized.";
		}
		return faceDetectionRunnable.errorMessage() + "\n" + lidarRunnable.errorMessage();
	}
}
