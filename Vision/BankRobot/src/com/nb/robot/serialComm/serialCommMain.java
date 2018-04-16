package com.nb.robot.serialComm;

import java.io.*;
import java.util.Enumeration;
import java.util.HashSet;

import gnu.io.*;

public class serialCommMain {
    static Enumeration portList;
    static CommPortIdentifier portId;
    static String messageString = "Hello, world!\n";
    static SerialPort serialPort;
    static OutputStream outputStream;

    public static void main(String[] args) {
        portList = CommPortIdentifier.getPortIdentifiers();

	System.out.println(portList.hasMoreElements());
        while (portList.hasMoreElements()) {
            portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
            	System.out.println(portId.getName());
                if (portId.getName().equals("/dev/ttyS99")) {
                    try {
                        serialPort = (SerialPort)portId.open("SimpleWriteApp", 2000);
                    } catch (PortInUseException e) {
                    	e.printStackTrace();
                    }
                    try {
                        outputStream = serialPort.getOutputStream();
                    } catch (IOException e) {
                    	e.printStackTrace();
                    }
                    try {
                        serialPort.setSerialPortParams(9600,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    } catch (UnsupportedCommOperationException e) {}
                    try {
                    	System.out.println("writing message...");
                        outputStream.write(messageString.getBytes());
                    } catch (IOException e) {
                    	e.printStackTrace();
                    }
                }
            }
        }
    }

}
