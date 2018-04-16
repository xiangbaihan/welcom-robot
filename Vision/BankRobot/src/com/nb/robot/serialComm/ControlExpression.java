package com.nb.robot.serialComm;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import gnu.io.SerialPort;

public class ControlExpression extends BaseSerialControl {
	private static Logger logger = Logger.getLogger(ControlExpression.class);
	private static Timer timer = new Timer();
	private static TimerTask task;
	private int baud = 9600;

	public ControlExpression(String portName) {
		super.openSerialPort(portName, baud, SerialPort.PARITY_NONE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1);
	}

	public ControlExpression(String portName, int baud, int parity, int databits, int stopbits) {
		super.openSerialPort(portName, baud, parity, databits, stopbits);
	}

	/**
	 * 通过静态协议设置LED表情
	 * 
	 * @param expression
	 *            表情的指令数据，静态协议
	 * @return
	 */
	public boolean sendStaticExpression(byte expression) {
		if (isOpen == false)
			return false;

		byte[] pBuff = new byte[4];

		pBuff[0] = (byte) 0xEE;
		pBuff[1] = (byte) 0xAA;
		pBuff[2] = expression;// 默认表情
		pBuff[3] = (byte) 0xBB;

		if (sendMsg(pBuff, 4)) {
			return true;
		}
		return false;
	}

	/**
	 * 通过动态协议设置LED表情
	 * 
	 * @param num
	 *            表情的个数
	 * @param expression
	 *            表情的指令数据的数组
	 * @param timeSpan
	 *            每个表情的时间数组
	 * @return
	 */
	public boolean sendDynamicExpression(int num, byte[] expression, byte[] timeSpan) {
		if (isOpen == false)
			return false;

		byte[] pBuff = new byte[256];

		pBuff[0] = (byte) 0XFF;
		pBuff[1] = (byte) 0xCC;
		pBuff[2] = Integer.valueOf(num).byteValue();
		logger.trace("print expression number:" + pBuff[2]);
		for (int i = 0; i < num; i++) {
			pBuff[3 + i * 2] = expression[i];
			pBuff[4 + i * 2] = timeSpan[i];
		}
		pBuff[num * 2 + 3] = (byte) 0XDD;

		if (sendMsg(pBuff, num * 2 + 4)) {
			return true;
		}
		return false;
	}

	/**
	 * 通过动态协议设置LED表情
	 * 
	 * @param num
	 *            表情的个数
	 * @param expression
	 *            表情的指令
	 * @param repeat
	 *            对应表情时间
	 * @return
	 */
	public boolean setDynamicExpression(int emotion, long duration, int repeat) {
		if (isOpen == false)
			return false;
		if(duration<1000 || repeat ==0)
			return false;//Interval of expression must >= 1s and repeat >0.
		byte[] pBuff = new byte[256];

		pBuff[0] = (byte) 0xEE;
		pBuff[1] = (byte) 0xAA;
		pBuff[3] = (byte) 0xBB;
		
		while (repeat != 0) {
			repeat--;
			pBuff[2] = (byte) emotion;
			sendMsg(pBuff, 4);
			try {
				Thread.sleep(duration);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			pBuff[2] = 0x05;// default expression-smile
			sendMsg(pBuff, 4);
		
			try {
				Thread.sleep(duration);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}

	public void setExpression(int emotion, long duration, int repeat) {
		logger.trace("setExpression 方法");
		switch (emotion) {
		case 1:// 吃惊
			setDynamicExpression(emotion, duration, repeat);
			break;
		case 2:// 大笑
			setDynamicExpression(emotion, duration, repeat);
			break;
		case 3:// 道别
			setDynamicExpression(emotion, duration, repeat);
			break;
		case 4:// 害羞
			setDynamicExpression(emotion, duration, repeat);
			break;
		case 5:// 微笑
			setDynamicExpression(emotion, duration, repeat);
			break;
		case 6:// 汗
			setDynamicExpression(emotion, duration, repeat);
			break;
		case 7:// 疑问
			setDynamicExpression(emotion, duration, repeat);
			break;
		case 8:// 赞
			setDynamicExpression(emotion, duration, repeat);
			break;
		case 9:// 无表情
			setDynamicExpression(emotion, duration, repeat);
			break;
		default:
			break;
		}
	}
}
