package com.tsingoal.com;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tsingoal.com.TRichAlarmInfo.EEfenceMore;

public class FrameParse {
	public static final int TAGID_32BIT = 32;
	public static final int TAGID_64BIT = 64;
	public static final int kFrameHead = 52319;
	public static final int kFrameTail = 43707;
	private static final int kFrameTypePos_16Bit = 1;
	private static final int kFrameTypeSimpleAlarm_16Bit = 3;
	private static final int kFrameTypeCap_16Bit = 5;
	private static final int kFrameTypeRichAlarm_16Bit = 9;
	private static final int kFrameTypePersonStatistics_16Bit = 49;
	private static final int kFrameTypeAreaStatics_16Bit = 33;
	private static final int kFrameTypeUpdate_16Bit = 2;
	private static final int kFrameTypeExtended_16Bit = 8;
	private static final int kFrameTypeAttendanceStatics_16Bit = 51;
	private static final int kFrameTypeBaseState_16Bit = 7;
	private static final int kFrameTypePos_32Bit = 129;
	private static final int kFrameTypeBeacon_32Bit = 143;
	private static final int kFrameTypeSimpleAlarm_32Bit = 131;
	private static final int kFrameTypeCap_32Bit = 133;
	private static final int kFrameTypeRichAlarm_32Bit = 137;
	private static final int kFrameTypePersonStatistics_32Bit = 177;
	private static final int kFrameTypeAreaStatics_32Bit = 161;
	private static final int kFrameTypeUpdate_32Bit = 130;
	private static final int kFrameTypeExtended_32Bit = 136;
	private static final int kFrameTypeAttendanceStatics_32Bit = 179;
	private static final int kFrameTypeBaseState_32Bit = 135;
	private static final int kFrameTypeGlobalgraphicPos_32Bit = 181;
	private static final int kFrameTypeWGS_32Bit = 180;
	private Object parse_result = null;

	private RtlsWsManager.PosOutMode pos_out_type;

	public RtlsWsManager.PosOutMode getPos_out_type() {
		return this.pos_out_type;
	}

	public void setPos_out_type(RtlsWsManager.PosOutMode pos_out_type) {
		this.pos_out_type = pos_out_type;
	}

	public enum FrameType {
		NOT_KNOWN, POS_DATA, BEACON_DATA, CAPACITY_DATA, SIMPLEALARM_DATA, RICHALARM_DATA, PERSONSTATISTICS_DATA,
		STATICS_AREA_DATA, UPDATE_DATA, EXTENDED_INFO, ATTENDANCE_DATA, BASESTATE_DATA;
	}

	public enum ID_XBIT {
		ID_16BIT, ID_32BIT;
	}

