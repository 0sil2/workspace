package com.tsingoal.com;

import java.util.List;

public class TPosInfoBeacon {
	private Long tagId;
	private float posX;
	private float posY;
	private float posZ;
	private short floorId;
	private int timestamp;
	private short floorNo;
	private short reserved;
	private List<Beacon> beacons;

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

	public short getFloorId() {
		return this.floorId;
	}

	public void setFloorId(short floorId) {
		this.floorId = floorId;
	}

	public int getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public short getFloorNo() {
		return this.floorNo;
	}

	public void setFloorNo(short floorNo) {
		this.floorNo = floorNo;
	}

	public short getReserved() {
		return this.reserved;
	}

	public void setReserved(short reserved) {
		this.reserved = reserved;
	}

	public List<Beacon> getBeacons() {
		return this.beacons;
	}

	public void setBeacons(List<Beacon> beacons) {
		this.beacons = beacons;
	}

	public String toString() {
		return "TPosInfoBeacon [tagId=" + Long.toHexString(this.tagId.longValue()) + ", posX=" + this.posX + ", posY="
				+ this.posY + ", posZ=" + this.posZ + ", floorId=" + this.floorId + ", timestamp=" + this.timestamp
				+ ", floorNo=" + this.floorNo + ", reserved=" + this.reserved + ", beacons=" + this.beacons + "]";
	}
}