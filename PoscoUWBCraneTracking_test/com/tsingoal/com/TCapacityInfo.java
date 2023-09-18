package com.tsingoal.com;

public class TCapacityInfo {
	private Long tagId;
	private Integer capacity;
	private boolean isCharged;

	public Long getTagId() {
		return this.tagId;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}

	public Integer getCapacity() {
		return this.capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public boolean isCharged() {
		return this.isCharged;
	}

	public void setCharged(boolean charged) {
		this.isCharged = charged;
	}

	public String toString() {
		return "TCapacityInfo{tagId=" + Long.toHexString(this.tagId.longValue()) + ", capacity=" + this.capacity
				+ ", isCharged=" + this.isCharged + '}';
	}
}