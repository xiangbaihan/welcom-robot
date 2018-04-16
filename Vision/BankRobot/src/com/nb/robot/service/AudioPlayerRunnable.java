package com.nb.robot.service;

import java.io.File;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

import org.apache.log4j.Logger;

import com.nb.robot.common.Constants;

public class AudioPlayerRunnable implements Runnable {
	private static Logger logger = Logger.getLogger(AudioPlayerRunnable.class);
	
	private boolean isRunning = false;
	private String audioFile;
	private int repeat;
	private Clip clip = null;
	private String errorMessage = "";
	private long startTimeInMs = 0;


	/**
	 * @param audioFile Audio file path.
	 * @param repeat The times the audioFile will be played
	 */
	public AudioPlayerRunnable(String audioFile, int repeat) {
		this(audioFile, 0L, repeat); 
	}

	/**
	 * @param audioFile Audio file path.
	 * @param startTimeInMs The elapsed time in milliseconds for the *first* time play.
	 * @param repeat The times the audioFile will be played
	 */
	public AudioPlayerRunnable(String audioFile, long startTimeInMs, int repeat) {
		this.audioFile = audioFile;
		this.startTimeInMs = startTimeInMs;
		this.repeat = repeat;
		try {
			clip = AudioSystem.getClip();
			LineListener listener = new LineListener() {
				public void update(LineEvent event) {
					if (event.getType() == LineEvent.Type.STOP) {
						clip.close();
					}
				}
			};
			clip.addLineListener(listener);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		isRunning = false;
	}

	@Override
	public void run() {
		if (clip == null) {
			errorMessage = "Clip is not initialized.";
			logger.error(errorMessage());
			return;
		}

		isRunning = true;
		int count = 0;
		while (!Thread.currentThread().isInterrupted() && isRunning && count < repeat) {
			try {
				clip.open(AudioSystem.getAudioInputStream(new File(audioFile)));
				// Set start time for the first play.
				if (count == 0) {
					clip.setMicrosecondPosition(startTimeInMs * 1000);
				}
				clip.start();
			} catch (Exception exc) {
				exc.printStackTrace();
				errorMessage = "Failed to play audio file " + audioFile;
				logger.error(errorMessage());
				break;
			}
			while(clip.isOpen()) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
			}
			count++;
			
			// Sleep between repeats.
			try {
				Thread.sleep(Constants.SPEECH_SYNTHESIS_REPEAT_SLEEP_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
				continue;
			}
		}
		if (count >= repeat) {
			errorMessage = "";
		}		
	}
	
	public void stop() {
		isRunning = false;
		if(clip != null) {
			clip.stop();
			clip.close();
		}
	}
	
	// Same as above, but returns current play time in milliseconds.
	public long stopWithCurrentPosition() {
		isRunning = false;
		long currentTimeMs = 0;
		if(clip != null) {
			currentTimeMs = clip.getMicrosecondPosition() / 1000;
			clip.stop();
			clip.close();
		}
		return currentTimeMs;
	}
	
	public String errorMessage() {
		return errorMessage;
	}

}
