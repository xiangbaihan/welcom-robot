package com.nb.robot.serialComm;

import java.io.IOException;

import com.nb.robot.common.CommonUtils;
import com.nb.robot.serialComm.ControlBattery.batteryReadCallBack;
import com.nb.robot.serialComm.ControlMotion.readCallBack;

public class SeriaPortTest {
	public static void main(String[] argv) throws IOException, InterruptedException
	{
		//控制机器人手臂旋转
//		ControlArm controlServo=new ControlArm(CommonUtils.getSymbolicLinkTarget("/dev/arm"));
//		controlServo.setArmMotion(3, 180, 2);
//		controlServo.setAngle(0x01, 1500);
//		
//		controlServo.setSpeed(0x01,10);
//		controlServo.setAngle(0x01, 200);
		
//		controlServo.closePort();
		
		//控制机器人轮子运动
//		ControlMotion controlMotion=new ControlMotion(CommonUtils.getSymbolicLinkTarget("/dev/chasis"));
		//controlMotion.initialChasisMotion();		
//		controlMotion.setReadCallBack(new readCallBack() {
//			@Override
//			public void readSuccess(ChasisMotionState state) {
//				System.out.println("获取到底盘的X坐标是 : "+state.getX());
//			}
//
//		});
//		
//		controlMotion.setChasisMotion(2, 0, 5000, 2);
		
//		Thread.sleep(10000);
		
	//	controlMotion.setChasisMotion(ChasisMotionDirection.forword, 1000, ChasisMotionDirection.forword, 0,10000);
		
		
//		controlMotion.closePort();
		//ee aa 00 00 00  00 ff fd 01 00 00 bb 
		
		
//		ControlLED controlLED=new ControlLED("/dev/ttyS99");
//		controlLED.sendDynamicExpression(3, new byte[]{0x10,0x7f,(byte) 0xff}, new byte[]{0x10,(byte) 0xff,(byte) 0x80});
//		controlLED.closePort();
		
//		ControlExpression controlExpression  = new ControlExpression(CommonUtils.getSymbolicLinkTarget("/dev/expression"));
//		controlExpression.sendStaticExpression((byte)1);
//		controlExpression.closePort();
		
		ControlBattery controlBattery = new ControlBattery(CommonUtils.getSymbolicLinkTarget("/dev/battery"));
	//	ControlBattery controlBattery = new ControlBattery("/dev/ttyUSB2");
		controlBattery.sendMsgToBattery();
		
		//Thread.sleep(2000);
		controlBattery.setReadCallBack(new batteryReadCallBack() {
			@Override
			public void readSuccess(BatteryState state) {
				// TODO Auto-generated method stub
				System.out.println("电池容量："+state.getSoc());
			}
		});
		controlBattery.closePort();
//		byte[] bs =new byte[10];
//		System.in.read(bs, 0, 10);
	}
}