	public FrameType Parse(ByteBuffer blob, int tagidBit) {
		FrameType type = FrameType.NOT_KNOWN;
		if (blob.hasRemaining()) {
			int frameHead = blob.getShort() & 0xFFFF;
			int frameTail = (blob.get(blob.capacity() - 2) & 0xFF) * 256 + (blob.get(blob.capacity() - 1) & 0xFF);
			if (frameHead == 52319 && frameTail == 43707) {
				int frameType = blob.get();
				if (frameType < 0) {
					frameType += 256;
				}
				int len = blob.remaining();
				byte[] data = new byte[len];
				blob.get(data, 0, len);

				switch (frameType) {
				case 1:
					type = FrameType.POS_DATA;
					ParsePos(data, len, ID_XBIT.ID_16BIT, tagidBit);

					return type;
				case 129:
					type = FrameType.POS_DATA;
					if (this.pos_out_type == RtlsWsManager.PosOutMode.XY
							|| this.pos_out_type == RtlsWsManager.PosOutMode.XY_GEO
							|| this.pos_out_type == RtlsWsManager.PosOutMode.XY_GLOBAL)
						ParsePos(data, len, ID_XBIT.ID_32BIT, tagidBit);
					return type;
				case 143:
					type = FrameType.BEACON_DATA;
					ParsePosBeacon(data, len, ID_XBIT.ID_32BIT, tagidBit);
					return type;
				case 180:
					type = FrameType.POS_DATA;
					if (this.pos_out_type == RtlsWsManager.PosOutMode.GEO
							|| this.pos_out_type == RtlsWsManager.PosOutMode.XY_GEO) {
						int BITGEO = 64;
						ParsePos(data, len, ID_XBIT.ID_32BIT, BITGEO);
						List<TPosInfo> allPos = (List<TPosInfo>) this.parse_result;
						for (TPosInfo tPosInfo : allPos) {
							tPosInfo.setPosX(tPosInfo.getPosX() / 100000.0F);
							tPosInfo.setPosY(tPosInfo.getPosY() / 100000.0F);
							tPosInfo.setPosZ(tPosInfo.getPosZ() / 100000.0F);
							tPosInfo.setGeoGraphicCoord(true);
						}
					}
					return type;
				case 181:
					type = FrameType.POS_DATA;
					if (this.pos_out_type == RtlsWsManager.PosOutMode.GLOBAL
							|| this.pos_out_type == RtlsWsManager.PosOutMode.XY_GLOBAL) {
						ParsePos(data, len, ID_XBIT.ID_32BIT, tagidBit);
						List<TPosInfo> allPos = (List<TPosInfo>) this.parse_result;
						for (TPosInfo tPosInfo : allPos)
							tPosInfo.setGlobalGraphicCoord(true);
					}
					return type;
				case 5:
					type = FrameType.CAPACITY_DATA;
					ParseCapacity(data, len, ID_XBIT.ID_16BIT, tagidBit);
					return type;
				case 133:
					type = FrameType.CAPACITY_DATA;
					ParseCapacity(data, len, ID_XBIT.ID_32BIT, tagidBit);
					return type;
				case 3:
					type = FrameType.SIMPLEALARM_DATA;
					ParseAlarm(data, len, Boolean.TRUE, ID_XBIT.ID_16BIT, tagidBit);
					return type;
				case 131:
					type = FrameType.SIMPLEALARM_DATA;
					ParseAlarm(data, len, Boolean.TRUE, ID_XBIT.ID_32BIT, tagidBit);
					return type;
				case 9:
					type = FrameType.RICHALARM_DATA;
					ParseAlarm(data, len, Boolean.FALSE, ID_XBIT.ID_16BIT, tagidBit);
					return type;
				case 137:
					type = FrameType.RICHALARM_DATA;
					ParseAlarm(data, len, Boolean.FALSE, ID_XBIT.ID_32BIT, tagidBit);
					return type;
				case 49:
					type = FrameType.PERSONSTATISTICS_DATA;
					ParsePersonStatistics(data, len, ID_XBIT.ID_16BIT, tagidBit);
					return type;
				case 177:
					type = FrameType.PERSONSTATISTICS_DATA;
					ParsePersonStatistics(data, len, ID_XBIT.ID_32BIT, tagidBit);
					return type;
				case 33:
					type = FrameType.STATICS_AREA_DATA;
					ParseAreaStatistics(data, len, ID_XBIT.ID_16BIT, tagidBit);
					return type;
				case 161:
					type = FrameType.STATICS_AREA_DATA;
					ParseAreaStatistics(data, len, ID_XBIT.ID_32BIT, tagidBit);
					return type;
				case 2:
					type = FrameType.UPDATE_DATA;
					ParseUpdate(data, len, ID_XBIT.ID_16BIT, tagidBit);
					return type;
				case 130:
					type = FrameType.UPDATE_DATA;
					ParseUpdate(data, len, ID_XBIT.ID_32BIT, tagidBit);
					return type;
				case 8:
					type = FrameType.EXTENDED_INFO;
					ParseExtendedInfo(data, len, ID_XBIT.ID_16BIT, tagidBit);
					return type;
				case 136:
					type = FrameType.EXTENDED_INFO;
					ParseExtendedInfo(data, len, ID_XBIT.ID_32BIT, tagidBit);
					return type;
				case 51:
					type = FrameType.ATTENDANCE_DATA;
					ParseAttendanceStatice(data, len, ID_XBIT.ID_16BIT, tagidBit);
					return type;
				case 179:
					type = FrameType.ATTENDANCE_DATA;
					ParseAttendanceStatice(data, len, ID_XBIT.ID_32BIT, tagidBit);
					return type;
				case 7:
					type = FrameType.BASESTATE_DATA;
					ParseBaseState(data, len, ID_XBIT.ID_16BIT, tagidBit);
					return type;
				case 135:
					type = FrameType.BASESTATE_DATA;
					ParseBaseState(data, len, ID_XBIT.ID_32BIT, tagidBit);
					return type;
				}
				return FrameType.NOT_KNOWN;
			}
		}
		return type;
	}

	public Object Result() {
		return this.parse_result;
	}

	private void ParsePos(byte[] data, int len, ID_XBIT bitX, int tagidBit) {
		if (len < 1) {
			return;
		}
		int tagNum = data[0] & 0xFF;
		if (len < 1 + tagNum * 21) {
			return;
		}
		int currentNum = 0;
		int offset = 1;
		List<TPosInfo> posLst = new ArrayList<>();
		while (currentNum < tagNum) {
			TPosInfo posInfo = new TPosInfo();
			long tagId = 0L;
			if (bitX == ID_XBIT.ID_16BIT) {

				byte[] tagbuff = new byte[2];
				tagbuff[0] = data[offset];
				tagbuff[1] = data[offset + 1];
				offset += 2;
				tagId = ByteUtil.byte2UShort(tagbuff);
			} else if (bitX == ID_XBIT.ID_32BIT) {
				if (tagidBit == 32) {
					byte[] tagbuff = new byte[4];
					tagbuff[0] = data[offset];
					tagbuff[1] = data[offset + 1];
					tagbuff[2] = data[offset + 2];
					tagbuff[3] = data[offset + 3];
					offset += 4;
					tagId = ByteUtil.bytes2Int(tagbuff);
				} else {
					byte[] tagbuff = new byte[8];
					for (int i = 0; i < 8; i++) {
						tagbuff[i] = data[offset + i];
					}
					offset += 8;
					tagId = ByteUtil.bytes2Long(tagbuff);
				}
			}

			posInfo.setTagId(Long.valueOf(tagId));

			byte[] posXbuff = new byte[4];
			posXbuff[0] = data[offset];
			posXbuff[1] = data[offset + 1];
			posXbuff[2] = data[offset + 2];
			posXbuff[3] = data[offset + 3];

			posInfo.setPosX(ByteUtil.bytes2Int(posXbuff) / 100.0F);
			offset += 4;

			byte[] posYbuff = new byte[4];
			posYbuff[0] = data[offset];
			posYbuff[1] = data[offset + 1];
			posYbuff[2] = data[offset + 2];
			posYbuff[3] = data[offset + 3];
			posInfo.setPosY(ByteUtil.bytes2Int(posYbuff) / 100.0F);

			offset += 4;

			byte[] posZbuff = new byte[2];
			posZbuff[0] = data[offset];
			posZbuff[1] = data[offset + 1];
			posInfo.setPosZ(ByteUtil.byte2Short(posZbuff) / 100.0F);
			offset += 2;

			posInfo.setFloorId((short) ByteUtil.byteToInt16(data[offset]));
			offset++;

			posInfo.setCapcity((short) ByteUtil.byteToInt16(data[offset]));
			offset++;

			posInfo.setSleep(Boolean.valueOf(((data[offset] & 0x10) != 0)));
			posInfo.setCharged(Boolean.valueOf(((data[offset] & 0x1) != 0)));
			offset++;

			byte[] timebuff = new byte[4];
			timebuff[0] = data[offset];
			timebuff[1] = data[offset + 1];
			timebuff[2] = data[offset + 2];
			timebuff[3] = data[offset + 3];
			posInfo.setTimestamp(ByteUtil.bytes2Int(timebuff));
			offset += 4;

			posInfo.setFloorNo((short) ByteUtil.byteToInt16(data[offset]));
			offset++;
			posInfo.setPosIndicator((short) ByteUtil.byteToInt16(data[offset]));
			offset++;
			posLst.add(posInfo);

			currentNum++;
		}
		this.parse_result = posLst;
	}

