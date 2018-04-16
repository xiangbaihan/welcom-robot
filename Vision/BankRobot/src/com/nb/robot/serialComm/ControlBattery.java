package com.nb.robot.serialComm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import gnu.io.SerialPort;

public class ControlBattery extends BaseSerialControl {
	private static Logger logger = Logger.getLogger(ControlBattery.class);
	private final int baud = 19200;

	private static final byte frameHeader = (byte) 0x7E;// 帧头
	private static final byte frameDelimiter = (byte) 0x0D;// 帧尾
	private static final int frameLength = 158;// 帧长度
	private static final int SLEEP_TIME_INTERVAL = 5;
	private static final int SOC_START = 15;// 获取电池组容量帧的起始位
	private static final int SOC_END = 18;// 获取电池组容量帧的结束位

	private batteryReadCallBack callBack;

	public interface batteryReadCallBack {
		void readSuccess(BatteryState state);
	}

	public void setReadCallBack(batteryReadCallBack batteryReadCallBack) {
		this.callBack = batteryReadCallBack;
	}

	public ControlBattery() {
	}

	public ControlBattery(String portName) {
		super.openSerialPort(portName, baud, SerialPort.PARITY_NONE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1);
	}

	public ControlBattery(String portName, int baud, int parity, int databits, int stopbits) {
		super.openSerialPort(portName, baud, parity, databits, stopbits);
	}

	public boolean sendMsgToBattery() {
		if (isOpen == false) {
			logger.error("Serial communication port is not open.");
			return false;
		}

		// 7E 32 32 30 33 34 41 34 32 45 30 30 32 30 31 46 44 32 36 0D
		// 起始位和结束位是十六进制，剩余位用ASCII表示，比如：通信协议版本号VER：0x22H，2的ASCII码是50=(0x32)
		byte[] pBuff = new byte[20];
		pBuff[0] = (byte) 0x7E;// 起始位SOI
		// 通信协议版本号VER
		pBuff[1] = (byte) 0x32;
		pBuff[2] = (byte) 0x32;
		// 设备地址ADR
		pBuff[3] = (byte) 0x30;
		pBuff[4] = (byte) 0x33;
		// 设备标识码CID1
		pBuff[5] = (byte) 0x34;
		pBuff[6] = (byte) 0x41;
		pBuff[7] = (byte) 0x34;// 命令类型描述CID2
		pBuff[8] = (byte) 0x32;
		// info字节长度 LENGTH
		pBuff[9] = (byte) 0x45;
		pBuff[10] = (byte) 0x30;
		pBuff[11] = (byte) 0x30;
		pBuff[12] = (byte) 0x32;
		pBuff[13] = (byte) 0x30;
		pBuff[14] = (byte) 0x31;
		pBuff[15] = (byte) 0x46;
		pBuff[16] = (byte) 0x44;
		// 校验位
		pBuff[17] = (byte) 0x32;
		pBuff[18] = (byte) 0x36;
		pBuff[19] = (byte) 0x0D;// 结束位
		if (sendMsg(pBuff, 20)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取电池状态信息
	 * 
	 * @param revByte
	 *            接收的协议数据
	 * @param outState
	 *            电池状态
	 * @return
	 */
	public BatteryState getBatteryStatus(byte[] revByte) {
		// 获取电量的状态，要先给电池发送命令，才能获取电池电量吗？
		BatteryState outState = new BatteryState();
		// ～ 22 01 4A 00 C0 8C 00 0034 0A5B 08 0CF30
		// 0034(SOC)表示电池组容量，0A5B是电压
		int soc = 0;
		if (revByte[0] == (byte) 0x7E && revByte[157] == (byte) 0x0D) {
			for (int i = SOC_START; i <= SOC_END; i++) {
				if (revByte[i] > 47 && revByte[i] < 58) {
					revByte[i] = (byte) (revByte[i] - 48);
				} else if (revByte[i] > 64 && revByte[i] < 71) {
					revByte[i] = (byte) (revByte[i] - 55);
				}
				 soc +=revByte[i]*(int)Math.pow(16, SOC_END-i);
			}
			outState = new BatteryState(soc);
		}
		return outState;
	}

	@Override
	protected void readComm() {
		byte[] readBuffer = new byte[1024];

		try {
			inputStream = serialPort.getInputStream();// 从串口来的输入流

			int len = inputStream.available();// 缓冲区可读字节个数
			while (len < frameLength) {// 可读字节数小于一帧的长度，则休眠一段时间再查询
				try {
					Thread.sleep(SLEEP_TIME_INTERVAL);
					len = inputStream.available();// 缓冲区可读字节个数
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			inputStream.read(readBuffer);

			byte[] frames = getStatusFrame(readBuffer, len);
			BatteryState state = new BatteryState();
			if (frames != null) {
				state = getBatteryStatus(frames);
				if (callBack != null)
					callBack.readSuccess(state);
			} else {
				logger.error("获取电池状态帧数据失败");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private byte[] getStatusFrame(byte[] readBuffer, int length) {
		byte[] statusFrame = new byte[1024];
		// 找帧尾
		int posDelimiter = 0;
		for (int i = length - 1; i > 0; i--) {
			if (readBuffer[i] == frameDelimiter) {
				posDelimiter = i;
				break;
			}
		}

		int tempLen = 0, nowLen = 0;
		for (int i = posDelimiter + 1; i < length; i++) {
			statusFrame[tempLen++] = readBuffer[i];
		}
		nowLen = frameLength - tempLen - 1;// 除去帧尾的字节外还需要几字节填满一帧数据

		for (int i = posDelimiter - nowLen; i < posDelimiter + 1; i++) {
			statusFrame[tempLen++] = readBuffer[i];
		}
		// 判断帧头是否正确
		if (statusFrame[0] == frameHeader) {
			return statusFrame;
		}
		return null;
	}
}
