package com.tsingoal.com;

import java.util.ArrayList;
import java.util.List;

public class TPersonStatistics {
	public int totalCount;
	public int onlineCount;
	public List<StatisticOneReg> detailResult;

	public class StatisticOneReg {
		public int floorId;
		public String mapName;
		public int onlineCount;
		public List<Long> onlineTags;

		public int getFloorId() {
			return this.floorId;
		}

		public void setFloorId(int floorId) {
			this.floorId = floorId;
		}

		public String getMapName() {
			return this.mapName;
		}

		public void setMapName(String mapName) {
			this.mapName = mapName;
		}

		public int getOnlineCount() {
			return this.onlineCount;
		}

		public void setOnlineCount(int onlineCount) {
			this.onlineCount = onlineCount;
		}

		public List<Long> getOnlineTags() {
			return this.onlineTags;
		}

		public String onlineTagsString() {
			List<String> s = new ArrayList<>();
			for (Long id : this.onlineTags) {
				s.add(Long.toHexString(id.longValue()));
			}
			return s.toString();
		}

		public void setOnlineTags(List<Long> onlineTags) {
			this.onlineTags = onlineTags;
		}

		public String toString() {
			return "StatisticOneReg{floorId=" + this.floorId + ", mapName='" + this.mapName + '\'' + ", onlineCount="
					+ this.onlineCount + ", onlineTags=" +

					onlineTagsString() + '}';
		}
	}

	public int getTotalCount() {
		return this.totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getOnlineCount() {
		return this.onlineCount;
	}

	public void setOnlineCount(int onlineCount) {
		this.onlineCount = onlineCount;
	}

	public List<StatisticOneReg> getDetailResult() {
		return this.detailResult;
	}

	public void setDetailResult(List<StatisticOneReg> detailResult) {
		this.detailResult = detailResult;
	}

	public String toString() {
		return "TPersonStatistics{totalCount=" + this.totalCount + ", onlineCount=" + this.onlineCount
				+ ", detailResult=" + this.detailResult + '}';
	}
}