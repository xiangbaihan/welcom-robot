package com.nb.robot.vision;

// Represents detected face information.
public class FaceInfo {
	// Face width in pixel.
	int width;
	// Face height in pixel.
	int height;
	// Face angle position: facing straightforward = 0, right hand side is >0 (clockwise)
	int degree;
	
	public FaceInfo(int width, int height, int drgree) {
		this.width = width;
		this.height = height;
		this.degree = drgree;		
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getDegree() {
		return degree;
	}

}