	private void ParsePosBeacon(byte[] data, int len, ID_XBIT bitX, int tagidBit) {
		if (tagidBit != 64) {
			return;
		}

		if (len < 1) {
			return;
		}
		int tagNum = data[0] & 0xFF;
		int currentNum = 0;
		int offset = 1;
		List<TPosInfoBeacon> posLst = new ArrayList<>();
		while (currentNum < tagNum) {
			TPosInfoBeacon posInfo = new TPosInfoBeacon();

			long tagId = 0L;
			byte[] tagbuff = new byte[8];
			for (int i = 0; i < 8; i++) {
				tagbuff[i] = data[offset + i];
			}
			offset += 8;
			tagId = ByteUtil.bytes2Long(tagbuff);
			posInfo.setTagId(Long.valueOf(tagId));

			byte[] posXbuff = new byte[4];
			posXbuff[0] = data[offset];
			posXbuff[1] = data[offset + 1];
			posXbuff[2] = data[offset + 2];
			posXbuff[3] = data[offset + 3];
			posInfo.setPosX(ByteUtil.bytes2Int(posXbuff) / 100.0F);
			offset += 4;

			byte[] posYbuff = new byte[4];
			posYbuff[0] = data[offset];
			posYbuff[1] = data[offset + 1];
			posYbuff[2] = data[offset + 2];
			posYbuff[3] = data[offset + 3];
			posInfo.setPosY(ByteUtil.bytes2Int(posYbuff) / 100.0F);
			offset += 4;

			byte[] posZbuff = new byte[2];
			posZbuff[0] = data[offset];
			posZbuff[1] = data[offset + 1];
			posInfo.setPosZ(ByteUtil.byte2Short(posZbuff) / 100.0F);
			offset += 2;

			byte[] floorBuff = new byte[2];
			floorBuff[0] = data[offset];
			floorBuff[1] = data[offset + 1];
			posInfo.setFloorId((short) ByteUtil.byte2UShort(floorBuff));
			offset += 2;

			byte[] timebuff = new byte[4];
			timebuff[0] = data[offset];
			timebuff[1] = data[offset + 1];
			timebuff[2] = data[offset + 2];
			timebuff[3] = data[offset + 3];
			posInfo.setTimestamp(ByteUtil.bytes2Int(timebuff));
			offset += 4;

			posInfo.setFloorNo((short) ByteUtil.byteToInt16(data[offset]));
			offset++;

			posInfo.setReserved((short) ByteUtil.byteToInt16(data[offset]));
			offset++;

			int beaconNum = data[0] & 0xFF;
			int oneBeaconLen = 21;
			List<Beacon> beacons = new ArrayList<>();
			for (int ii = 0; ii < beaconNum / oneBeaconLen; ii++) {
				Beacon beacon = new Beacon();

				int ttt = 0;
				byte[] uuidBuf = new byte[16];
				while (ttt <= 15) {
					uuidBuf[ttt] = data[offset++];
					ttt++;
				}

				String uuid = new String(uuidBuf);
				beacon.setUuid(uuid);

				byte[] majorBuf = new byte[2];
				majorBuf[0] = data[offset];
				majorBuf[1] = data[offset + 1];
				beacon.setMajor((short) ByteUtil.byte2UShort(majorBuf));
				offset += 2;

				byte[] minorBuf = new byte[2];
				minorBuf[0] = data[offset];
				minorBuf[1] = data[offset + 1];
				beacon.setMinor((short) ByteUtil.byte2UShort(minorBuf));
				offset += 2;

				beacon.setRssi((short) ByteUtil.byteToInt16(data[offset]));
				offset++;

				beacons.add(beacon);
			}
			posInfo.setBeacons(beacons);

			posLst.add(posInfo);

			currentNum++;
		}
		this.parse_result = posLst;
	}

