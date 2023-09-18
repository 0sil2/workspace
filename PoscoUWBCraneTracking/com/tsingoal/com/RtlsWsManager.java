package com.tsingoal.com;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.net.ssl.SSLContext;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.protocols.Protocol;
import org.json.simple.JSONObject;

public abstract class RtlsWsManager {
	private String serverIp = null;
	private int serverPort = 0;
	private String wsProtocal = null;
	private RtlsWebsocketClient wc = null;
	private String wsUserName = null;
	private String wsUserPasswd = null;
	private Boolean wsCloseMannual = Boolean.valueOf(false);
	private Timer dogCheckTimer = null;
	private int m_log_level = 1;
	private Boolean isWss = Boolean.valueOf(false);
	private int tagidBit = 64;

	private PosOutMode pos_mode;

	public RtlsWsManager() {
		this.pos_mode = PosOutMode.XY;
	}

	public RtlsWsManager(String userName, String password) {
		this.wsUserName = userName;
		this.wsUserPasswd = Md5Util.MD5Encode(password, "");
		this.pos_mode = PosOutMode.XY;
	}

	public RtlsWsManager(String userName, String password, Boolean isMD5) {
		this.wsUserName = userName;
		if (isMD5.booleanValue()) {
			this.wsUserPasswd = password;
		} else {
			this.wsUserPasswd = Md5Util.MD5Encode(password, "");
		}
		this.pos_mode = PosOutMode.XY;
	}

	public void setPos_mode(PosOutMode pos_mode) {
		this.pos_mode = pos_mode;
	}

	public PosOutMode getPos_mode() {
		return this.pos_mode;
	}

	public Boolean getIsWss() {
		return this.isWss;
	}

	public void setIsWss(Boolean isWss) {
		this.isWss = isWss;
	}

	public void setHost(String server_ip) {
		this.serverIp = server_ip;
	}

	public void setServerPort(int port) {
		this.serverPort = port;
	}

	public void setProtocal(String protocal) {
		this.wsProtocal = protocal;
	}

	public void setWsUserName(String wsUserName) {
		this.wsUserName = wsUserName;
	}

	public void setWsUserPasswd(String wsUserPasswd) {
		this.wsUserPasswd = Md5Util.MD5Encode(wsUserPasswd, "");
	}

	public void setWsUserPasswd(String wsUserPasswd, String salt) {
		if (salt != null && !salt.equals("")) {
			this.wsUserPasswd = Md5Util.MD5Encode(Md5Util.MD5Encode(wsUserPasswd, "") + salt, "");
		} else {
			this.wsUserPasswd = Md5Util.MD5Encode(wsUserPasswd, "");
		}
	}

	public void setWsUserPasswdNoMD5(String wsUserPasswd) {
		this.wsUserPasswd = wsUserPasswd;
	}

	public void setLogPrint(int val) {
		this.m_log_level = val;
	}

	public int getTagidBit() {
		return this.tagidBit;
	}

	public void setTagidBit(int tagidBit) {
		this.tagidBit = tagidBit;
	}

	public void connectToServer() {
		setWsMannualClose(Boolean.valueOf(false));
		newAndConnecWebSocket();
		timerCheckWebSocket();
	}

	public void closeWebSocket() {
		if (getWc() != null) {
			setWsMannualClose(Boolean.valueOf(true));
			getWc().close();
			setWc(null);
		}
	}

	public String GetHost() {
		return this.serverIp;
	}

	public int GetServerPort() {
		return this.serverPort;
	}

	public String getProtocal() {
		return this.wsProtocal;
	}

	public String getWsUserName() {
		return this.wsUserName;
	}

	public String getWsUserPasswd() {
		return this.wsUserPasswd;
	}

	public WebSocket.READYSTATE getWsState() {
		return getWc().getReadyState();
	}

	public int getLogPrint() {
		return this.m_log_level;
	}

	public void send(String str) {
		if (getWc() != null) {
			getWc().send(str);
		}
	}

	public void send(byte[] bs) {
		if (getWc() != null) {
			getWc().send(bs);
		}
	}

	public enum PosOutMode {
		XY, GEO, GLOBAL, XY_GEO, XY_GLOBAL;
	}

	public enum TagGrpMap {
		TAG, GRP, MAP;
	}

	public enum SwitchType2Show {
		eefence(1), noaccompany(2), rollcall(3), vibrate(4), arraign(5), overman(6), inside_out(7), out_service(8);

		private int ntype;

		SwitchType2Show(int _ntype) {
			this.ntype = _ntype;
		}

		int getNtype() {
			return this.ntype;
		}
	}

	public void subInfo(TagGrpMap ntype, List<String> ids) {
		byte[] subInfoTest = getSubReqParamStr(true, ids, ntype);
		send(subInfoTest);
	}

