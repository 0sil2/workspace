package com.tsingoal.com;

import java.util.ArrayList;
import java.util.List;

public class TBaseState {
	private int baseNum;
	public List<BaseInfo> base_rtls;

	public class BaseInfo {
		private int baseId;
		private int baseState;
		private float posX;
		private float posY;
		private float posZ;
		private int regid;

		public BaseInfo() {
		}

		public BaseInfo(int baseId, int baseState, float posX, float posY, float posZ, int regid) {
			this.baseId = baseId;
			this.baseState = baseState;
			this.posX = posX;
			this.posY = posY;
			this.posZ = posZ;
			this.regid = regid;
		}

		public String toString() {
			return "BaseInfo [baseId=" + this.baseId + ", baseState=" + this.baseState + ", posX=" + this.posX
					+ ", posY=" + this.posY + ", posZ=" + this.posZ + ", regid=" + this.regid + "]";
		}

		public int getBaseId() {
			return this.baseId;
		}

		public void setBaseId(int baseId) {
			this.baseId = baseId;
		}

		public int getBaseState() {
			return this.baseState;
		}

		public void setBaseState(int baseState) {
			this.baseState = baseState;
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

		public int getRegid() {
			return this.regid;
		}

		public void setRegid(int regid) {
			this.regid = regid;
		}
	}

	public TBaseState() {
		this.baseNum = 0;
		this.base_rtls = new ArrayList<>();
	}

	public TBaseState(int baseNum, List<BaseInfo> base_rtls) {
		this.baseNum = baseNum;
		this.base_rtls = base_rtls;
	}

	public String toString() {
		return "TBaseState [baseNum=" + this.baseNum + ", base_rtls=" + this.base_rtls + "]";
	}

	public int getBaseNum() {
		return this.baseNum;
	}

	public void setBaseNum(int baseNum) {
		this.baseNum = baseNum;
	}

	public List<BaseInfo> getBase_rtls() {
		return this.base_rtls;
	}

	public void setBase_rtls(List<BaseInfo> base_rtls) {
		this.base_rtls = base_rtls;
	}
}