	private void ParseCapacity(byte[] data, int len, ID_XBIT bitX, int tagidBit) {
		if (len < 1) {
			return;
		}
		int tagNum = data[0] & 0xFF;
		if (len < 1 + tagNum * 4) {
			return;
		}

		List<TCapacityInfo> capLst = new ArrayList<>();
		int currentNum = 0;
		int offset = 1;
		while (currentNum < tagNum) {
			TCapacityInfo cap = new TCapacityInfo();

			long tagId = 0L;
			if (bitX == ID_XBIT.ID_16BIT) {

				byte[] tagbuff = new byte[2];
				tagbuff[0] = data[offset];
				tagbuff[1] = data[offset + 1];
				offset += 2;
				tagId = ByteUtil.byte2UShort(tagbuff);
			} else if (bitX == ID_XBIT.ID_32BIT) {
				if (tagidBit == 32) {
					byte[] tagbuff = new byte[4];
					tagbuff[0] = data[offset];
					tagbuff[1] = data[offset + 1];
					tagbuff[2] = data[offset + 2];
					tagbuff[3] = data[offset + 3];
					offset += 4;
					tagId = ByteUtil.bytes2Int(tagbuff);
				} else {
					byte[] tagbuff = new byte[8];
					for (int i = 0; i < 8; i++) {
						tagbuff[i] = data[offset + i];
					}
					offset += 8;
					tagId = ByteUtil.bytes2Long(tagbuff);
				}
			}

			cap.setTagId(Long.valueOf(tagId));

			cap.setCapacity(Integer.valueOf(ByteUtil.byteToInt16(data[offset])));
			offset++;

			cap.setCharged((ByteUtil.byteToInt16(data[offset]) == 1));
			offset++;

			capLst.add(cap);
			currentNum++;
		}
		this.parse_result = capLst;
	}

	private void ParseAlarm(byte[] data, int len, Boolean isSimpleAlarm, ID_XBIT bitX, int tagidBit) {
		if (len < 131) {
			return;
		}
		int alarmType = data[0] & 0xFF;
		int offset = 1;

		long tagId = 0L;
		if (bitX == ID_XBIT.ID_16BIT) {

			byte[] tagbuff = new byte[2];
			tagbuff[0] = data[offset];
			tagbuff[1] = data[offset + 1];
			offset += 2;
			tagId = ByteUtil.byte2UShort(tagbuff);
		} else if (bitX == ID_XBIT.ID_32BIT) {
			if (tagidBit == 32) {
				byte[] tagbuff = new byte[4];
				tagbuff[0] = data[offset];
				tagbuff[1] = data[offset + 1];
				tagbuff[2] = data[offset + 2];
				tagbuff[3] = data[offset + 3];
				offset += 4;
				tagId = ByteUtil.bytes2Int(tagbuff);
			} else {
				byte[] tagbuff = new byte[8];
				for (int j = 0; j < 8; j++) {
					tagbuff[j] = data[offset + j];
				}
				offset += 8;
				tagId = ByteUtil.bytes2Long(tagbuff);
			}
		}

		byte[] timebuff = new byte[8];
		for (int i = 0; i < 8; i++) {
			timebuff[i] = data[offset + i];
		}
		Long alarmTime = Long.valueOf(ByteUtil.bytes2Long(timebuff));
		offset += 8;

		if (isSimpleAlarm.booleanValue()) {
			TSimpleAlarmInfo alarm = new TSimpleAlarmInfo();
			alarm.setAlarmType((short) alarmType);
			alarm.setRelatedTagId(Long.valueOf(tagId));
			alarm.setAlarmTime(alarmTime);
			byte[] descbuff = new byte[119];
			for (int j = 0; j < 119; j++)
				descbuff[j] = data[offset + j];
			alarm.setAlarmDesc(ByteUtil.bytesToUnicodeString(descbuff));
			this.parse_result = alarm;
		} else {
			TRichAlarmInfo alarm = new TRichAlarmInfo();
			alarm.setAlarmType((short) alarmType);
			alarm.setRelatedTagId(Long.valueOf(tagId));
			alarm.setAlarmTime(alarmTime);

			alarm.getClass();
			//TRichAlarmInfo.EEfenceMore fence = new TRichAlarmInfo.EEfenceMore(alarm);
			TRichAlarmInfo.EEfenceMore fence = new TRichAlarmInfo.EEfenceMore(alarm);			
			
			if (alarmType == 1 || alarmType == 5 || alarmType == 22 || alarmType == 23) {

				byte[] fenceidbuff = new byte[8];
				for (int i5 = 0; i5 < 8; i5++) {
					fenceidbuff[i5] = data[offset + i5];
				}
				fence.id = Long.valueOf(ByteUtil.bytes2Long(fenceidbuff));
			}
			offset += 8;

			byte[] posXBuff = new byte[4];
			for (int j = 0; j < 4; j++) {
				posXBuff[j] = data[offset + j];
			}
			alarm.setCurrentPosX(ByteUtil.bytes2Int(posXBuff) / 100.0F);
			offset += 4;

			byte[] posYBuff = new byte[4];
			for (int k = 0; k < 4; k++) {
				posYBuff[k] = data[offset + k];
			}
			alarm.setCurrentPosY(ByteUtil.bytes2Int(posYBuff) / 100.0F);
			offset += 4;

			byte[] cameraIdBuff = new byte[30];
			for (int m = 0; m < 30; m++) {
				cameraIdBuff[m] = data[offset + m];
			}
			alarm.setRelatedCameraId(ByteUtil.bytesToUnicodeString(cameraIdBuff));
			offset += 30;

			if (alarmType == 1 || alarmType == 5 || alarmType == 22 || alarmType == 23) {

				byte[] fenceNameBuff = new byte[34];
				for (int i5 = 0; i5 < 34; i5++) {
					fenceNameBuff[i5] = data[offset + i5];
				}
				fence.fenceName = ByteUtil.bytesToUnicodeString(fenceNameBuff);
				alarm.setEefenceMore(fence);
			}
			offset += 34;

			byte[] alarm_id = new byte[8];
			for (int n = 0; n < 8; n++) {
				alarm_id[n] = data[offset + n];
			}
			alarm.setAlarmId(String.valueOf(ByteUtil.bytes2Long(alarm_id)));

			offset += 8;
			byte[] camera_ip = new byte[15];
			for (int i1 = 0; i1 < 15; i1++) {
				camera_ip[i1] = data[offset + i1];
			}
			String ip_str = new String(camera_ip);
			String ip_trimed = ip_str.replaceAll("[\000-\037]", "");
			alarm.setRelatedCameraIp(ip_trimed);

			offset += 15;
			byte[] camera_port = new byte[2];
			for (int i2 = 0; i2 < 2; i2++) {
				camera_port[i2] = data[offset + i2];
			}
			alarm.setRelatedCameraPort(ByteUtil.byte2UShort(camera_port));

			offset += 2;
			byte[] camera_channel = new byte[2];
			for (int i3 = 0; i3 < 2; i3++) {
				camera_channel[i3] = data[offset + i3];
			}
			alarm.setRelatedCameraChannel(ByteUtil.byte2Short(camera_channel));

			offset += 2;
			byte[] floorId = new byte[2];
			for (int i4 = 0; i4 < 2; i4++) {
				floorId[i4] = data[offset + i4];
			}
			alarm.setFloorId(ByteUtil.byte2Short(floorId));
			offset += 2;

			offset += 10;

			int hasDesc = data[offset++] & 0xFF;
			if (hasDesc == 2) {

				byte[] alarmDescLengthByte = new byte[2];
				for (int i5 = 0; i5 < 2; i5++) {
					alarmDescLengthByte[i5] = data[offset + i5];
				}
				int alarmDescLength = ByteUtil.byte2UShort(alarmDescLengthByte);
				offset += 2;

				byte[] alarmDescDataByte = new byte[alarmDescLength];
				for (int i6 = 0; i6 < alarmDescLength; i6++) {
					alarmDescDataByte[i6] = data[offset + i6];
				}
				alarm.setAlarmDesc(ByteUtil.bytesToUnicodeString(alarmDescDataByte));
			}
			this.parse_result = alarm;
		}
	}

