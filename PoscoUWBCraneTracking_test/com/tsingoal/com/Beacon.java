package com.tsingoal.com;

class Beacon {
	private String uuid;
	private short major;
	private short minor;
	private short rssi;

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public short getMajor() {
		return this.major;
	}

	public void setMajor(short major) {
		this.major = major;
	}

	public short getMinor() {
		return this.minor;
	}

	public void setMinor(short minor) {
		this.minor = minor;
	}

	public short getRssi() {
		return this.rssi;
	}

	public void setRssi(short rssi) {
		this.rssi = rssi;
	}

	public String toString() {
		return "Beacon [uuid=" + this.uuid + ", major=" + this.major + ", minor=" + this.minor + ", rssi=" + this.rssi
				+ "]";
	}
}