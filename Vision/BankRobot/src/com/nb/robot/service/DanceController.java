package com.nb.robot.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.nb.robot.common.Constants;
import com.nb.robot.serialComm.ChasisMotionState;

public class DanceController {

	private static Logger logger = Logger.getLogger(DanceController.class);

	// http://192.168.31.24:8080/motion

	public AudioPlayerRunnable aduioPlayerRunnable = null;

	private Thread motionThread;// 执行底盘的指令执行-线程
	private Thread armThread;// 执行手臂的指令执行-线程
	private Thread expressionThread;// 执行表情的指令执行-线程
	private Thread audioThread;// 音乐播放线程
	private boolean runningFlag = true;// 用户停止 舞蹈
	private long duration = 0;// 表演时长|0表示一直跳完
	private long pastDanceDuration = 0;// 之前已经跳的时间（不包括当前此次）
	private long currentDanceStartTime = 0; // 当前舞的开始时间
	private long audioLastPlayTimePosition = 0; // 上次播放歌曲的时间位置
	private boolean muteFlag = false;// 静音标志
	private List<Dance> dancePatternList;// 舞蹈集合
	private List<String> songList;// 歌曲集合
	private List<MotionControlRequest> footList;// 所有地盘指令，包括重复的次数
	private List<ArmControlRequest> armList;// 所有手臂指令，包括重复的次数
	private List<ExpressionControlRequest> expressionList;// 所有手臂指令
	private Dance dance;// 用户选择的舞蹈
	private String song;// 用户选择的音乐
	private int index_arm = 0;// 存放手臂执行到第几条指令
	private int index_motion = 0;// 存放底盘执行到第几条指令
	private int index_expression = 0;// 存放表情执行到第几条指令
	private ChasisMotionState startMotionState;// 跳舞开始时的坐标

	private MotionAndLedControlModule motionContrlModule = MotionAndLedControlModule.getInstance();
	private ExpressionControlModule expressionContrlModule = ExpressionControlModule.getInstance();

	// 构造方法（无参）
	public DanceController() {
		// 初始化动作
		logger.debug("DanceControlle init");
		defineDanceList();
		defineSongList();
	}

	/**
	 * 
	 * @param type
	 *            舞蹈类型
	 * @param repeat
	 *            舞蹈动作重复次数
	 * @param duration
	 *            表演时长 0表示跳完
	 * @param musicFlag
	 *            静音标志 false不播放 舞蹈音乐
	 */
	public void controlDance(int type, int repeat, long duration, boolean muteFlag) {
		logger.debug("控制舞蹈参数：type " + type + "duration " + duration + " muteFlag " + muteFlag);
		this.duration = duration;
		this.muteFlag = muteFlag;
		index_arm = 0;
		index_motion = 0;
		index_expression = 0;
		startMotionState = motionContrlModule.getChasisMotionState();// 初始位置
		// type should be 1 ~ 3.
		assert (type <= dancePatternList.size());
		dance = dancePatternList.get(type - 1);
		song = songList.get(type - 1);
		// 初始化
		footList = new ArrayList<MotionControlRequest>();
		armList = new ArrayList<ArmControlRequest>();
		expressionList = new ArrayList<ExpressionControlRequest>();
		for (int i = 0; i < repeat; ++i) {
			footList.addAll(dance.getMotionActionList());
			armList.addAll(dance.getArmActionList());
			expressionList.addAll(dance.getExpressionnActionList());
		}
		pastDanceDuration = 0;
		audioLastPlayTimePosition = 0;
		// Start dancing and singing. Both should not be blocking.
		dance();
		controlSong();
	}

