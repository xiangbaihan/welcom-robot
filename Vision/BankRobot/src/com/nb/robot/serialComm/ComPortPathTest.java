package com.nb.robot.serialComm;

import com.nb.robot.common.CommonUtils;

public class ComPortPathTest {

	public static void main(String[] args) {
		System.out.println(CommonUtils.getSymbolicLinkTarget("/dev/rtc"));
	}
}
