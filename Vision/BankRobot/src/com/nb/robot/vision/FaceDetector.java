package com.nb.robot.vision;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import com.nb.robot.common.Constants;
import com.nb.robot.service.ExpressionControlModule;

public class FaceDetector {
	private static Logger logger = Logger.getLogger(FaceDetector.class);

	static CascadeClassifier frontalFaceDetector;
	static CascadeClassifier profileFaceDetector;
	static CascadeClassifier eyeDetector;

	public FaceDetector() {
		frontalFaceDetector = new CascadeClassifier("/home/mj/workspace/BankRobot/haarcascade_frontalface_alt.xml");
		profileFaceDetector = new CascadeClassifier("/home/mj/workspace/BankRobot/haarcascade_profileface.xml");
		eyeDetector = new CascadeClassifier("/home/mj/workspace/BankRobot/haarcascade_eye_tree_eyeglasses.xml");
	}

	// Detects faces in the given Mat.
	public List<Rect> detectFace(Mat mat) {
		// Convert the current frame to grayscale.
		Mat gray_mat = new Mat();
		Imgproc.cvtColor(mat, gray_mat, Imgproc.COLOR_BGR2GRAY);
		
		List<Rect> faces = new ArrayList<Rect>();
		double w = (double) mat.width();
		double h = (double) mat.height();
		
		// Detect frontal faces.
		MatOfRect faceDetections = new MatOfRect();
		frontalFaceDetector.detectMultiScale(gray_mat, faceDetections, 1.1, 3, Objdetect.CASCADE_DO_CANNY_PRUNING,
				new Size(0.05 * w, 0.05 * h), new Size(0.8 * w, 0.8 * h));
		faces.addAll(faceDetections.toList());
		// Detect right profile faces.
		faceDetections = new MatOfRect();
		profileFaceDetector.detectMultiScale(gray_mat, faceDetections, 1.1, 3, Objdetect.CASCADE_DO_CANNY_PRUNING,
				new Size(0.05 * w, 0.05 * h), new Size(0.8 * w, 0.8 * h));
		faces.addAll(faceDetections.toList());

		// Detect left profile faces.
		faceDetections = new MatOfRect();
		// flipCode = 1 for horizontal flip, along y-axis in mat.
		Mat flipped_mat = new Mat();
		Core.flip(gray_mat, flipped_mat, /*flipCode=*/1);
		profileFaceDetector.detectMultiScale(flipped_mat, faceDetections, 1.1, 3, Objdetect.CASCADE_DO_CANNY_PRUNING,
				new Size(0.05 * w, 0.05 * h), new Size(0.8 * w, 0.8 * h));
		for (Rect leftProfileInFlippedMat : faceDetections.toArray()) {
			Rect leftProfileInOriginalMat = leftProfileInFlippedMat;
			leftProfileInOriginalMat.x = mat.width() - (leftProfileInFlippedMat.x + leftProfileInFlippedMat.width);
			faces.add(leftProfileInOriginalMat);
		}		
		logger.trace("CascadeClassifier detected face number: " + faces.size());
		
		// Merge overlapped faces.
		MatOfRect allMatOfRect = new MatOfRect();
		// NOTE: this is a hack!
		// groupRectangles() needs at least groupThreshold(1)+1=2 overlapped Rects
		// to include the Rect in the result.
		faces.addAll(faces);
		allMatOfRect.fromList(faces);
		Objdetect.groupRectangles(allMatOfRect, new MatOfInt(), /*groupThreshold*/1);
		logger.trace("Grouped face number: " + allMatOfRect.size());
		
		return allMatOfRect.toList();
	}

	// Converts a list of Rect into FaceInfo.
	public List<FaceInfo> convertToFaceInfo(Mat mat, List<Rect> faceRects) {
		List<FaceInfo> faceInfos = new ArrayList<FaceInfo>();
		for (Rect faceRect : faceRects) {
			faceInfos.add(new FaceInfo(faceRect.width, faceRect.height,
					ComputeFacePositionDegree(mat.width(), faceRect.x + faceRect.width / 2)));
		}
		return faceInfos;
	}
	
	// Adds a list of Rect into Mat for display purpose.
	public Mat addFaceRectToMat(Mat mat, List<Rect> faceRects) {
		for (Rect faceRect : faceRects) {
			Imgproc.rectangle(mat, new Point(faceRect.x, faceRect.y),
					new Point(faceRect.x + faceRect.width, faceRect.y + faceRect.height), new Scalar(0, 255, 0));
		}
		return mat;
	}

	private int ComputeFacePositionDegree(int imageWidth, int position) {
		return (int) ((position - imageWidth / 2) / (double) imageWidth * Constants.CAMERA_FIELD_OF_VIEW);
	}

	// Detects eyes based on faces and draws them on Mat.
	// NOTE: this is not used for now.
	private Mat detectEyes(Mat mat, Rect[] faceRects) {
		// Convert the current frame to grayscale.
		Mat gray_mat = new Mat();
		Imgproc.cvtColor(mat, gray_mat, Imgproc.COLOR_BGR2GRAY);
		for (Rect faceRect : faceRects) {
			// -- In each face, detect eyes
			Mat faceROI = gray_mat.submat(faceRect);
			MatOfRect eyes = new MatOfRect();
			eyeDetector.detectMultiScale(faceROI, eyes);
			for (Rect rectEye : eyes.toArray()) {
				Point center1 = new Point(faceRect.x + rectEye.x + rectEye.width * 0.5,
						faceRect.y + rectEye.y + rectEye.height * 0.5);
				int radius = (int) Math.round((rectEye.width + rectEye.height) * 0.25);
				Imgproc.circle(mat, center1, radius, new Scalar(255, 0, 0), 4, 8, 0);
			}
		}
		return mat;
	}
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
}
