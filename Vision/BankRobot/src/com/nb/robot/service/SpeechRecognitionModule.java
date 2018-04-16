package com.nb.robot.service;

import org.apache.log4j.Logger;

import com.nb.robot.common.CommonUtils;
import com.nb.robot.common.Constants;
import com.nb.robot.common.FunctionModule;
import com.nb.robot.common.PropertiesUtil;
import com.nb.robot.serialComm.ControlMotion;

// Speech recognition module. It depends on MscModule and requires MscModule.start() first.
// This is a singleton class.
public class SpeechRecognitionModule implements FunctionModule {
	private static Logger logger = Logger.getLogger(SpeechRecognitionModule.class);
	
	private static volatile SpeechRecognitionModule instance = null;
	private SpeechRecognitionRunnable speechRecognitionRunnable;
	private Thread speechRecognitionThread;
//	private String bnfFilePath = Constants.SPEECH_RECOGNITION_DEFAULT_BNF;
	private String bnfFilePath;
	private boolean isRunning = false;
	
	public static SpeechRecognitionModule getInstance() {
		if (instance == null) {
			synchronized (HumanDetectionModule.class) {
				if (instance == null) {
					instance = new SpeechRecognitionModule();
				}
			}
		}
		return instance;
	}

	private SpeechRecognitionModule() {
		isRunning = false;
	};
	
	@Override
	public synchronized boolean start() {
		if (isRunning) {
			return true;
		}
		
		bnfFilePath = PropertiesUtil.getPropFromProperties("bnf.path");
		bnfFilePath = CommonUtils.getSymbolicLinkTarget(bnfFilePath);
		
		speechRecognitionRunnable = new SpeechRecognitionRunnable(bnfFilePath);
		if (!speechRecognitionRunnable.init()) {
			logger.error("Failed to init speechRecognitionRunnable: " + speechRecognitionRunnable.errorMessage());
			return false;
		}
		speechRecognitionThread = new Thread(speechRecognitionRunnable);
		speechRecognitionThread.start();
		isRunning = true;
		logger.info("SpeechRecognitionModule started");
		return true;
	}

	@Override
	public synchronized void stop() {		
		if (speechRecognitionRunnable != null) {
			speechRecognitionRunnable.close();
		}
		if (speechRecognitionThread != null) {
			speechRecognitionThread.interrupt();
			try {
				speechRecognitionThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		speechRecognitionRunnable = null;
		speechRecognitionThread = null;
		
		isRunning = false;
		logger.info("SpeechRecognitionModule stopped");
	}

	@Override
	public synchronized boolean isHealthy() {
		if (speechRecognitionRunnable == null) {
			return false;
		}
		return speechRecognitionRunnable.isHealthy();
	}

	@Override
	public synchronized String errorMessage() {
		if (speechRecognitionRunnable == null) {
			return "Speech recognition thread is not initialized.";
		}
		return speechRecognitionRunnable.errorMessage();
	}
	
	// Resets BNF file path. This function must be called after stop and before start() to make
	// the new BNF file effective.
	public void setBnfFilePath(String bnfFilePath) {
		this.bnfFilePath = bnfFilePath;
	}
}
