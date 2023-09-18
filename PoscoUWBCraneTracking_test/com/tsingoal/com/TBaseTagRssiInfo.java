package com.tsingoal.com;

import java.util.List;

public class TBaseTagRssiInfo {
	private String baseid;
	private List<TagRssiInfo> tags;

	public String toString() {
		return "TBaseTagIssrInfo [baseid=" + this.baseid + ", tags=" + this.tags + "]";
	}

	public String getBaseid() {
		return this.baseid;
	}

	public void setBaseid(String baseid) {
		this.baseid = baseid;
	}

	public List<TagRssiInfo> getTags() {
		return this.tags;
	}

	public void setTags(List<TagRssiInfo> tags) {
		this.tags = tags;
	}

	public class TagRssiInfo {
		private String tagid;
		private Integer rssi;

		public String toString() {
			return "TagRssiInfo [tagid=" + this.tagid + ", rssi=" + this.rssi + "]";
		}

		public String getTagid() {
			return this.tagid;
		}

		public void setTagid(String tagid) {
			this.tagid = tagid;
		}

		public Integer getRssi() {
			return this.rssi;
		}

		public void setRssi(Integer rssi) {
			this.rssi = rssi;
		}
	}
}