	/**
	 * 用户暂停/开始 跳舞 interrupt();执行后，线程还会继续发送手臂底盘指令 stop();可以停止
	 * 
	 * @param flag
	 *            标志（true为开始跳舞）
	 */
	public void stopOrResume(boolean flag) {
		runningFlag = flag;
		if (!flag) {// 用户暂停
			stopAllThreads();
			motionContrlModule.controlChasis(0, 0, 10);// 底盘速度为0
			motionContrlModule.controlArm(3, 180, 1);// 手臂放下

			// Record elapsed time for this dance.
			if (currentDanceStartTime > 0) {
				pastDanceDuration += System.currentTimeMillis() - currentDanceStartTime;
				currentDanceStartTime = 0;
			}
		} else {// 用户重新开始
			logger.trace("舞蹈暂停开始：index_arm=" + index_arm + ",index_motion=" + index_motion);
			index_arm = 0;
			index_motion = 0;
			dance();// 继续跳舞
			controlSong();
		}
	}

	// 设置静音
	public void setMuteFlag(boolean muteFlag) {
		// No change.
		if (this.muteFlag == muteFlag) {
			return;
		}
		this.muteFlag = muteFlag;
		controlSong();
	}

	// 统一设计舞蹈动作
	private void defineDanceList() {
		// 底盘运动 lineSpeed线速度值 angularSpeed角速度值 motionDuration运动的时长
		int lispeed = 1;
		int vspeed = 1;
		int motionDuration = 5000;// （ms）
		// 格式：前进（x,0），后退（-x,0），左转（0,-y），右转（0,y）
		MotionControlRequest foot1 = new MotionControlRequest(lispeed * 2, 0, motionDuration);// 前进5s
		MotionControlRequest foot2 = new MotionControlRequest(-lispeed * 2, 0, motionDuration);// 后退5s
		MotionControlRequest foot3 = new MotionControlRequest(0, vspeed, motionDuration);// 向左原地转圈5s
		MotionControlRequest foot4 = new MotionControlRequest(0, -vspeed, motionDuration);// 向右原地转圈5s
		MotionControlRequest foot5 = new MotionControlRequest(lispeed * 2, 0, motionDuration);// 前进10s
		MotionControlRequest foot6 = new MotionControlRequest(-lispeed * 2, 0, motionDuration);// 后退10s
		// 手臂运动 armPart 1,2,3（1-右臂，2-左臂，3-双臂） armPosition摆动后角度 armSpeed运动速度（1-10）
		int arm_speed = 1;
		ArmControlRequest arm140 = new ArmControlRequest(3, 140, arm_speed);
		ArmControlRequest arm200 = new ArmControlRequest(3, 200, arm_speed);
		ArmControlRequest arm270 = new ArmControlRequest(3, 270, arm_speed);
		ArmControlRequest arm180 = new ArmControlRequest(3, 180, arm_speed);
		// 表情
		ExpressionControlRequest expression1 = new ExpressionControlRequest(1, 5000, 1);
		ExpressionControlRequest expression2 = new ExpressionControlRequest(2, 5000, 1);
		ExpressionControlRequest expression3 = new ExpressionControlRequest(4, 5000, 1);
		ExpressionControlRequest expression4 = new ExpressionControlRequest(8, 5000, 1);
		ExpressionControlRequest expression5 = new ExpressionControlRequest(4, 5000 * 2, 1);
		ExpressionControlRequest expression6 = new ExpressionControlRequest(8, 5000 * 2, 1);

		dancePatternList = new ArrayList<Dance>();
		// 组合动作 手臂先左后右180 轮子前进 左右转圈左转弯
		List<ArmControlRequest> armList1 = new ArrayList<ArmControlRequest>();
		List<MotionControlRequest> footList1 = new ArrayList<MotionControlRequest>();
		List<ExpressionControlRequest> expressionList1 = new ArrayList<ExpressionControlRequest>();
		// 手臂
		armList1.add(arm270);
		armList1.add(arm180);
		armList1.add(arm140);
		armList1.add(arm180);
		// 运动：前进5s->后退5s->右转5s->左转5s
		footList1.add(foot1);
		footList1.add(foot2);
		footList1.add(foot3);
		footList1.add(foot4);
		// 表情
		expressionList1.add(expression1);
		expressionList1.add(expression2);
		expressionList1.add(expression3);
		expressionList1.add(expression4);
		Dance dance1 = new Dance(20000, armList1, footList1, expressionList1);
		dancePatternList.add(dance1);

		// 2 手臂先左后右再一起90 轮子 左转圈 前进 左转弯 右转圈 右转弯 左转圈
		List<ArmControlRequest> armList2 = new ArrayList<ArmControlRequest>();
		List<MotionControlRequest> footList2 = new ArrayList<MotionControlRequest>();
		List<ExpressionControlRequest> expressionList2 = new ArrayList<ExpressionControlRequest>();
		armList2.add(arm270);
		armList2.add(arm180);
		armList2.add(arm270);
		armList2.add(arm180);
		footList2.add(foot2);
		footList2.add(foot1);
		footList2.add(foot3);
		footList2.add(foot4);
		footList2.add(foot5);
		footList2.add(foot2);
		footList2.add(foot2);
		expressionList2.add(expression4);
		expressionList2.add(expression3);
		expressionList2.add(expression2);
		expressionList2.add(expression1);
		expressionList2.add(expression2);
		expressionList2.add(expression3);
		expressionList2.add(expression4);
		Dance dance2 = new Dance(40000, armList2, footList2, expressionList2);
		dancePatternList.add(dance2);

		// 3 手臂先左后右90 再左右90 轮子 左 右转弯 前进 右转弯 左右转圈 右转弯 前进 左转弯
		List<ArmControlRequest> armList3 = new ArrayList<ArmControlRequest>();
		List<MotionControlRequest> footList3 = new ArrayList<MotionControlRequest>();
		List<ExpressionControlRequest> expressionList3 = new ArrayList<ExpressionControlRequest>();
		armList3.add(arm180);
		armList3.add(arm270);
		armList3.add(arm180);
		footList3.add(foot4);
		footList3.add(foot5);
		footList3.add(foot1);
		footList3.add(foot5);
		footList3.add(foot6);
		footList3.add(foot5);
		footList3.add(foot1);
		footList3.add(foot4);
		expressionList3.add(expression2);
		expressionList3.add(expression4);
		expressionList3.add(expression6);
		expressionList3.add(expression5);
		expressionList3.add(expression3);
		expressionList3.add(expression1);
		Dance dance3 = new Dance(60000, armList3, footList3, expressionList3);
		dancePatternList.add(dance3);
	}