	private int personStatistics_all_frame = 0;
	private int personStatistics_this_frame = 0;
	private static TPersonStatistics personStatisticsCache = null;

	private void ParsePersonStatistics(byte[] data, int len, ID_XBIT bitX, int tagidBit) {
		if (len < 1) {
			return;
		}
		TPersonStatistics sta = new TPersonStatistics();

		int offset = 0;
		byte[] twobuffer = new byte[2];

		twobuffer[0] = data[offset];
		twobuffer[1] = data[offset + 1];
		this.personStatistics_all_frame = ByteUtil.byte2UShort(twobuffer);
		offset += 2;

		twobuffer[0] = data[offset];
		twobuffer[1] = data[offset + 1];
		this.personStatistics_this_frame = ByteUtil.byte2UShort(twobuffer);
		offset += 2;

		twobuffer[0] = data[offset];
		twobuffer[1] = data[offset + 1];
		sta.setTotalCount(ByteUtil.byte2UShort(twobuffer));

		offset += 2;
		twobuffer[0] = data[offset];
		twobuffer[1] = data[offset + 1];
		sta.setOnlineCount(ByteUtil.byte2UShort(twobuffer));

		offset += 2;
		int mapCount = data[offset] & 0xFF;
		if (mapCount > 0) {
			sta.detailResult = new ArrayList<>();
		}
		offset++;

		for (int i = 0; i < mapCount; i++) {
			sta.getClass();
			TPersonStatistics.StatisticOneReg oneReg = new TPersonStatistics.StatisticOneReg(sta);
			twobuffer[0] = data[offset];
			twobuffer[1] = data[offset + 1];
			oneReg.setFloorId(ByteUtil.byte2UShort(twobuffer));
			offset += 2;

			int mapNameLen = data[offset] & 0xFF;
			offset++;
			byte[] nameBuff = new byte[mapNameLen];
			int k;
			for (k = 0; k < mapNameLen; k++) {
				nameBuff[k] = data[offset + k];
			}
			oneReg.mapName = ByteUtil.bytesToUnicodeString(nameBuff);

			offset += mapNameLen;
			twobuffer[0] = data[offset];
			twobuffer[1] = data[offset + 1];
			oneReg.setOnlineCount(ByteUtil.byte2UShort(twobuffer));
			if (oneReg.getOnlineCount() > 0) {
				oneReg.onlineTags = new ArrayList<>();
			}
			offset += 2;
			if (tagidBit == 32) {
				for (k = 0; k < oneReg.getOnlineCount(); k++) {
					byte[] tagbuff = new byte[4];
					tagbuff[0] = data[offset];
					tagbuff[1] = data[offset + 1];
					tagbuff[2] = data[offset + 2];
					tagbuff[3] = data[offset + 3];
					offset += 4;
					long tagId = ByteUtil.bytes2Int(tagbuff);
					oneReg.onlineTags.add(Long.valueOf(tagId));
				}
			} else {
				for (k = 0; k < oneReg.getOnlineCount(); k++) {
					byte[] tagbuff = new byte[8];
					for (int l = 0; l < 8; l++) {
						tagbuff[l] = data[offset + l];
					}
					offset += 8;
					long tagId = ByteUtil.bytes2Long(tagbuff);
					oneReg.onlineTags.add(Long.valueOf(tagId));
				}
			}
			offset += 2;
			sta.detailResult.add(oneReg);
		}

		if (personStatisticsCache == null) {
			personStatisticsCache = new TPersonStatistics();
			personStatisticsCache.detailResult = new ArrayList<>();
			personStatisticsCache.totalCount = sta.totalCount;
			personStatisticsCache.onlineCount = sta.onlineCount;
		}

		personStatisticsCache.detailResult.addAll(personStatisticsCache.detailResult.size(), sta.detailResult);

		if (this.personStatistics_this_frame >= this.personStatistics_all_frame) {

			this.parse_result = personStatisticsCache;
			personStatisticsCache = null;
		}
	}

