package com.tsingoal.com;

import java.util.ArrayList;
import java.util.List;

public class TAreaStatics {
	private Long nAreaID;
	private String nAreaName;
	public List<TagDetail> tag_rtls;

	public class TagDetail {
		public Long tagid;
		public String tagname;
		public String groupname;
		public int stat;

		public Long getTagid() {
			return this.tagid;
		}

		public Long entry_time;
		public Long leave_time;
		public int stay_time;
		public int isRelevanceDm;

		public String getTagname() {
			return this.tagname;
		}

		public String getGroupname() {
			return this.groupname;
		}

		public int getStat() {
			return this.stat;
		}

		public Long getEntry_time() {
			return this.entry_time;
		}

		public Long getLeave_time() {
			return this.leave_time;
		}

		public int getStay_time() {
			return this.stay_time;
		}

		public void setTagid(Long tagid) {
			this.tagid = tagid;
		}

		public void setTagname(String tagname) {
			this.tagname = tagname;
		}

		public void setGroupname(String groupname) {
			this.groupname = groupname;
		}

		public void setStat(int stat) {
			this.stat = stat;
		}

		public void setEntry_time(Long entry_time) {
			this.entry_time = entry_time;
		}

		public void setLeave_time(Long leave_time) {
			this.leave_time = leave_time;
		}

		public void setStay_time(int stay_time) {
			this.stay_time = stay_time;
		}

		public int getIsRelevanceDm() {
			return this.isRelevanceDm;
		}

		public void setIsRelevanceDm(int isRelevanceDm) {
			this.isRelevanceDm = isRelevanceDm;
		}

		public String toString() {
			return "TagDetail [tagid=" + Long.toHexString(this.tagid.longValue()) + ", tagname=" + this.tagname
					+ ", groupname=" + this.groupname + ", stat=" + this.stat + ", entry_time=" + this.entry_time
					+ ", leave_time=" + this.leave_time + ", stay_time=" + this.stay_time + ", isRelevanceDm="
					+ this.isRelevanceDm + "]";
		}
	}

	public TAreaStatics() {
		this.nAreaID = Long.valueOf(0L);
		this.nAreaName = "";
		this.tag_rtls = new ArrayList<>();
	}

	public void Append(TagDetail tag) {
		this.tag_rtls.add(tag);
	}

	public Long getnAreaID() {
		return this.nAreaID;
	}

	public void setnAreaID(Long nAreaID) {
		this.nAreaID = nAreaID;
	}

	public String getnAreaName() {
		return this.nAreaName;
	}

	public void setnAreaName(String nAreaName) {
		this.nAreaName = nAreaName;
	}

	public List<TagDetail> getTag_rtls() {
		return this.tag_rtls;
	}

	public void setTag_rtls(List<TagDetail> tagRtls) {
		this.tag_rtls = tagRtls;
	}

	public String toString() {
		return "TAreaStatics [nAreaID=" + this.nAreaID + ", nAreaName=" + this.nAreaName + ", tag_rtls=" + this.tag_rtls
				+ "]";
	}
}