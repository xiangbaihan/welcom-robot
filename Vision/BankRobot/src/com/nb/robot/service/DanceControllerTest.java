package com.nb.robot.service;

import org.apache.log4j.Logger;

public class DanceControllerTest {
	private static Logger logger = Logger.getLogger(DanceControllerTest.class);
	public static AudioPlayerRunnable aduio1 = null;
	// 测试跳舞
	public static void main(String[] args) throws InterruptedException {

		
		 MotionAndLedControlModule motionContrlModule = MotionAndLedControlModule.getInstance();
		 motionContrlModule.start();	
		 DanceControlModule danceControlModule = DanceControlModule.getInstance();
		 danceControlModule.start();//
		// 线程测试
			Thread stopDance = new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						Thread.sleep(8000);
					} catch (InterruptedException e) {// TODO Auto-generated catch
														// block
						e.printStackTrace();
					}
					logger.info("测试  突然停止");
					// 停止
					danceControlModule.controlStopOrResumeDance(false);
					try {
						Thread.sleep(8000);
					} catch (InterruptedException e) {// TODO Auto-generated catch
														// block
						e.printStackTrace();
					}
					logger.info("测试  停止后继续跳舞\"");
					danceControlModule.controlStopOrResumeDance(true);
				}
			});
		//用户播放
		danceControlModule.controlDance(2, 1,0, false);
		//用户播放---跳舞10s
		//danceControlModule.controlDance(2, 1,10000, false);
		// 静音
		//danceControlModule.controlDance(2, 1,0, true);
		//线程测试
		stopDance.start();
		
		
	}

}