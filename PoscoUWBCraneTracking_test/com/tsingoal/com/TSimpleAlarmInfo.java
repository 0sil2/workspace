package com.tsingoal.com;

public class TSimpleAlarmInfo {
	private short alarmType;
	private Long relatedTagId;
	private Long alarmTime;
	private String alarmDesc;

	public short getAlarmType() {
		return this.alarmType;
	}

	public void setAlarmType(short alarmType) {
		this.alarmType = alarmType;
	}

	public Long getRelatedTagId() {
		return this.relatedTagId;
	}

	public void setRelatedTagId(Long relatedTagId) {
		this.relatedTagId = relatedTagId;
	}

	public Long getAlarmTime() {
		return this.alarmTime;
	}

	public void setAlarmTime(Long alarmTime) {
		this.alarmTime = alarmTime;
	}

	public String getAlarmDesc() {
		return this.alarmDesc;
	}

	public void setAlarmDesc(String alarmDesc) {
		this.alarmDesc = alarmDesc;
	}

	public String toString() {
		return "TSimpleAlarmInfo{alarmType=" + this.alarmType + ", relatedTagId=" +

				Long.toHexString(this.relatedTagId.longValue()) + ", alarmTime=" + this.alarmTime + ", alarmDesc='"
				+ this.alarmDesc + '\'' + '}';
	}
}