	// Defines song list.
	private void defineSongList() {
		songList = new ArrayList<String>();
		songList.add(Constants.DANCE_SONG_1);
		songList.add(Constants.DANCE_SONG_2);
		songList.add(Constants.DANCE_SONG_3);
		assert (dancePatternList.size() == songList.size());
	}

	// Returns whether the user-defined duration has been exhausted.
	private boolean isOverDuration() {
		if (duration == 0) {
			return false;
		}
		assert (currentDanceStartTime > 0);
		if (pastDanceDuration + System.currentTimeMillis() - currentDanceStartTime > duration) {
			logger.trace("跳舞时间已经超过预定时间: " + duration);
			return true;
		}
		return false;
	}

	// Kills threads.
	private void stopAllThreads() {
		if (motionThread != null) {
			motionThread.interrupt();
			motionThread = null;
		}
		if (armThread != null) {
			armThread.interrupt();
			armThread = null;
		}
		if (expressionThread != null) {
			expressionThread.interrupt();
			expressionThread = null;
		}
		if (aduioPlayerRunnable != null) {
			audioLastPlayTimePosition = aduioPlayerRunnable.stopWithCurrentPosition();
		}
		if (audioThread != null) {
			audioThread.interrupt();
			audioThread = null;
		}
		logger.info("Dance stopped");
	}

