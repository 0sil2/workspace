package com.tsingoal.com;

import java.io.UnsupportedEncodingException;

public class AccessUtil {
	public static byte[] CreateAccessData(String userName, String password) {
		byte[] data = null;
		try {
			int userNameLen = (userName.getBytes("GB2312")).length;
			int passwordLen = (password.getBytes()).length;
			data = new byte[15 + userNameLen + password.length()];
			data[0] = -52;
			data[1] = 95;
			data[2] = 39;

			byte[] userlenBuff = ByteUtil.int2Bytes(userNameLen);
			int offset = 3;
			for (int i = 0; i < 4; i++)
				data[i + offset] = userlenBuff[i];
			offset += 4;

			byte[] userbuff = userName.getBytes("GB2312");
			for (int j = 0; j < userNameLen; j++) {
				data[j + offset] = userbuff[j];
			}
			offset += userNameLen;

			byte[] passwdlenBuff = ByteUtil.int2Bytes(passwordLen);
			for (int k = 0; k < 4; k++)
				data[k + offset] = passwdlenBuff[k];
			offset += 4;

			byte[] passwdbuff = password.getBytes();
			for (int m = 0; m < passwordLen; m++) {
				data[m + offset] = passwdbuff[m];
			}
			offset += passwordLen;

			byte[] crcbuff = new byte[offset - 2];
			for (int n = 0; n < crcbuff.length; n++) {
				crcbuff[n] = data[n + 2];
			}
			int crc = Crc16.calcCrc16(crcbuff);
			byte[] crcres = ByteUtil.int2Bytes(crc);
			data[offset] = crcres[2];
			data[offset + 1] = crcres[3];
			offset += 2;

			data[offset] = -86;
			data[offset + 1] = -69;

			return data;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();

			return data;
		}
	}
}