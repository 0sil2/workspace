package com.tsingoal.com;

public class TRichAlarmInfo {
	private short alarmType;
	private Long relatedTagId;
	private Long alarmTime;
	private String alarmId;
	private String alarmDesc;
	private float currentPosX;
	private float currentPosY;
	private short floorId;
	private String relatedCameraId;
	private String relatedCameraIp;
	private int relatedCameraPort;
	private short relatedCameraChannel;
	private EEfenceMore eefenceMore;

	public EEfenceMore getEefenceMore() {
		return this.eefenceMore;
	}

	public void setEefenceMore(EEfenceMore eefenceMore) {
		this.eefenceMore = eefenceMore;
	}

	public String toString() {
		return "TRichAlarmInfo{alarmType=" + this.alarmType + ", relatedTagId=" +

				Long.toHexString(this.relatedTagId.longValue()) + ", alarmTime=" + this.alarmTime + ", alarmId="
				+ this.alarmId + ", alarmDesc=" + this.alarmDesc + ", relatedCameraID=" + this.relatedCameraId
				+ ", relatedCameraIp=" + this.relatedCameraIp + ", relatedCameraPort=" + this.relatedCameraPort
				+ ", relatedCameraChannel=" + this.relatedCameraChannel + ", eefenceMore=" + this.eefenceMore
				+ ", currentPosX=" + this.currentPosX + ", currentPosY=" + this.currentPosY + ", floorId="
				+ this.floorId + '}';
	}

	public float getCurrentPosX() {
		return this.currentPosX;
	}

	public void setCurrentPosX(float currentPosX) {
		this.currentPosX = currentPosX;
	}

	public float getCurrentPosY() {
		return this.currentPosY;
	}

	public void setCurrentPosY(float currentPosY) {
		this.currentPosY = currentPosY;
	}

	public short getRelatedCameraChannel() {
		return this.relatedCameraChannel;
	}

	public void setRelatedCameraChannel(short relatedCameraChannel) {
		this.relatedCameraChannel = relatedCameraChannel;
	}

	public short getFloorId() {
		return this.floorId;
	}

	public void setFloorId(short floorId) {
		this.floorId = floorId;
	}

	public class EEfenceMore {
		public Long id;
		public String fenceName;

		public String toString() {
			return "EEfenceMore{id=" + this.id + ", fenceName='" + this.fenceName + '\'' + '}';
		}
	}

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

	public String getAlarmId() {
		return this.alarmId;
	}

	public void setAlarmId(String alarm_id) {
		this.alarmId = alarm_id;
	}

	public void setRelatedCameraId(String relatedCameraId) {
		this.relatedCameraId = relatedCameraId;
	}

	public String getRelatedCameraId() {
		return this.relatedCameraId;
	}

	public String getRelatedCameraIp() {
		return this.relatedCameraIp;
	}

	public void setRelatedCameraIp(String relatedCameraIp) {
		this.relatedCameraIp = relatedCameraIp;
	}

	public int getRelatedCameraPort() {
		return this.relatedCameraPort;
	}

	public void setRelatedCameraPort(int relatedCameraPort) {
		this.relatedCameraPort = relatedCameraPort;
	}

	public String getAlarmDesc() {
		return this.alarmDesc;
	}

	public void setAlarmDesc(String alarmDesc) {
		this.alarmDesc = alarmDesc;
	}
}