	private int areaStatistics_all_frame = 0;
	private int areaStatistics_this_frame = 0;
	private static List<TAreaStatics> areaStatisticsCache = null;

	private void ParseAreaStatistics(byte[] data, int len, ID_XBIT bitX, int tagidBit) {
		if (len < 1) {
			return;
		}
		List<TAreaStatics> area_lst = new ArrayList<>();
		int offset = 0;
		byte[] twobuffer = new byte[2];

		twobuffer[0] = data[offset];
		twobuffer[1] = data[offset + 1];
		this.areaStatistics_all_frame = ByteUtil.byte2UShort(twobuffer);
		offset += 2;

		twobuffer[0] = data[offset];
		twobuffer[1] = data[offset + 1];
		this.areaStatistics_this_frame = ByteUtil.byte2UShort(twobuffer);
		offset += 2;
		int area_total = data[offset++];
		for (int index = 0; index < area_total; index++) {

			TAreaStatics sta = new TAreaStatics();

			byte[] idbuff = new byte[8];
			for (int i = 0; i < 8; i++) {
				idbuff[i] = data[offset + i];
			}
			Long id_area = Long.valueOf(ByteUtil.bytes2Long(idbuff));
			offset += 8;
			sta.setnAreaID(id_area);

			byte name_len = data[offset++];

			byte[] name_buf = new byte[name_len + 1];
			for (int j = 0; j < name_len; j++) {
				name_buf[j] = data[offset + j];
			}
			sta.setnAreaName(ByteUtil.bytesToUnicodeString(name_buf));
			offset += name_len;

			byte[] tagNumbuff = new byte[2];
			for (int k = 0; k < 2; k++) {
				tagNumbuff[k] = data[offset + k];
			}
			int tag_num = ByteUtil.byte2UShort(tagNumbuff);
			offset += 2;

			List<TAreaStatics.TagDetail> tagDetailList = new ArrayList<>();
			for (int m = 0; m < tag_num; m++) {
				sta.getClass();
				TAreaStatics.TagDetail tagDetail = new TAreaStatics.TagDetail(sta);

				long tagId = 0L;
				if (bitX == ID_XBIT.ID_16BIT) {

					byte[] tagbuff = new byte[2];
					tagbuff[0] = data[offset];
					tagbuff[1] = data[offset + 1];
					offset += 2;
					tagId = ByteUtil.byte2UShort(tagbuff);
				} else if (bitX == ID_XBIT.ID_32BIT) {
					if (tagidBit == 32) {
						byte[] tagbuff = new byte[4];
						tagbuff[0] = data[offset];
						tagbuff[1] = data[offset + 1];
						tagbuff[2] = data[offset + 2];
						tagbuff[3] = data[offset + 3];
						offset += 4;
						tagId = ByteUtil.bytes2Int(tagbuff);
					} else {
						byte[] tagbuff = new byte[8];
						for (int i6 = 0; i6 < 8; i6++) {
							tagbuff[i6] = data[offset + i6];
						}
						offset += 8;
						tagId = ByteUtil.bytes2Long(tagbuff);
					}
				}
				tagDetail.setTagid(Long.valueOf(tagId));

				byte tagName_Len = data[offset++];

				byte[] tagName_buf = new byte[tagName_Len + 1];
				for (int n = 0; n < tagName_Len; n++)
					tagName_buf[n] = data[offset + n];
				tagDetail.setTagname(ByteUtil.bytesToUnicodeString(tagName_buf));
				offset += tagName_Len;

				byte groupName_Len = data[offset++];

				byte[] groupName_buf = new byte[groupName_Len + 1];
				for (int i1 = 0; i1 < groupName_Len; i1++)
					groupName_buf[i1] = data[offset + i1];
				tagDetail.setGroupname(ByteUtil.bytesToUnicodeString(groupName_buf));
				offset += groupName_Len;

				byte statbuff = data[offset++];
				int stat = ByteUtil.oneByte2Int(statbuff);
				tagDetail.setStat(stat);

				byte[] entrytimebuff = new byte[8];
				for (int i2 = 0; i2 < 8; i2++) {
					entrytimebuff[i2] = data[offset + i2];
				}
				Long entry_time = Long.valueOf(ByteUtil.bytes2Long(entrytimebuff));
				offset += 8;
				tagDetail.setEntry_time(entry_time);

				byte[] leavetimebuff = new byte[8];
				for (int i3 = 0; i3 < 8; i3++) {
					leavetimebuff[i3] = data[offset + i3];
				}
				Long leave_time = Long.valueOf(ByteUtil.bytes2Long(leavetimebuff));
				offset += 8;
				tagDetail.setLeave_time(leave_time);

				byte[] staytimebuff = new byte[4];
				for (int i4 = 0; i4 < 4; i4++) {
					staytimebuff[i4] = data[offset + i4];
				}
				int stay_time = ByteUtil.bytes2Int(staytimebuff);
				tagDetail.setStay_time(stay_time);
				offset += 4;

				byte[] isRelevanceDmbuff = new byte[4];
				for (int i5 = 0; i5 < 4; i5++) {
					isRelevanceDmbuff[i5] = data[offset + i5];
				}
				int isRelevanceDm = ByteUtil.bytes2Int(isRelevanceDmbuff);
				tagDetail.setIsRelevanceDm(isRelevanceDm);
				offset += 4;

				tagDetailList.add(tagDetail);
			}

			sta.setTag_rtls(tagDetailList);
			area_lst.add(sta);
		}
		if (areaStatisticsCache == null) {
			areaStatisticsCache = new ArrayList<>();
		}
		areaStatisticsCache.addAll(areaStatisticsCache.size(), area_lst);
		if (this.areaStatistics_this_frame >= this.areaStatistics_all_frame) {
			this.parse_result = areaStatisticsCache;
			areaStatisticsCache = null;
		}
	}

