package com.tsingoal.com;

public class TExtendedInfo {
	private Long tagid;
	private int type;
	private Object val;
	private long time_;

	public Long getTagid() {
		return this.tagid;
	}

	public void setTagid(Long tagid) {
		this.tagid = tagid;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Object getVal() {
		return this.val;
	}

	public void setVal(Object val) {
		this.val = val;
	}

	public long getTime_() {
		return this.time_;
	}

	public void setTime_(long time_) {
		this.time_ = time_;
	}

	public String toString() {
		return "TExtendedInfo [tagid=" + Long.toHexString(this.tagid.longValue()) + ", type=" + this.type + ", val="
				+ this.val + ", time_=" + this.time_ + "]";
	}
}