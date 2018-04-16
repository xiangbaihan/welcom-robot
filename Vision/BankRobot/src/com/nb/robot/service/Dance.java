package com.nb.robot.service;

import java.util.List;

// 一套舞蹈
public class Dance {
	int time;// 时间，单位为秒
	List<ArmControlRequest> armActionList;// 一组手臂动作
	List<MotionControlRequest> motionActionList;// 一组底盘动作
	List<ExpressionControlRequest> expressionActionList;// 一组表情

	Dance(int time, List<ArmControlRequest> armActionList, List<MotionControlRequest> motionActionList,
			List<ExpressionControlRequest> expressionActionList) {
		this.armActionList = armActionList;
		this.time = time;
		this.motionActionList = motionActionList;
		this.expressionActionList = expressionActionList;
	}

	public int getTime() {
		return time;
	}

	public List<ArmControlRequest> getArmActionList() {
		return armActionList;
	}

	public List<MotionControlRequest> getMotionActionList() {
		return motionActionList;
	}

	public List<ExpressionControlRequest> getExpressionnActionList() {
		return expressionActionList;
	}

	@Override
	public String toString() {
		return "Move [time=" + time + ", armActionList.size()=" + armActionList.size() + ", motionActionList.size()="
				+ motionActionList.size() + ", expressionActionList.size()=" + expressionActionList.size() + "]";
	}
}
