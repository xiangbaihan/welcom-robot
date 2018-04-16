package com.nb.robot.serialComm;

import gnu.io.SerialPort;

public class ControlArm extends BaseSerialControl {
	private final int baud = 57600;
	private static final double ANGEL_PER = 4095 * 1.0 / 360;// 旋转角度
	private static final double SPEED_PER = 1023 * 1.0 / 100;// 旋转速度
	private int armPosition_left , armPosition_right;

	public ControlArm(String portName) {
		super.openSerialPort(portName, baud, SerialPort.PARITY_NONE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1);
	}

	public ControlArm(String portName, int baud, int parity, int databits, int stopbits) {
		super.openSerialPort(portName, baud, parity, databits, stopbits);
	}

	public boolean setArmMotion(int armPart, int armPosition, int armSpeed) {
		//the left arm is out of sync with the right arm. right armPosition = - left armPostition + 310
		//Beause of the left arm installation is not reasonable. If armPart =3 or =2 ,the armPostition range  
		// is better for 0~310.
		
		if(armPart==3) {
			armPosition_left = armPosition;
			armPosition_right = -armPosition+310;
			armPosition_left = (int) (armPosition_left * ANGEL_PER);
			armPosition_right = (int) (armPosition_right * ANGEL_PER);
		}else if(armPart==2){
			armPosition = -armPosition+310;
			armPosition = (int) (armPosition * ANGEL_PER);
		}else {
			armPosition = (int) (armPosition * ANGEL_PER);
		}

		if (armSpeed == 10)
			armSpeed = 0;// 最大转速
		else {
			armSpeed = (int) (armSpeed * SPEED_PER);
		}

		try {
			if (armPart <= 0 || armPart >= 3) {
				// 0xfe for both arms.
				armPart = 0xfe;
			}
			
			setSpeed(armPart, armSpeed);
			Thread.sleep(50);// 必须加延时，否则speed和angel的指令冲突，无法被执行
			if(armPart==0xfe) {
				setAngle(1, armPosition_left);
				Thread.sleep(50);
				setAngle(2, armPosition_right);
			}else {
				setAngle(armPart, armPosition);
			}
			
			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 设置舵机速度
	 * 
	 * @param i
	 *            十六进制的舵机编号，0xfe表示所有舵机
	 * @param speedValue
	 *            速度值
	 * @return 发送是否成功
	 */
	private boolean setSpeed(int id, int speedValue) {
		if (isOpen == false) {
			return false;
		}
		byte[] pBuff = new byte[10];
		pBuff[0] = (byte) 0xff;
		pBuff[1] = (byte) 0xff;
		pBuff[2] = (byte) id;
		pBuff[3] = 0x05;
		pBuff[4] = 0x03;
		pBuff[5] = 0x20;
		pBuff[6] = (byte) (speedValue % 256);
		pBuff[7] = (byte) (speedValue / 256);
		pBuff[8] = (byte) ~(pBuff[2] + pBuff[3] + pBuff[4] + pBuff[5] + pBuff[6] + pBuff[7]);

		if (sendMsg(pBuff, 9)) {
			return true;
		}
		return false;
	}

	/**
	 * 设置舵机角度
	 * 
	 * @param id
	 *            左右舵机编号或所有舵机
	 * @param angle
	 *            角度值
	 * @return 发送是否成功
	 */
	private boolean setAngle(int id, int angle) {
		if (isOpen == false) {
			return false;
		}

		byte[] pBuff = new byte[10];
		pBuff[0] = (byte) 0xff;
		pBuff[1] = (byte) 0xff;
		pBuff[2] = (byte) id;
		pBuff[3] = 0x05;
		pBuff[4] = 0x03;
		pBuff[5] = 0x1E;
		pBuff[6] = (byte) (angle % 256);
		pBuff[7] = (byte) (angle / 256);
		pBuff[8] = (byte) ~(pBuff[2] + pBuff[3] + pBuff[4] + pBuff[5] + pBuff[6] + pBuff[7]);

		if (sendMsg(pBuff, 9)) {
			return true;
		}
		return false;
	}
}
