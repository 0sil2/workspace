package com.tsingoal.com;

public class TVideoInfo {
	private String id;
	private String ip;
	private String port;
	private String type;
	private String channel;
	private String code;
	private String tagid;
	private String usr;
	private String pwd;
	private Boolean success;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return this.port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getChannel() {
		return this.channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String toString() {
		return "TVideoInfo [tagid=" + this.tagid + ", id=" + this.id + ", ip=" + this.ip + ", port=" + this.port
				+ ", type=" + this.type + ", channel=" + this.channel + ", code=" + this.code + "]";
	}

	public void setTagid(String tagid) {
		this.tagid = tagid;
	}

	public String getTagid() {
		return this.tagid;
	}

	public String getUsr() {
		return this.usr;
	}

	public void setUsr(String usr) {
		this.usr = usr;
	}

	public String getPwd() {
		return this.pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public Boolean getSuccess() {
		return this.success;
	}
}