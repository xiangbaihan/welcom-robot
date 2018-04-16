package com.nb.robot.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ExpressionControlRequest {
	// 1-吃惊，2-大笑，3-道别，4-害羞，5-微笑，6-汗，7-疑问，8-赞，9-无表情
	private int emotion;// 表情类型
	private long emotionDuration;// 表情持续时间
	private int emotionRepeat;// 表情重复次数

	public ExpressionControlRequest() {
	}

	public ExpressionControlRequest(int emotion, long emotionDuration, int emotionRepeat) {
		super();
		this.emotion = emotion;
		this.emotionDuration = emotionDuration;
		this.emotionRepeat = emotionRepeat;
	}

	@XmlElement(name = "emotion")
	public int getEmotion() {
		return emotion;
	}

	@XmlElement(name = "emotionDuration")
	public long getEmotionDuration() {
		return emotionDuration;
	}

	@XmlElement(name = "emotionRepeat")
	public int getEmotionRepeat() {
		return emotionRepeat;
	}

	public void setEmotion(int emotion) {
		this.emotion = emotion;
	}

	public void setEmotionDuration(long emotionDuration) {
		this.emotionDuration = emotionDuration;
	}

	public void setEmotionRepeat(int emotionRepeat) {
		this.emotionRepeat = emotionRepeat;
	}
}