	private List<TAreaStatics> combineAreaStatistics() {
		List<TAreaStatics> areaStatistics = new ArrayList<>();
		areaStatistics.addAll(areaStatisticsCache);
		Map<String, TAreaStatics> tmMap = new HashMap<>();
		for (TAreaStatics areaStatics : areaStatistics) {
			String areaid = areaStatics.getnAreaID().toString();
			if (tmMap.containsKey(areaid)) {

				((TAreaStatics) tmMap.get(areaid)).getTag_rtls().addAll(areaStatics.getTag_rtls());

				continue;
			}
			tmMap.put(areaid, areaStatics);
		}

		areaStatistics = new ArrayList<>(tmMap.values());
		return areaStatistics;
	}

	private void ParseUpdate(byte[] data, int len, ID_XBIT idbit, int tagidBit) {
		if (len < 1) {
			return;
		}
		TUpdateInfo myUpdate = new TUpdateInfo();
		int offset = 0;

		myUpdate.setType(data[offset] & 0xFF);
		offset++;
		myUpdate.setHandle_type(data[offset] & 0xFF);
		offset++;
		byte[] idBytes = new byte[2];
		idBytes[0] = data[offset];
		offset++;
		idBytes[1] = data[offset];
		myUpdate.setId_(String.valueOf(ByteUtil.byte2UShort(idBytes)));

		this.parse_result = myUpdate;
	}

	private void ParseExtendedInfo(byte[] data, int len, ID_XBIT idbit, int tagidBit) {
		if (len < 1) {
			return;
		}
		TExtendedInfo extend = new TExtendedInfo();
		int offset = 0;

		long tagId = 0L;
		if (idbit == ID_XBIT.ID_16BIT) {

			byte[] tagbuff = new byte[2];
			tagbuff[0] = data[offset];
			tagbuff[1] = data[offset + 1];
			offset += 2;
			tagId = ByteUtil.byte2UShort(tagbuff);
		} else if (idbit == ID_XBIT.ID_32BIT) {
			if (tagidBit == 32) {
				byte[] tagbuff = new byte[4];
				tagbuff[0] = data[offset];
				tagbuff[1] = data[offset + 1];
				tagbuff[2] = data[offset + 2];
				tagbuff[3] = data[offset + 3];
				offset += 4;
				tagId = ByteUtil.bytes2Int(tagbuff);
			} else {
				byte[] tagbuff = new byte[8];
				for (int k = 0; k < 8; k++) {
					tagbuff[k] = data[offset + k];
				}
				offset += 8;
				tagId = ByteUtil.bytes2Long(tagbuff);
			}
		}
		extend.setTagid(Long.valueOf(tagId));

		byte[] lenBytes = new byte[2];
		lenBytes[0] = data[offset];
		offset++;
		lenBytes[1] = data[offset];
		offset++;
		int info_len = ByteUtil.byte2UShort(lenBytes);

		extend.setType(data[offset] & 0xFF);
		offset++;

		int val_len = info_len - 9;
		byte[] valBytes = new byte[val_len];

		for (int i = 0; i < val_len; i++) {
			valBytes[i] = data[offset + i];
		}
		extend.setVal(ByteUtil.bytesToHexString(valBytes));
		offset += val_len;

		byte[] timebuff = new byte[8];
		for (int j = 0; j < 8; j++) {
			timebuff[j] = data[offset + j];
		}
		Long alarmTime = Long.valueOf(ByteUtil.bytes2Long(timebuff));
		extend.setTime_(alarmTime.longValue());

		this.parse_result = extend;
	}

