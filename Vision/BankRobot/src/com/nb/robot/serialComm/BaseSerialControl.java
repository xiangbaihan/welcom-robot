package com.nb.robot.serialComm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import org.apache.log4j.Logger;

import com.nb.robot.server.ServerMain;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

public class BaseSerialControl implements SerialPortEventListener {
	private static Logger logger = Logger.getLogger(BaseSerialControl.class);

	protected CommPortIdentifier portId;
	protected Enumeration<CommPortIdentifier> portList;
	protected InputStream inputStream;
	protected OutputStream outputStream;
	protected SerialPort serialPort;
	protected boolean isOpen = false;

	/**
	 * 初始化端口
	 * 
	 * @param portName
	 *            端口名称
	 * @param baud
	 *            端口波特率
	 * @param parity
	 *            是否进行奇偶校验
	 * @param databits
	 *            数据位个数
	 * @param stopbits
	 *            停止位
	 * @param event
	 *            收发数据的事件产生方式
	 */
	@SuppressWarnings("unchecked")
	public void openSerialPort(String portName, int baud, int parity, int databits, int stopbits) {
		portList = CommPortIdentifier.getPortIdentifiers();

		while (portList.hasMoreElements()) {
			portId = portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals(portName)) {
					logger.debug("找到端口：" + portName);
					if (portId.isCurrentlyOwned()) {
						logger.debug("端口" + portName + "已经被" + portId.getCurrentOwner() + "占用");
						isOpen = false;
						return;
					}
					try {
						serialPort = (SerialPort) portId.open("BankRobot", 1000);
						serialPort.addEventListener(this);
						serialPort.notifyOnDataAvailable(true);
						serialPort.setSerialPortParams(baud, databits, stopbits, parity);
						outputStream = serialPort.getOutputStream();
						isOpen = true;
					} catch (PortInUseException e) {
						e.printStackTrace();
					} catch (TooManyListenersException e) {
						e.printStackTrace();
					} catch (UnsupportedCommOperationException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 从串口读取数据
	 * 
	 * @return 数据字符串
	 */
	protected void readComm() {
	}

	/**
	 * 关闭串口，释放资源
	 * 
	 * @return
	 */
	protected boolean closePort() {
		if (isOpen) {
			try {
				inputStream.close();
				outputStream.close();
				inputStream = null;
				outputStream = null;
				serialPort.notifyOnDataAvailable(false);
				serialPort.removeEventListener();
				serialPort.close();
				isOpen = false;
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 向串口发送数据
	 * 
	 * @param data
	 *            数据字符串
	 */
	protected boolean sendMsg(byte[] data, int length) {
		if (!isOpen) {
			return false;
		}
		try {
			outputStream.write(data, 0, length);
			// outputStream.flush();
			inputStream = serialPort.getInputStream();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 监听串口数据事件
	 */
	@Override
	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		case SerialPortEvent.DATA_AVAILABLE:
			readComm();
			break;
		default:
			break;
		}
	}
}
