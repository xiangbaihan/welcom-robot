package com.nb.robot.serialComm;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.nb.robot.common.Constants;

import gnu.io.SerialPort;

public class ControlMotion extends BaseSerialControl {
	private static Logger logger = Logger.getLogger(ControlMotion.class);
	private static final int baud = 19200;
	private static final byte frameHeader = (byte) 0xEE;// 帧头
	private static final byte frameDelimiter = (byte) 0xBB;// 帧尾
	private static final int frameLength = 12;// 帧长度
	private static final int SLEEP_TIME_INTERVAL = 5;
	private static final byte Chasis_Direction_FORWORDANDRIGHT = 0x01;
	private static final byte Chasis_Direction_BACKANDLEFT = 0x02;
	//角速度和线速度映射
	private static int VSPEED_PER=500;
	private static int  RSPEED_PER= 300;//(1500/3.1415926)
	private static Timer timer = new Timer();
	private static TimerTask task;
	private readCallBack callBack;

	public interface readCallBack {
		void readSuccess(ChasisMotionState state);
	}

	public void setReadCallBack(readCallBack call) {
		this.callBack = call;
	}

	public ControlMotion() {
	}

	public ControlMotion(String portName) {
		super.openSerialPort(portName, baud, SerialPort.PARITY_NONE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1);
	}

	public ControlMotion(String portName, int baud, int parity, int databits, int stopbits) {
		super.openSerialPort(portName, baud, parity, databits, stopbits);
	}

	/**
	 * 获取底盘状态信息
	 * 
	 * @param revByte
	 *            接收的协议数据
	 * @param outState
	 *            底盘状态
	 * @return
	 */
	public ChasisMotionState getMotionStatus(byte[] revByte) {
		ChasisMotionState outState = new ChasisMotionState();

		if (revByte[0] == (byte) 0xee && revByte[1] == (byte) 0xaa && revByte[11] == (byte) 0xbb) { // revByte[10]==(byte)0x00
			int x = (revByte[2] & 0xff) << 8 | (revByte[3] & 0xff);
			int y = (revByte[4] & 0xff) << 8 | (revByte[5] & 0xff);
			int th = (revByte[6] & 0xff) << 8 | (revByte[7] & 0xff);
			outState = new ChasisMotionState(x, y, th);
			// outState.v=revByte[8];
			// outState.w=revByte[9];
			// System.out.println(String.format("获取到底盘控制器的信息，X = %d ,"
			// + "y = %d ,TH = %d ",outState.x,outState.y,outState.th));
		}
		return outState;
	}

	/**
	 * 设置底盘的运动,F1=F2=0x00
	 * 
	 * @param lineSpeed
	 *            线速度值
	 * @param angularSpeed
	 *            角速度值
	 * @param duration
	 *            运动的时长
	 * @param ledState
	 *            LED灯状态位.
	 *            0x01:亮暗循环变化
	 *            0x02:慢速从暗到亮（5秒）
	 *            0x03:快速从暗到亮（2.5秒）
	 *            0x04:慢速从亮到暗（5秒）
	 *            0x05:快速从亮到暗（2.5秒
	 *            0x06:常亮
	 *            0x07:中度亮
	 *            0x08:低度亮
	 *            0x09:常灭
	 */
	public void setChasisMotion(int lineSpeed, int angularSpeed, long duration, int ledState) {
		if (isOpen == false) {
			logger.error("Serial communication port is not open.");
			return;
		}
		lineSpeed = lineSpeed*VSPEED_PER;
		angularSpeed = angularSpeed*RSPEED_PER;
		byte[] pBUff = new byte[12];
		pBUff[0] = (byte) 0xEE;
		pBUff[1] = (byte) 0xAA;
		if (lineSpeed < 0) {
			pBUff[2] = Chasis_Direction_BACKANDLEFT;
			lineSpeed = Math.abs(lineSpeed);// 转换为正数
		} else {
			pBUff[2] = Chasis_Direction_FORWORDANDRIGHT;
		}
		pBUff[3] = (byte) (lineSpeed / 256);
		pBUff[4] = (byte) (lineSpeed % 256);
		if (angularSpeed < 0) {
			pBUff[5] = Chasis_Direction_BACKANDLEFT;
			angularSpeed = Math.abs(angularSpeed);
		} else {
			pBUff[5] = Chasis_Direction_FORWORDANDRIGHT;
		}
		pBUff[6] = (byte) (angularSpeed / 256);
		pBUff[7] = (byte) (angularSpeed % 256);
		pBUff[8] = 0x00;
		pBUff[9] = 0x00;
		// 灯控位
		pBUff[10] = (byte) ledState;
		pBUff[11] = (byte) 0xBB;

		if (task != null)
			task.cancel();
		timer.purge();
		task = new TimerTask() {
			long runTimes = duration / Constants.CHASIS_LED_SERIAL_COM_TIME_INTERVAL;

			@Override
			public void run() {
				logger.trace("向底盘发送指令数据pBuff = ");
				for (byte b : pBUff) {
					logger.trace(Integer.toHexString(b & 0xff));
				}
				runTimes--;
				if (runTimes <= 0) {// 执行时间达到duration，结束task
					this.cancel();
				}
				sendMsg(pBUff, 12);
			}
		};
		timer.scheduleAtFixedRate(task, 0, Constants.CHASIS_LED_SERIAL_COM_TIME_INTERVAL);
	}

	/**
	 * 通过设置功能位F1,F2,初始化底盘的运动,X,Y,TH置为0
	 * 
	 * @return 发送是否成功
	 */
	public boolean resetChassisCoordinate() {
		if (isOpen == false) {
			logger.error("Serial communication port is not open.");
			return false;
		}

		byte[] pBUff = new byte[12];
		pBUff[0] = (byte) 0xEE;
		pBUff[1] = (byte) 0xAA;
		pBUff[2] = (byte) 0x01;
		pBUff[3] = (byte) 0x00;
		pBUff[4] = (byte) 0x00;
		pBUff[5] = (byte) 0x01;
		pBUff[6] = (byte) 0x00;
		pBUff[7] = (byte) 0x00;
		pBUff[8] = (byte) 0xFE;
		pBUff[9] = (byte) 0XDC;
		/* 这一位设置为灯控 */
		pBUff[10] = 0x01;
		pBUff[11] = (byte) 0xBB;

		if (sendMsg(pBUff, 12)) {
			return true;
		}

		return false;
	}

	@Override
	protected void readComm() {
		byte[] readBuffer = new byte[1024];

		try {
			inputStream = serialPort.getInputStream();
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
			ChasisMotionState state = new ChasisMotionState();
			if (frames != null) {
				state = getMotionStatus(frames);
				if (callBack != null)
					callBack.readSuccess(state);
			} else {
				logger.error("获取底盘状态帧数据失败");
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
