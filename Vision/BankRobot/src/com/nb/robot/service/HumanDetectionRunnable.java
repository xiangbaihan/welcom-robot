package com.nb.robot.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.nb.robot.common.Constants;
import com.nb.robot.lidar.LidarRunnable;
import com.nb.robot.lidar.MeasurementNode;
import com.nb.robot.server.SocketServerModule;
import com.nb.robot.vision.FaceDetectionRunnable;
import com.nb.robot.vision.FaceInfo;

// A runnable for human detection based on FaceDetectionRunnable and LidarRunnable.
public class HumanDetectionRunnable implements Runnable {
	private static Logger logger = Logger.getLogger(HumanDetectionRunnable.class);
	private final static int MAX_DEGREE = 360;

	private SocketServerModule socketServer = SocketServerModule.getInstance();
	private AudioPlayerRunnable audioPlayerRunnable;
	private Thread audioPlayerRunnableThread;
	private FaceDetectionRunnable faceDetectionRunnable;
	private LidarRunnable lidarRunnable;
	private boolean isRunning = true;

	// State definition:
	// 0: no humans. Default.
	// 1: Humans appear in the large range && previous state is 0.
	// 2: Humans appear in the small range.
	// 3: Humans appear in the large range && previous state is 2.
	private int humanDetectionState;
	private int humanNumState;

	// Based on face detection result.
	private int curFaceNumInSmallRange;
	private int curFaceNumInLargeRange;
	// Based on LIDAR scan result.
	private boolean hasObjectInSmallRange;
	private long lastHumanDetectionReportTimestamp;

