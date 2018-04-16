package com.nb.robot.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DanceRequest {
	private int type;// 舞蹈类型
	private int repeat;// 重复次数
	private long duration;// 表演时间
	private boolean muteFlag;// 静音标志

	public DanceRequest() {
	}

	public DanceRequest(int type, int repeat, long duration, boolean muteFlag) {
		this.type = type;
		this.repeat = repeat;
		this.duration = duration;
		this.muteFlag = muteFlag;
	}

	@XmlElement(name = "type")
	public int getType() {
		return type;
	}

	@XmlElement(name = "repeat")
	public int getRepeat() {
		return repeat;
	}

	@XmlElement(name = "duration")
	public long getDuration() {
		return duration;
	}

	@XmlElement(name = "muteFlag")
	public boolean getMuteFlag() {
		return muteFlag;
	}
}
