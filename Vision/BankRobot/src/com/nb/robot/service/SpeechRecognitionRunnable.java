package com.nb.robot.service;

import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nb.robot.common.Constants;
import com.nb.robot.common.HardwareModule;
import com.nb.robot.common.Pair;
import com.nb.robot.server.SocketServerModule;
import com.nb.robot.xf.AsrNative;
import com.nb.robot.xf.UserData;

public class SpeechRecognitionRunnable implements Runnable, HardwareModule {
	private static Logger logger = Logger.getLogger(SpeechRecognitionRunnable.class);

	private SocketServerModule socketServer = SocketServerModule.getInstance();
	private String bnfFilePath;
	private AsrNative asrObj = null;
	private UserData userData = null;
	private String errorMessage = "";
	private boolean isRunning = true;
	private Semaphore semaphore = new Semaphore(1);
	// Timestamp of last time sending a socket message on unrecognized speech.
	private long lastUnrecognizedMessageTimestamp = 0;
	
	long startTime,endTime;

	public SpeechRecognitionRunnable(String bnfFilePath) {
		this.bnfFilePath = bnfFilePath;
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted() && isRunning) {
			try {
				Thread.sleep(Constants.SPEECH_RECOGNITION_MODULE_SLEEP_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
				continue;
			}
          
//			// Re-init AsrNative if necessary.
			if (!isHealthy() && !init()) {
				logger.error("SpeechRecognitionRunnable is not healthy: " + errorMessage());
				continue;
			}
           
			try {
				semaphore.acquire();
				userData = asrObj.runAsr(userData);
				semaphore.release();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			if (userData.getErrorCode() != Constants.DEFAULT_ERROR_CODE_OK) {
				logger.error("Speech recognition returned error: " + userData.getErrorCode());
				continue;
			}
			startTime = System.currentTimeMillis();
			SpeechRecognitionResponse response = SpeechRecognitionUtils.parseAsrResult(userData.getResult());
			endTime = System.currentTimeMillis();
	        logger.debug("speechRecognition need time:"+(float)(endTime-startTime)+"ms");
			if (response == null) {
				logger.debug("No speech recognition result.");
				continue;
			}
			String jsonString = "";
			try {
				JSONArray array = new JSONArray();
				for (Pair<String, String> pair : response.getKeywords()) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("slot", pair.getLeft());
					jsonObject.put("word", pair.getRight());
					array.put(jsonObject);
				}
				jsonString = new JSONObject().put("type", 3).put( "fullText", response.getFullText()).put("keywords", array)
						.put("isRecognized", response.getIsRecognized()).put("timestamp", response.getTimeStamp())
						.toString();
			
			} catch (JSONException e) {
				e.printStackTrace();
			}
			logger.info(jsonString);
            
			// We always send socket messages on recognized speech.
			if (response.getIsRecognized()) {
				socketServer.sendMessage(jsonString);
			} else {
				// We do not send too many socket messages on unrecognized speech.
				if (System.currentTimeMillis() - lastUnrecognizedMessageTimestamp > Constants.SPEECH_UNRECOGNIZED_MESSAGE_INTERVAL) {
					socketServer.sendMessage(jsonString);
				}
				lastUnrecognizedMessageTimestamp = System.currentTimeMillis();
			}
		}
	}

	// Assume MscModule has started successfully. Otherwise, this init() will fail.
	@Override
	public boolean init() {
		close();
		isRunning = true;
		try {
			semaphore.acquire();
			asrObj = new AsrNative();
			userData = asrObj.buildGrammar(bnfFilePath);
			semaphore.release();
			return true;
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			errorMessage = e1.getMessage();
		}
		if (userData.getErrorCode() != Constants.DEFAULT_ERROR_CODE_OK) {
			errorMessage = "Build grammar failed. Error code: " + userData.getErrorCode();
			return false;
		}
		return true;
	}

	@Override
	public void close() {
		try {
			semaphore.acquire();
			asrObj = null;
			userData = null;
			semaphore.release();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			errorMessage = e1.getMessage();
		}
		isRunning = false;
	}

	@Override
	public boolean isHealthy() {
		return userData != null && userData.getErrorCode() == Constants.DEFAULT_ERROR_CODE_OK;
	}

	@Override
	public String errorMessage() {
		return errorMessage;
	}

}
