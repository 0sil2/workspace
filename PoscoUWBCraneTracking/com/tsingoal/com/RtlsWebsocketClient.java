package com.tsingoal.com;

import com.google.gson.Gson;
import java.net.URI;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class RtlsWebsocketClient extends WebSocketClient {
	private RtlsWsManager mManager = null;
	private String mName = null;
	private String mPasswd = null;
	private Gson gson = new Gson();

	public void setManager(RtlsWsManager manager) {
		this.mManager = manager;
	}

	public void setUserName(String userName) {
		this.mName = userName;
	}

	public void setPasswd(String passwd) {
		this.mPasswd = passwd;
	}

	public RtlsWebsocketClient(URI serverURI) {
		super(serverURI);
	}

	public RtlsWebsocketClient(URI serverUri, Draft draft, Map<String, String> headers, int connecttimeout) {
		super(serverUri, draft, headers, connecttimeout);
	}

	public void onOpen(ServerHandshake serverHandshake) {
		if (this.mName != null && this.mName != "" && this.mPasswd != null && this.mPasswd != "") {
			byte[] accessData = AccessUtil.CreateAccessData(this.mName, this.mPasswd);
			send(accessData);
		}
		if (this.mManager.getLogPrint() > 0) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date_str = df.format(new Date());
			System.out.println(
					"-- " + date_str + " 连接上websocket " + this.mManager.getProtocal() + ": " + getURI().toString());
		}

		this.mManager.OnOpen();
	}

	public void onMessage(String s) {
		try {
			if (null != this.mManager) {
				this.mManager.OnMessage(s);
			}

			JSONObject jsonObj = (JSONObject) (new JSONParser()).parse(s);

			Object video_obj = jsonObj.get("localsense_video_response");
			if (video_obj != null) {
				String info_str = video_obj.toString();
				JSONObject video_info = (JSONObject) (new JSONParser()).parse(info_str);
				TVideoInfo video = new TVideoInfo();
				video.setTagid(String.valueOf(video_info.get("tagid")));
				video.setUsr(String.valueOf(video_info.get("user")));
				video.setPwd(String.valueOf(video_info.get("pwd")));
				video.setId(String.valueOf(video_info.get("id")));
				video.setIp(String.valueOf(video_info.get("ip")));
				video.setPort(String.valueOf(video_info.get("port")));
				video.setType(String.valueOf(video_info.get("type")));
				video.setChannel(String.valueOf(video_info.get("channel")));
				if (null != this.mManager) {
					this.mManager.OnTrackTagVideoChanged(video);
				}
			}

			Object obj = jsonObj.get("localsense_conf_response");
			if (obj != null) {
				String info_str = obj.toString();
				JSONObject info = (JSONObject) (new JSONParser()).parse(info_str);
				String type = String.valueOf(info.get("conf_type"));
				String val = String.valueOf(info.get("conf_value"));
				boolean b_val = val.equals("enable");
				if (null != this.mManager) {

					try {
						RtlsWsManager.SwitchType2Show e_type = RtlsWsManager.SwitchType2Show.valueOf(type);
						this.mManager.OnSwitchChanged(e_type.getNtype(), b_val);
					} catch (IllegalArgumentException e) {

						System.out.println("未定义的总开关状态类型：" + type + "，无法转换：");
					}
				}
			}

			Object baseObj = jsonObj.get("baseid");
			if (baseObj != null) {
				Object tagsObj = jsonObj.get("tags");
				if (tagsObj != null) {
					String json = jsonObj.toString();
					TBaseTagRssiInfo baseTagRssi = (TBaseTagRssiInfo) this.gson.fromJson(json, TBaseTagRssiInfo.class);
					if (null != this.mManager) {
						this.mManager.OnBaseTagRssi(baseTagRssi);
					}
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public void onClose(int i, String s, boolean b) {
		if (this.mManager.getLogPrint() > 0) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date_str = df.format(new Date());
			System.out.println(
					"-- " + date_str + " websocket已关闭 " + this.mManager.getProtocal() + ": " + getURI().toString());
		}
	}

	public void onError(Exception e) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date_str = df.format(new Date());
		System.out.println("-- " + date_str + " websocket发生错误 " + this.mManager.getProtocal() + ": " + e);
	}

	public void onMessage(ByteBuffer blob) {
		FrameParse parser = new FrameParse();
		FrameParse.FrameType type = null;
		parser.setPos_out_type(this.mManager.getPos_mode());
		try {
			type = parser.Parse(blob, this.mManager.getTagidBit());
		} catch (Exception e) {

			String mess = e.toString();
			if (mess.contains("ArrayIndexOutOfBoundsException")) {
				System.out.println("Received less than expected data");

				return;
			}
		}
		Object result = parser.Result();
		if (this.mManager != null && result != null)
			if (type == FrameParse.FrameType.CAPACITY_DATA) {
				List<TCapacityInfo> allCaps = (List<TCapacityInfo>) result;
				this.mManager.OnCapacityInfo(allCaps);
			} else if (type == FrameParse.FrameType.POS_DATA) {
				List<TPosInfo> allPos = (List<TPosInfo>) result;
				this.mManager.OnPosInfo(allPos);
			} else if (type == FrameParse.FrameType.BEACON_DATA) {
				List<TPosInfoBeacon> allPos = (List<TPosInfoBeacon>) result;
				this.mManager.OnPosBeaconInfo(allPos);
			} else if (type == FrameParse.FrameType.SIMPLEALARM_DATA) {
				TSimpleAlarmInfo alarmInfo = (TSimpleAlarmInfo) result;
				this.mManager.OnSimpleAlarm(alarmInfo);
			} else if (type == FrameParse.FrameType.RICHALARM_DATA) {
				TRichAlarmInfo alarmInfo = (TRichAlarmInfo) result;
				this.mManager.OnRichAlarm(alarmInfo);
			} else if (type == FrameParse.FrameType.PERSONSTATISTICS_DATA) {
				if (result == null)
					return;
				TPersonStatistics statisticsInfo = (TPersonStatistics) result;
				this.mManager.OnPersonStatistics(statisticsInfo);
			} else if (type == FrameParse.FrameType.STATICS_AREA_DATA) {
				if (result == null)
					return;
				List<TAreaStatics> area_statics = (List<TAreaStatics>) result;
				this.mManager.OnAreaStatistics(area_statics);
			} else if (type == FrameParse.FrameType.UPDATE_DATA) {
				TUpdateInfo update = (TUpdateInfo) result;
				this.mManager.OnUpdate(update);
			} else if (type == FrameParse.FrameType.EXTENDED_INFO) {
				TExtendedInfo extended_info = (TExtendedInfo) result;
				this.mManager.OnExtendedInfo(extended_info);
			} else if (type == FrameParse.FrameType.ATTENDANCE_DATA) {
				TAttendanceStatics attendanceStatics = (TAttendanceStatics) result;
				this.mManager.OnAttendanceStatics(attendanceStatics);
			} else if (type == FrameParse.FrameType.BASESTATE_DATA) {
				TBaseState baseState = (TBaseState) result;
				this.mManager.OnBaseState(baseState);
			} else {
				this.mManager.OnUnknownMessage(blob);
			}
	}
}