	public void unsubInfo(TagGrpMap ntype, List<String> ids) {
		byte[] subInfoTest = getSubReqParamStr(false, ids, ntype);
		send(subInfoTest);
	}

	public void tagVibrateAndShake(String tagid, String typeConf, String typeValue) {
		try {
			Map<String, Object> outter = new HashMap<>();
			Map<String, Object> inner = new HashMap<>();
			inner.put("tagid", tagid);
			inner.put("conf_type", typeConf);
			inner.put("conf_value", typeValue);
			outter.put("localsense_conf_request", inner);
			String jsStr = JSONObject.toJSONString(outter);

			send(jsStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void trackTagVideo(String tagid) {
		try {
			Map<String, Object> outter = new HashMap<>();
			Map<String, Object> inner = new HashMap<>();
			inner.put("tagid", tagid);
			inner.put("track", "true");
			outter.put("localsense_video_request", inner);
			String jsStr = JSONObject.toJSONString(outter);

			send(jsStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void trackTagVideo(Integer tagid) {
		try {
			Map<String, Object> outter = new HashMap<>();
			Map<String, Object> inner = new HashMap<>();
			inner.put("tagid", Integer.toString(tagid.intValue()));
			inner.put("track", "true");
			outter.put("localsense_video_request", inner);
			String jsStr = JSONObject.toJSONString(outter);

			send(jsStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void unTrackTagVideo(String tagid) {
		try {
			Map<String, Object> outter = new HashMap<>();
			Map<String, Object> inner = new HashMap<>();
			inner.put("tagid", tagid);
			inner.put("track", "false");
			outter.put("localsense_video_request", inner);
			String jsStr = JSONObject.toJSONString(outter);

			send(jsStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void unTrackTagVideo(Integer tagid) {
		try {
			Map<String, Object> outter = new HashMap<>();
			Map<String, Object> inner = new HashMap<>();
			inner.put("tagid", Integer.toString(tagid.intValue()));
			inner.put("track", "false");
			outter.put("localsense_video_request", inner);
			String jsStr = JSONObject.toJSONString(outter);

			send(jsStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setSwitchOn(int ntype, boolean opened) {
		try {
			Map<String, Object> outter = new HashMap<>();
			Map<String, Object> inner = new HashMap<>();
			inner.put("conf_type", getSwitchReqStrByType(ntype));
			inner.put("conf_value", opened ? "enable" : "disable");
			outter.put("localsense_conf_request", inner);
			String jsStr = JSONObject.toJSONString(outter);

			send(jsStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int GetVerMajor() {
		return 2;
	}

	public int GetVerMinor() {
		return 2;
	}

	private synchronized RtlsWebsocketClient getWc() {
		return this.wc;
	}

	private synchronized void setWc(RtlsWebsocketClient wc) {
		this.wc = wc;
	}

	private synchronized Boolean getWsMannualClose() {
		return this.wsCloseMannual;
	}

	private synchronized void setWsMannualClose(Boolean close) {
		this.wsCloseMannual = close;
	}

	private void newAndConnecWebSocket() {
		String wsType = "";
		if (this.isWss.booleanValue()) {
			wsType = "wss://";

			try {
				SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
				sslContext.init(null, null, null);
				SSLContext.setDefault(sslContext);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else {
			wsType = "ws://";
		}
		if (getWc() != null) {
			getWc().close();
			setWc(null);
		}

		Map<String, String> headers = new HashMap<>();

		try {
			Draft_6455 draft = null;
			if (this.wsProtocal != null) {
				draft = new Draft_6455(Collections.emptyList(),	Collections.singletonList(new Protocol(this.wsProtocal)));
			} else {

				draft = new Draft_6455();
			}
			RtlsWebsocketClient webSocketClient = new RtlsWebsocketClient(
					new URI(wsType + this.serverIp + ":" + this.serverPort), (Draft) draft, headers, 300);
			webSocketClient.setUserName(this.wsUserName);
			webSocketClient.setPasswd(this.wsUserPasswd);
			setWc(webSocketClient);
			this.wc.setManager(this);
			webSocketClient.connectBlocking();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void timerCheckWebSocket() {
		if (this.dogCheckTimer == null) {
			this.dogCheckTimer = new Timer();
		}
		this.dogCheckTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if (RtlsWsManager.this.getWsMannualClose().booleanValue() || RtlsWsManager.this.getWc() == null)
					return;
				WebSocket.READYSTATE dogReadyState = RtlsWsManager.this.getWc().getReadyState();
				if (!dogReadyState.equals(WebSocket.READYSTATE.OPEN)) {
					try {
						RtlsWsManager.this.newAndConnecWebSocket();
					} catch (Exception e) {

						e.printStackTrace();
					}
				}
			}
		}, 5000L, 5000L);
	}

	protected byte[] getSubReqParamStr(boolean sub_or_unSub, List<String> ids, TagGrpMap tagGrpMap) {
		byte[] data = null;
		try {
			int KNOWN_LEN = 11;

			data = new byte[KNOWN_LEN + 2048];

			data[0] = -52;
			data[1] = 95;
			data[2] = -87;
			data[3] = 0;
			data[4] = (tagGrpMap == TagGrpMap.TAG) ? 0 : ((tagGrpMap == TagGrpMap.GRP) ? 1 : 2);

			int offset = 5;
			if (!sub_or_unSub) {
				data[5] = 0;
				data[6] = 0;
			} else {
				byte[] tagLenBytes = ByteUtil.shortToByteArray((short) ids.size());
				for (int j = 0; j < 2; j++)
					data[j + offset] = tagLenBytes[j];
			}
			offset += 2;

			for (String obj : ids) {
				if (tagGrpMap == TagGrpMap.TAG) {
					if (this.tagidBit == 32) {
						int k = Integer.parseInt(obj);
						byte[] arrayOfByte = ByteUtil.int2Bytes(k);
						for (int m = 0; m < 4; m++)
							data[m + offset] = arrayOfByte[m];
						offset += 4;
						continue;
					}
					long val = Long.parseLong(obj);
					byte[] tagBytes = ByteUtil.long2Bytes(val);
					for (int j = 0; j < 8; j++)
						data[j + offset] = tagBytes[j];
					offset += 8;
					continue;
				}
				if (tagGrpMap == TagGrpMap.MAP) {
					int val = Integer.parseInt(obj);
					byte[] tagBytes = ByteUtil.int2Bytes(val);
					for (int j = 0; j < 4; j++)
						data[j + offset] = tagBytes[j];
					offset += 4;
					continue;
				}
				if (tagGrpMap == TagGrpMap.GRP) {
					String grpVal = obj;

					data[offset++] = (byte) (grpVal.length() >> 8);
					data[offset++] = (byte) grpVal.length();

					byte[] tagBytes = grpVal.getBytes();
					for (int j = 0; j < grpVal.length(); j++) {
						data[offset++] = tagBytes[j];
					}
				}
			}

			byte[] crcbuff = new byte[offset - 2];
			for (int i = 0; i < crcbuff.length; i++) {
				crcbuff[i] = data[i + 2];
			}
			int crc = Crc16.calcCrc16(crcbuff);
			byte[] crcres = ByteUtil.int2Bytes(crc);
			data[offset] = crcres[2];
			data[offset + 1] = crcres[3];
			offset += 2;

			data[offset] = -86;
			data[offset + 1] = -69;

			return data;
		} catch (Exception e) {
			e.printStackTrace();

			return data;
		}
	}

	private String getSwitchReqStrByType(int type) {
		String reqStr = "";
		switch (type) {
		case 1:
			reqStr = "eefence";

			return reqStr;
		case 2:
			reqStr = "noaccompany";
			return reqStr;
		case 3:
			reqStr = "rollcall";
			return reqStr;
		case 4:
			reqStr = "arraign";
			return reqStr;
		case 5:
			reqStr = "vibrate";
			return reqStr;
		case 6:
			reqStr = "overman";
			return reqStr;
		}
		return "";
	}

	public abstract void OnOpen();

	public abstract void OnCapacityInfo(List<TCapacityInfo> paramList);

	public abstract void OnPosInfo(List<TPosInfo> paramList);

	public abstract void OnPosBeaconInfo(List<TPosInfoBeacon> paramList);

	public abstract void OnSimpleAlarm(TSimpleAlarmInfo paramTSimpleAlarmInfo);

	public abstract void OnRichAlarm(TRichAlarmInfo paramTRichAlarmInfo);

	public abstract void OnPersonStatistics(TPersonStatistics paramTPersonStatistics);

	public abstract void OnAreaStatistics(List<TAreaStatics> paramList);

	public abstract void OnUpdate(TUpdateInfo paramTUpdateInfo);

	public abstract void OnExtendedInfo(TExtendedInfo paramTExtendedInfo);

	public abstract void OnTrackTagVideoChanged(TVideoInfo paramTVideoInfo);

	public abstract void OnSwitchChanged(int paramInt, boolean paramBoolean);

	public abstract void OnUnknownMessage(ByteBuffer paramByteBuffer);

	public abstract void OnAttendanceStatics(TAttendanceStatics paramTAttendanceStatics);

	public abstract void OnBaseState(TBaseState paramTBaseState);

	public abstract void OnBaseTagRssi(TBaseTagRssiInfo paramTBaseTagRssiInfo);

	public abstract void OnMessage(String paramString);
}