package com.tsingoal.com;

public class TAttendanceStatics {
	private Long relatedTagId;
	private String relatedTagName;
	private long areaId;
	private String areaName;
	private int mapId;
	private String mapName;
	private int stat;
	private long timestamp;

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TAttendanceStatics other = (TAttendanceStatics) obj;
		if (this.areaId != other.areaId)
			return false;
		if (this.areaName == null) {
			if (other.areaName != null)
				return false;
		} else if (!this.areaName.equals(other.areaName)) {
			return false;
		}
		if (this.mapId != other.mapId)
			return false;
		if (this.mapName == null) {
			if (other.mapName != null)
				return false;
		} else if (!this.mapName.equals(other.mapName)) {
			return false;
		}
		if (this.relatedTagId != other.relatedTagId)
			return false;
		if (this.relatedTagName == null) {
			if (other.relatedTagName != null)
				return false;
		} else if (!this.relatedTagName.equals(other.relatedTagName)) {
			return false;
		}
		if (this.stat != other.stat)
			return false;
		if (this.timestamp != other.timestamp)
			return false;
		return true;
	}

	public Long getRelatedTagId() {
		return this.relatedTagId;
	}

	public void setRelatedTagId(Long relatedTagId) {
		this.relatedTagId = relatedTagId;
	}

	public String getRelatedTagName() {
		return this.relatedTagName;
	}

	public void setRelatedTagName(String relatedTagName) {
		this.relatedTagName = relatedTagName;
	}

	public long getAreaId() {
		return this.areaId;
	}

	public void setAreaId(long areaId) {
		this.areaId = areaId;
	}

	public String getAreaName() {
		return this.areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public int getMapId() {
		return this.mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	public String getMapName() {
		return this.mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public int getStat() {
		return this.stat;
	}

	public void setStat(int stat) {
		this.stat = stat;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String toString() {
		return "TAttendanceStatics [relatedTagId=" + Long.toHexString(this.relatedTagId.longValue())
				+ ", relatedTagName=" + this.relatedTagName + ", areaId=" + this.areaId + ", areaName=" + this.areaName
				+ ", mapId=" + this.mapId + ", mapName=" + this.mapName + ", stat=" + this.stat + ", timestamp="
				+ this.timestamp + "]";
	}
}