package com.nb.robot.service;

import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import com.nb.robot.common.Constants;
import com.nb.robot.common.FunctionModule;
import com.nb.robot.common.UtilStatus;
import com.nb.robot.xf.TtsNative;

// Speech synthesis (text-to-speech) module. It depends on MscModule and requires MscModule.start() first.
// This is a singleton class.
public class SpeechSynthesisModule implements FunctionModule {
	private static Logger logger = Logger.getLogger(SpeechSynthesisModule.class);
	
	private static volatile SpeechSynthesisModule instance = null;
	
	TtsNative ttsObj = null;
	AudioPlayerRunnable audioPlayerRunnable;
	private Thread audioPlayerRunnableThread;
	private String errorMessage = "";
	private Semaphore semaphore = new Semaphore(1);
	
	public static SpeechSynthesisModule getInstance() {
		if (instance == null) {
			synchronized (HumanDetectionModule.class) {
				if (instance == null) {
					instance = new SpeechSynthesisModule();
				}
			}
		}
		return instance;
	}

	private SpeechSynthesisModule() {
	}
	
	// Processes the given SpeechSynthesisRequest.
	public synchronized UtilStatus run(SpeechSynthesisRequest request) {
		if (!isHealthy()) {
			return new UtilStatus(-1, errorMessage());
		}
		if (ttsObj == null) {
			errorMessage = "SpeechSynthesisModule is not initialized.";
			return new UtilStatus(-1, errorMessage());
		}
		stopAudioPlayer();
		
		int ret = ttsObj.runTts(Constants.SPEECH_SYNTHESIS_DEFAULT_FILE,
				request.getContent(), request.getSpeed(), request.getVolume(), request.getPitch());
		if (ret != Constants.DEFAULT_ERROR_CODE_OK) {
			errorMessage = "Failed in speech synthesis (text-to-speech). Error code: " + ret;
			return new UtilStatus(ret, errorMessage());
		}
		audioPlayerRunnable = new AudioPlayerRunnable(Constants.SPEECH_SYNTHESIS_DEFAULT_FILE,
				request.getRepeat());
		audioPlayerRunnableThread = new Thread(audioPlayerRunnable);
		audioPlayerRunnableThread.start();
		return new UtilStatus();
	}
	
	@Override
	public synchronized boolean start() {
		try {
			semaphore.acquire();
			ttsObj = new TtsNative();
			semaphore.release();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			errorMessage = e1.getMessage();
		}
		logger.info("SpeechSynthesisModule started.");
		return true;
	}

	@Override
	public synchronized void stop() {
		stopAudioPlayer();
		try {
			semaphore.acquire();
			ttsObj = null;
			semaphore.release();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			errorMessage = e1.getMessage();
		}
	}

	@Override
	public synchronized boolean isHealthy() {
		return errorMessage.isEmpty();
	}

	@Override
	public synchronized String errorMessage() {
		return errorMessage;
	};
	
	// Stops current audio playing if any.
	private synchronized void stopAudioPlayer() {
		if (audioPlayerRunnable != null) {
			audioPlayerRunnable.stop();
		}
		
		if (audioPlayerRunnableThread != null) {
			audioPlayerRunnableThread.interrupt();
			try {
				audioPlayerRunnableThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		audioPlayerRunnable = null;
		audioPlayerRunnableThread = null;
	}

}
