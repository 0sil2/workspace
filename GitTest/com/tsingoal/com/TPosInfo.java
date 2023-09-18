package com.tsingoal.com;

public class TPosInfo {
	private Long tagId;
	private float posX;
	private float posY;
	private float posZ;
	private short capcity;
	private Boolean isSleep;
	private Boolean isCharged;
	private int timestamp;
	private short posIndicator;
	private short floorId;
	private short floorNo;
	private boolean isGeoGraphicCoord = false;
	private boolean isGlobalGraphicCoord = false;

	public Long getTagId() {
		return this.tagId;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}

	public float getPosX() {
		return this.posX;
	}

	public void setPosX(float posX) {
		this.posX = posX;
	}

	public float getPosY() {
		return this.posY;
	}

	public void setPosY(float posY) {
		this.posY = posY;
	}

	public float getPosZ() {
		return this.posZ;
	}

	public void setPosZ(float posZ) {
		this.posZ = posZ;
	}

	public short getCapcity() {
		return this.capcity;
	}

	public void setCapcity(short capcity) {
		this.capcity = capcity;
	}

	public Boolean getSleep() {
		return this.isSleep;
	}

	public void setSleep(Boolean sleep) {
		this.isSleep = sleep;
	}

	public Boolean getCharged() {
		return this.isCharged;
	}

	public void setCharged(Boolean charged) {
		this.isCharged = charged;
	}

	public int getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public short getFloorId() {
		return this.floorId;
	}

	public void setFloorId(short floorId) {
		this.floorId = floorId;
	}

	public String toString() {
		return "TPosInfo{tagId=" + Long.toHexString(this.tagId.longValue()) + ", posX=" + this.posX + ", posY="
				+ this.posY + ", posZ=" + this.posZ + ", capcity=" + this.capcity + ", isSleep=" + this.isSleep
				+ ", isCharged=" + this.isCharged + ", timestamp=" + this.timestamp + ", posIndicator="
				+ this.posIndicator + ", floorId=" + this.floorId + ", floorNo=" + this.floorNo + ", isGeoGraphicCoord="
				+ this.isGeoGraphicCoord + ", isGlobalGraphicCoord=" + this.isGlobalGraphicCoord + '}';
	}

	public short getPosIndicator() {
		return this.posIndicator;
	}

	public void setPosIndicator(short posIndicator) {
		this.posIndicator = posIndicator;
	}

	public short getFloorNo() {
		return this.floorNo;
	}

	public void setFloorNo(short floorNo) {
		this.floorNo = floorNo;
	}

	public boolean isGeographicCoord() {
		return this.isGeoGraphicCoord;
	}

	public void setGeoGraphicCoord(boolean isGeoGraphicCoord) {
		this.isGeoGraphicCoord = isGeoGraphicCoord;
	}

	public boolean isGlobalGraphicCoord() {
		return this.isGlobalGraphicCoord;
	}

	public void setGlobalGraphicCoord(boolean isGlobalGraphicCoord) {
		this.isGlobalGraphicCoord = isGlobalGraphicCoord;
	}
}