	HumanDetectionRunnable(FaceDetectionRunnable faceDetectionRunnable, LidarRunnable lidarRunnable) {
		this.faceDetectionRunnable = faceDetectionRunnable;
		this.lidarRunnable = lidarRunnable;
		humanDetectionState = 0;
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted() && isRunning) {
			try {
				Thread.sleep(Constants.HUMAN_DETECTION_MODULE_SLEEP_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
				continue;
			}

			curFaceNumInSmallRange = 0;
			curFaceNumInLargeRange = 0;
			hasObjectInSmallRange = false;
			List<MeasurementNode> lidarInfos = lidarRunnable.getLidarInfos();
			// List<MeasurementNode> lidarInfos = lidarRunnable.getLidarInfosForTesting();
			if (lidarInfos == null || lidarInfos.size() == 0) {
				logger.error("No LIDAR scan result: " + lidarRunnable.errorMessage());
				continue;
			}
			hasObjectInSmallRange = detectObjectWithinRange(lidarInfos, Constants.HUMAN_DETECTION_RANGE_SMALL,
					Constants.HUMAN_DETECTION_RANGE_ANGLE);
			List<FaceInfo> faceInfos = faceDetectionRunnable.getFaceInfos();
			if (faceInfos == null || faceInfos.size() == 0) {
				logger.trace("No face detection result.");
			}
			for (FaceInfo faceInfo : faceInfos) {
				int faceDegree = (faceInfo.getDegree() + MAX_DEGREE) % MAX_DEGREE;
				float distance = getObjectDistanceByDegree(lidarInfos, faceDegree);
				if (distance == Float.MAX_VALUE) {
					logger.warn("Failed to find a valid LIDAR read distance around degree " + faceDegree);
					continue;
				}
				if (distance < Constants.HUMAN_DETECTION_RANGE_SMALL) {
					curFaceNumInSmallRange++;
				} else if (distance < Constants.HUMAN_DETECTION_RANGE_LARGE) {
					curFaceNumInLargeRange++;
				}
			}
			// Keep the current human detection state before it may change.
			int oldHumanDetectionState = humanDetectionState;
			decideHumanDetectionState();
			HumanDetectionResponse response = reportState();
			String jsonString = "";
			try {
				jsonString = new JSONObject().put("type", 2).put("state", response.getState())
						.put("number", response.getNumber()).put("timestamp", response.getTimeStamp()).toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// Send human detection state change to clients.
			if (oldHumanDetectionState != humanDetectionState) {
				logger.debug(jsonString);
				socketServer.sendMessage(jsonString);
				// Play the audio file, its content is the status of the current with speech synthesis
				String audioFile = Constants.SPEECH_STATE_AUDIO_FILE_PREFIX+"state_" + humanDetectionState + ".wav";
				audioPlayerRunnable = new AudioPlayerRunnable(audioFile, 1);
				audioPlayerRunnableThread = new Thread(audioPlayerRunnable);
				audioPlayerRunnableThread.start();
			}
		}

	}

	public void close() {
		isRunning = false;
	}

	// Returns whether there is any object within the given distance range and
	// angle range (0 +/- angleRange), based on LIDAR scan result.
	private boolean detectObjectWithinRange(List<MeasurementNode> lidarInfos, float distanceRange, int angleRange) {
		for (MeasurementNode node : lidarInfos) {
			if (node.getSyncQuality() <= 0) {
				continue;
			}
			if (node.getDistance() >= distanceRange) {
				continue;
			}
			if (node.getAngle() < angleRange || (MAX_DEGREE - node.getAngle()) < angleRange) {
				return true;
			}
		}
		return false;
	}

	// Finds the distance of object along the given degree. Since lidarInfos
	// does not always contain exactly 360 degrees, the closest distance of
	// MeasurementNode within a small degree range is used.
	// NOTE: make sure the degrees in FaceInfo and MeasurementNode are aligned!
	private float getObjectDistanceByDegree(List<MeasurementNode> lidarInfos, int degree) {
		float minDegreeDiff = Float.MAX_VALUE;
		float distance = Float.MAX_VALUE;
		for (MeasurementNode node : lidarInfos) {
			if (node.getSyncQuality() <= 0) {
				continue;
			}
			float degreeDiff = Math.abs(node.getAngle() - degree);
			if (degreeDiff <= minDegreeDiff && degreeDiff <= Constants.HUMAN_DETECTION_MAX_DEGREE_ALIGNMENT_DIFF) {
				minDegreeDiff = degreeDiff;
				// Keep the smallest distance within an allowed degree range.
				distance = Math.min(distance, node.getDistance());
			}
		}
		return distance;
	}

	// Decides state based on number of faces in various ranges.
	private void decideHumanDetectionState() {

		// If face is not seen but some object is found in small range, and face
		// was seen recently (i.e., humanDetectionState is not 0), update state to 2.
		// This helps the situation when face detection fails for a long time, e.g., >
		// 15s.
		if (hasObjectInSmallRange && humanDetectionState > 0) {
			humanDetectionState = 2;
			return;
		}
        
		long now = System.currentTimeMillis();
		if (curFaceNumInSmallRange == 0 && curFaceNumInLargeRange == 0) {
			//If faces disappear for such long time(5s), we report humans have left. 
			if (now - lastHumanDetectionReportTimestamp > Constants.HUMAN_DETECTION_FACE_LOST_DURATION) {
				humanDetectionState = 0;
				humanNumState = 0;
			}
			return;
		}
		lastHumanDetectionReportTimestamp = now;
		
		// If face is seen in small range, update state to 2.
		if (curFaceNumInSmallRange > 0) {
			humanDetectionState = 2;
			humanNumState = curFaceNumInSmallRange;
			return;
		}

		// If face is seen in large range, update state to 1 or 3.
		if (curFaceNumInLargeRange > 0) {
			if (humanDetectionState == 0) {
				humanDetectionState = 1;
			} else if (humanDetectionState == 2) {
				humanDetectionState = 3;
			}
			humanNumState = curFaceNumInLargeRange;
			return;
		}
	}

	// Generates HumanDetectionResponse based on human detection state.
	private HumanDetectionResponse reportState() {
		return new HumanDetectionResponse(humanDetectionState, humanNumState, System.currentTimeMillis());
	}
}
