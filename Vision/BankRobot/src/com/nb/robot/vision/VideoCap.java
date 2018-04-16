package com.nb.robot.vision;

import java.awt.image.BufferedImage;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class VideoCap {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	VideoCapture cap;
	Mat2Image mat2Img = new Mat2Image();
	FaceDetector faceDetector = new FaceDetector();

	public VideoCap() {
		cap = new VideoCapture();
		cap.open(0);  // Default camera.
		// cap.open("rtp://230.0.0.1:5555");  // RTP  
	}
	
	public void close() {		
		cap.release();
	}

	// Returns one image with detection results.
	public BufferedImage getOneFrame() {
		Mat mat = getOneMat();
		List<Rect> faceRects = faceDetector.detectFace(mat);
		mat = faceDetector.addFaceRectToMat(mat, faceRects);
		return mat2Img.getImage(mat);
	}
	
	// Returns one frame as Mat.
	private Mat getOneMat() {
		Mat mat = new Mat();
		cap.read(mat);
		Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2BGR);
		return mat;		
	}
}
