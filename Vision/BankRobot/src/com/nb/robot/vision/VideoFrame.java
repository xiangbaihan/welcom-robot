package com.nb.robot.vision;

import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.nb.robot.common.Constants;

public class VideoFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private VideoCap videoCap = null;
	private FaceDetectionRunnable faceDetectionRunnable = null;
	
	/**
	 * Create the frame.
	 */
	public VideoFrame(VideoCap videoCap) {
		this.videoCap = videoCap;
		initAndStart(JFrame.EXIT_ON_CLOSE);
	}
	
	public VideoFrame(FaceDetectionRunnable faceDetectionRunnable) {
		this.faceDetectionRunnable = faceDetectionRunnable;
		initAndStart(JFrame.DISPOSE_ON_CLOSE);
	}
	
	private void initAndStart(int closeOperation) {		
		setDefaultCloseOperation(closeOperation);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		new MyThread().start();		
	}

	public void paint(Graphics g) {
		g = contentPane.getGraphics();
		if (videoCap != null) {
			g.drawImage(videoCap.getOneFrame(), 0, 0, this);
		} else {
			g.drawImage(faceDetectionRunnable.getCurFrame(), 0, 0, this);			
		}
	}

	class MyThread extends Thread {
		@Override
		public void run() {
			for (;;) {
				repaint();
				try {
					Thread.sleep(Constants.FACE_DETECTION_DISPLAY_REFRESH_INTERVAL);
				} catch (InterruptedException e) {
				}
			}
		}
	}
}
