package com.tsingoal.com;

public class TUpdateInfo {
	private int type;
	private int handle_type;
	private String id_;

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getHandle_type() {
		return this.handle_type;
	}

	public void setHandle_type(int handle_type) {
		this.handle_type = handle_type;
	}

	public String getId_() {
		return this.id_;
	}

	public void setId_(String id_) {
		this.id_ = id_;
	}

	public String toString() {
		return "TUpdateInfo [type=" + this.type + ", handle_type=" + this.handle_type + ", id_=" + this.id_ + "]";
	}
}