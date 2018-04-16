package com.nb.robot.vision;

import java.awt.EventQueue;

public class VideoFrameMain {
	static VideoCap videoCap = new VideoCap();
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VideoFrame frame = new VideoFrame(videoCap);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