	/**
	 * 执行跳舞 发送指令
	 * 
	 * @param Index_arm_new
	 *            暂停后开始，发送的第一条手臂指令下标
	 * @param Index_motion_new
	 *            暂停后开始，发送的第一条底盘指令下标
	 */
	private void dance() {
		// stopAllThreads();
		logger.info("Dance start again");
		currentDanceStartTime = System.currentTimeMillis();
		armThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (index_arm < armList.size()) {
					if (Thread.currentThread().isInterrupted() || !runningFlag) {
						// Exit thread.
						return;
					}
					if (isOverDuration()) {
						break;
					}
					logger.trace("正常发送手臂指令第：" + index_arm + " 条");
					ArmControlRequest request = armList.get(index_arm);
					motionContrlModule.controlArm(request.getArmPart(), request.getArmPosition(),
							request.getArmSpeed());
					try {// 每5秒发送一次
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					index_arm++;
				}
			}
		});
		motionThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (index_motion < footList.size()) {
					if (Thread.currentThread().isInterrupted() || !runningFlag) {
						// Exit thread.
						return;
					}
					if (isOverDuration()) {
						break;
					}
					logger.trace("正常发送底盘指令第：" + index_motion + " 条");
					MotionControlRequest request = footList.get(index_motion);
					motionContrlModule.controlChasis(request.getMotionLineSpeed(), request.getMotionAngularSpeed(),
							request.getMotionDuration());
					try {
						Thread.sleep(request.getMotionDuration());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					index_motion++;
				}
				// 回到初始位置. This should be called from one thread only!
				robotBack();
			}
		});
		expressionThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (index_expression < expressionList.size()) {
					if (Thread.currentThread().isInterrupted() || !runningFlag) {
						// Exit thread.
						return;
					}
					if (isOverDuration()) {
						break;
					}
					logger.trace("正常发送表情指令第：" + index_expression + " 条");
					ExpressionControlRequest request = expressionList.get(index_expression);
					expressionContrlModule.controlDynamicExpresssion(request.getEmotion(), request.getEmotionDuration(),
							request.getEmotionRepeat());
					try {
						Thread.sleep(request.getEmotionDuration());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					index_expression++;
				}
			}
		});
		armThread.start();
		motionThread.start();
		expressionThread.start();
	}

	/**
	 * 播放或暂定音乐
	 */
	private void controlSong() {
		if (muteFlag) {
			if (aduioPlayerRunnable != null) {
				audioLastPlayTimePosition = aduioPlayerRunnable.stopWithCurrentPosition();
			}
			if (audioThread != null) {
				aduioPlayerRunnable.stop();
				audioThread.interrupt();
				audioThread = null;
			}
			return;
		}

		// Set repeat to MAX_VALUE because we want to play until dance is done.
		aduioPlayerRunnable = new AudioPlayerRunnable(song, audioLastPlayTimePosition, Integer.MAX_VALUE);
		audioThread = new Thread(aduioPlayerRunnable);
		audioThread.start();
	}

	/**
	 * 机器人回到初始位置
	 */
	private void robotBack() {
		logger.debug("执行----回到原位置方法");
		if (aduioPlayerRunnable != null) {
			aduioPlayerRunnable.stop();
		}
		if (audioThread != null) {
			audioThread.interrupt();
		}
		audioThread = null;
		// 两个手臂放下
		motionContrlModule.controlArm(3, 180, 1);//
		// 获取坐标 revByte 接收的协议数据
		// 使用改写的get方法获取
		ChasisMotionState motionState = motionContrlModule.getChasisMotionState();
		int x = (Math.abs(motionState.getX() - startMotionState.getX()));
		int y = (Math.abs(motionState.getY() - startMotionState.getY()));
		int th = (Math.abs(motionState.getTh() - startMotionState.getTh()));
		// logger.trace("底盘变化坐标x " + x + " y " + y + " th " + th);
		// 转动角度
		int rspeed = 1;// 角速度--线速度为0，角速变换
		motionContrlModule.controlChasis(0, rspeed, th / rspeed);
		double distance = Math.sqrt(x * x + y * y);// 计算距离
		int dis = (int) distance;
		int vspeed = 1;// 线速度--角速度为0，线速度变换
		motionContrlModule.controlChasis(vspeed, 0, dis / vspeed);
		// 转回角度
		motionContrlModule.controlChasis(0, rspeed, (360 - th) / rspeed);
		// 设置默认表情为微笑
		expressionContrlModule.controlStaticExpresssion(5);
	}

}