	public void ParseAttendanceStatice(byte[] data, int len, ID_XBIT idbit, int tagidBit) {
		if (len < 1)
			return;
		TAttendanceStatics attendanceStatics = new TAttendanceStatics();
		int offset = 0;

		long tagId = 0L;
		if (idbit == ID_XBIT.ID_16BIT) {
			byte[] tagbuff = new byte[2];
			tagbuff[0] = data[offset];
			tagbuff[1] = data[offset + 1];
			offset += 2;
			tagId = ByteUtil.byte2UShort(tagbuff);
		} else if (idbit == ID_XBIT.ID_32BIT) {
			if (tagidBit == 32) {
				byte[] tagbuff = new byte[4];
				tagbuff[0] = data[offset];
				tagbuff[1] = data[offset + 1];
				tagbuff[2] = data[offset + 2];
				tagbuff[3] = data[offset + 3];
				offset += 4;
				tagId = ByteUtil.bytes2Int(tagbuff);
			} else {
				byte[] tagbuff = new byte[8];
				for (int i1 = 0; i1 < 8; i1++) {
					tagbuff[i1] = data[offset + i1];
				}
				offset += 8;
				tagId = ByteUtil.bytes2Long(tagbuff);
			}
		}
		attendanceStatics.setRelatedTagId(Long.valueOf(tagId));

		byte[] tagNamelenbuff = new byte[2];
		tagNamelenbuff[0] = data[offset];
		tagNamelenbuff[1] = data[offset + 1];
		offset += 2;
		int tagNamelen = ByteUtil.byte2UShort(tagNamelenbuff);
		byte[] tagNamebuff = new byte[tagNamelen];
		for (int i = 0; i < tagNamelen; i++) {
			tagNamebuff[i] = data[offset + i];
		}
		offset += tagNamelen;
		attendanceStatics.setRelatedTagName(ByteUtil.bytesToUnicodeString(tagNamebuff));

		byte[] areaIdbuff = new byte[8];
		for (int j = 0; j < 8; j++) {
			areaIdbuff[j] = data[offset + j];
		}
		offset += 8;
		Long areaId = Long.valueOf(ByteUtil.bytes2Long(areaIdbuff));
		attendanceStatics.setAreaId(areaId.longValue());

		byte[] areaNamelenbuff = new byte[2];
		areaNamelenbuff[0] = data[offset];
		areaNamelenbuff[1] = data[offset + 1];
		offset += 2;
		int areaNamelen = ByteUtil.byte2UShort(areaNamelenbuff);
		byte[] areaNamebuff = new byte[areaNamelen];
		for (int k = 0; k < areaNamelen; k++) {
			areaNamebuff[k] = data[offset + k];
		}
		offset += areaNamelen;
		attendanceStatics.setAreaName(ByteUtil.bytesToUnicodeString(areaNamebuff));

		int mapId = 0;
		byte[] mapbuff = new byte[2];
		mapbuff[0] = data[offset];
		mapbuff[1] = data[offset + 1];
		offset += 2;
		mapId = ByteUtil.byte2UShort(mapbuff);
		attendanceStatics.setMapId(mapId);

		byte[] mapNamelenbuff = new byte[2];
		mapNamelenbuff[0] = data[offset];
		mapNamelenbuff[1] = data[offset + 1];
		offset += 2;
		int mapNamelen = ByteUtil.byte2UShort(mapNamelenbuff);
		byte[] mapNamebuff = new byte[mapNamelen];
		for (int m = 0; m < mapNamelen; m++) {
			mapNamebuff[m] = data[offset + m];
		}
		offset += mapNamelen;
		attendanceStatics.setMapName(ByteUtil.bytesToUnicodeString(mapNamebuff));

		byte statbuff = data[offset];
		int stat = ByteUtil.oneByte2Int(statbuff);
		offset++;
		attendanceStatics.setStat(stat);

		byte[] timebuff = new byte[8];
		for (int n = 0; n < 8; n++) {
			timebuff[n] = data[offset + n];
		}
		offset += 8;
		Long timestamp = Long.valueOf(ByteUtil.bytes2Long(timebuff));
		attendanceStatics.setTimestamp(timestamp.longValue());

		this.parse_result = attendanceStatics;
	}

	public void ParseBaseState(byte[] data, int len, ID_XBIT idbit, int tagidBit) {
		if (len < 1)
			return;
		TBaseState tBaseState = new TBaseState();
		int offset = 0;

		byte baseNumBuff = data[offset];
		int baseNum = ByteUtil.oneByte2Int(baseNumBuff);
		offset++;
		tBaseState.setBaseNum(baseNum);
		List<TBaseState.BaseInfo> base_lst = new ArrayList<>();
		for (int i = 0; i < baseNum; i++) {
			tBaseState.getClass();
			TBaseState.BaseInfo baseInfo = new TBaseState.BaseInfo(tBaseState);

			int baseId = 0;
			if (idbit == ID_XBIT.ID_16BIT) {
				byte[] basebuff = new byte[2];
				basebuff[0] = data[offset];
				basebuff[1] = data[offset + 1];
				offset += 2;
				baseId = ByteUtil.byte2UShort(basebuff);
			} else if (idbit == ID_XBIT.ID_32BIT) {
				byte[] basebuff = new byte[4];
				basebuff[0] = data[offset];
				basebuff[1] = data[offset + 1];
				basebuff[2] = data[offset + 2];
				basebuff[3] = data[offset + 3];
				offset += 4;
				baseId = ByteUtil.bytes2Int(basebuff);
			}
			baseInfo.setBaseId(baseId);

			byte baseStateBuff = data[offset];
			int baseState = ByteUtil.oneByte2Int(baseStateBuff);
			offset++;
			baseInfo.setBaseState(baseState);

			byte[] posXbuff = new byte[4];
			posXbuff[0] = data[offset];
			posXbuff[1] = data[offset + 1];
			posXbuff[2] = data[offset + 2];
			posXbuff[3] = data[offset + 3];
			baseInfo.setPosX(ByteUtil.bytes2Int(posXbuff) / 100.0F);
			offset += 4;

			byte[] posYbuff = new byte[4];
			posYbuff[0] = data[offset];
			posYbuff[1] = data[offset + 1];
			posYbuff[2] = data[offset + 2];
			posYbuff[3] = data[offset + 3];
			baseInfo.setPosY(ByteUtil.bytes2Int(posYbuff) / 100.0F);
			offset += 4;

			byte[] posZbuff = new byte[2];
			posZbuff[0] = data[offset];
			posZbuff[1] = data[offset + 1];
			baseInfo.setPosZ(ByteUtil.byte2Short(posZbuff) / 100.0F);
			offset += 2;

			byte regidBuff = data[offset];
			int regid = ByteUtil.oneByte2Int(regidBuff);
			offset++;
			baseInfo.setRegid(regid);
			base_lst.add(baseInfo);
		}
		tBaseState.setBase_rtls(base_lst);
		this.parse_result = tBaseState;
	}

	private static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
}