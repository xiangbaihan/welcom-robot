package com.nb.robot.service;

public class AudioPlayerRunnableTest {

	public static void main(String[] args) {
		AudioPlayerRunnable aduio = new AudioPlayerRunnable("tts_test.wav", 5000, 2);
		Thread aduioThread=new Thread(aduio);
		aduioThread.start();
		try {
			aduioThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
