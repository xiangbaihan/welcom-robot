package com.nb.robot.vision;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import com.nb.robot.common.Constants;
import com.nb.robot.common.HardwareModule;

public class FaceDetectionRunnable implements Runnable, HardwareModule {
	private static Logger logger = Logger.getLogger(FaceDetectionRunnable.class);
	
	VideoCapture cap = null;
	FaceDetector faceDetector = new FaceDetector();
	volatile boolean isRunning = true;
	private static Semaphore semaphoreCap = new Semaphore(1);
	private static Semaphore semaphoreFaceInfos = new Semaphore(1);

	// Current frame in Mat format.
	Mat curMat = new Mat();
	// Used to convert Mat to BufferedImage;
	Mat2Image mat2Img = new Mat2Image();
	// Current face detection result;
	List<FaceInfo> curFaceInfos = new ArrayList<FaceInfo>();
	// Previous face detection result;
	List<FaceInfo> prevFaceInfos = new ArrayList<FaceInfo>();

	// This is empty if OK.
	String errorMessage = "";

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted() && isRunning) {
			try {
				Thread.sleep(Constants.FACE_DETECTION_SLEEP_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
				continue;
			}
			//logger.debug("当前人脸检测的时间"+System.currentTimeMillis()+"ms");
			// Reopen camera if necessary.
//			if (!isHealthy() && !init()) {
//				logger.debug("FaceDetectionRunnable is not healthy: " + errorMessage());
//				continue;
//			}

			prevFaceInfos.clear();
			int frameCount = 0;
			int frameHasFaceCount = 0;
			for (int i = 0; i < Constants.FACE_DETECTION_FRAME_COUNT_FOR_ONE_RESULT; i++) {
				try {
					Thread.sleep(Constants.FACE_DETECTION_SLEEP_INTERVAL_BETWEEN_FRAME);
					long start = System.currentTimeMillis();
					List<FaceInfo> faceInfos = detectOneFrame();
					long end = System.currentTimeMillis()-start;
					//logger.debug("--one frame delay time is :"+end+"ms");
					if (faceInfos.size() > 0) {
						frameHasFaceCount++;
						// Keep a copy of face results in case subsequent frames
						// fail to detect faces.
						prevFaceInfos = faceInfos;
					}
					frameCount++;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			try {
			    semaphoreFaceInfos.acquire();				
				curFaceInfos.clear();
				double frameHasFaceRatio = frameHasFaceCount / (double) frameCount;
				if (frameHasFaceRatio > Constants.FACE_DETECTION_POSITIVE_RATIO) {
					curFaceInfos = prevFaceInfos;
				}
			    semaphoreFaceInfos.release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			reportSuccess();
		}
	}

	@Override
	public boolean init() {
		close();
		isRunning = true;
		boolean openCamera = false;
		try {
			semaphoreCap.acquire();
			cap = new VideoCapture();
			openCamera = cap.open(Constants.DEFAULT_CAMERA_INDEX);
			semaphoreCap.release();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}	
		if (!openCamera) {
			errorMessage = "Failed to open camera id " + Constants.DEFAULT_CAMERA_INDEX;
			return false;
		}
		return true;
	}

	@Override
	public void close() {
		try {
			semaphoreCap.acquire();
			if (cap != null) {
				cap.release();
			}
			cap = null;
			semaphoreCap.release();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}	
		isRunning = false;
	}

	@Override
	public boolean isHealthy() {
		boolean isOpened = false;
		try {
			semaphoreCap.acquire();
			isOpened = cap.isOpened();
			semaphoreCap.release();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}	
		
		if (!isOpened) {
			errorMessage = "Video camera is not opened and seems unhealthy.";
		}
		return isOpened;
	}

	@Override
	public String errorMessage() {
		return errorMessage;
	}

	// Returns current face detection result.
	public List<FaceInfo> getFaceInfos() {
		List<FaceInfo> result = null;
		try {
			semaphoreFaceInfos.acquire();
			result = curFaceInfos;
			semaphoreFaceInfos.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
		return result;
	}

	// Returns current frame with face detection results.
	public BufferedImage getCurFrame() {
		if (curMat.empty()) {
			return new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
		}
		return mat2Img.getImage(curMat);
	}

	// Detects faces on one frame.
	private List<FaceInfo> detectOneFrame() {
		boolean readNewFrame = false;
		try {
			semaphoreCap.acquire();
			if (cap != null) {
				cap.read(curMat);
				readNewFrame = true;
			}
			semaphoreCap.release();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}		
		if (!readNewFrame) {
			return new ArrayList<FaceInfo>(); 
		}
		Imgproc.cvtColor(curMat, curMat, Imgproc.COLOR_RGB2BGR);
		List<Rect> faceRects = faceDetector.detectFace(curMat);
		if (Constants.FACE_DETECTION_ENABLE_DISPLAY) {
			curMat = faceDetector.addFaceRectToMat(curMat, faceRects);
		}
		return faceDetector.convertToFaceInfo(curMat, faceRects);
	}

	// Reports success.
	private void reportSuccess() {
		errorMessage = "";
	}
}
