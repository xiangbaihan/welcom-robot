package com.nb.robot.serialComm;

// 底盘的状态 xy坐标和th角度，v速度和w角速度
public class ChasisMotionState {
	private int x = 0;
	private int y = 0;
	private int th = 0;
	private int v = 0;
	private int w = 0;

	public ChasisMotionState() {
	}

	public ChasisMotionState(int x, int y, int th) {
		this.x = x;
		this.y = y;
		this.th = th;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getTh() {
		return th;
	}

	public int getV() {
		return v;
	}

	public int getW() {
		return w;
	}
}
