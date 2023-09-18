package com.tsingoal.com;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteUtil {
	private static String hexString = "0123456789ABCDEF";

	public static String string2Hex(String content) {
		byte[] bytes = content.getBytes();
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			sb.append(hexString.charAt((bytes[i] & 0xF0) >> 4));
			sb.append(hexString.charAt((bytes[i] & 0xF) >> 0));
		}
		return sb.toString();
	}

	public static String hexStr2Str(String hexStr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];

		for (int i = 0; i < bytes.length; i++) {

			int n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xFF);
		}
		return new String(bytes);
	}

	public static String toStringHex(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {

			try {
				baKeyword[i] = (byte) (0xFF & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

		try {
			s = new String(baKeyword, "utf-8");
		} catch (Exception e1) {

			e1.printStackTrace();
		}
		return s;
	}

	public static byte[] hexStringToByte(String hex) {
		int len = hex.length() / 2;
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	public static String bytesToHexString(byte[] src) {
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
		return stringBuilder.toString().toUpperCase();
	}

	public static String decode(String bytes) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);

		for (int i = 0; i < bytes.length(); i += 2)
			baos.write(hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1)));
		return new String(baos.toByteArray());
	}

	private static byte toByte(char c) {
		byte b = (byte) hexString.indexOf(c);
		return b;
	}

	public static byte[] int2Bytes(int num) {
		byte[] byteNum = new byte[4];
		for (int ix = 0; ix < 4; ix++) {
			int offset = 32 - (ix + 1) * 8;
			byteNum[ix] = (byte) (num >> offset & 0xFF);
		}
		return byteNum;
	}

	public static byte[] intToHexToBytes(short num) {
		byte[] byteNum = { (byte) (num >> 8 & 0xFF), (byte) (num & 0xFF) };
		return byteNum;
	}

	public static byte intToHexToByte(int integer) {
		String hexStr = Integer.toHexString(integer);
		return Byte.valueOf(hexStr, 16).byteValue();
	}

	public static byte int2OneByte(int num) {
		return (byte) (num & 0xFF);
	}

	public static int twoBytes2Int(byte[] buffer) {
		return buffer[0] | buffer[1] << 8;
	}

	public static int oneByte2Int(byte byteNum) {
		return byteNum & 0xFF;
	}

	public static int byteToInt16(byte b) {
		String result = Integer.toHexString(b & 0xFF);
		return Integer.valueOf(result, 16).intValue();
	}

	public static int bytes2Int(byte[] byteNum) {
		int num = 0;
		for (int ix = 0; ix < 4; ix++) {
			num <<= 8;
			num |= byteNum[ix] & 0xFF;
		}
		return num;
	}

	public static int byte2UShort(byte[] byteNum) {
		int num = 0;
		for (int ix = 0; ix < 2; ix++) {
			num <<= 8;
			num |= byteNum[ix] & 0xFF;
		}
		return num;
	}

	public static short byte2Short(byte[] byteNum) {
		return (short) ((byteNum[0] & 0xFF) << 8 | 0xFF & byteNum[1]);
	}

	public static byte[] long2Bytes(long num) {
		byte[] byteNum = new byte[8];
		for (int ix = 0; ix < 8; ix++) {
			int offset = 64 - (ix + 1) * 8;
			byteNum[ix] = (byte) (int) (num >> offset & 0xFFL);
		}
		return byteNum;
	}

	public static long bytes2Long(byte[] byteNum) {
		long num = 0L;
		for (int ix = 0; ix < 8; ix++) {
			num <<= 8L;
			num |= (byteNum[ix] & 0xFF);
		}
		return num;
	}

	public static float byte162float(byte[] num) {
		String hexString = bytesToHexString(num);
		Integer temp = Integer.valueOf(hexString.trim(), 16);
		float value = Float.intBitsToFloat(temp.intValue());

		return value;
	}

	public static byte[] float2ByteArray(float value) {
		return ByteBuffer.allocate(4).putFloat(value).array();
	}

	public static float bytes2float(byte[] b, int index) {
		int l = b[index + 0];
		l &= 0xFF;
		l = (int) (l | b[index + 1] << 8L);
		l &= 0xFFFF;
		l = (int) (l | b[index + 2] << 16L);
		l &= 0xFFFFFF;
		l = (int) (l | b[index + 3] << 24L);
		return Float.intBitsToFloat(l);
	}

	public static boolean byteCompare(byte[] data1, byte[] data2, int len) {
		if (data1 == null && data2 == null) {
			return true;
		}
		if (data1 == null || data2 == null) {
			return false;
		}
		if (data1 == data2) {
			return true;
		}
		boolean bEquals = true;

		for (int i = 0; i < data1.length && i < data2.length && i < len; i++) {
			if (data1[i] != data2[i]) {
				bEquals = false;
				break;
			}
		}
		return bEquals;
	}

	public static String byteToBit(byte b) {
		return "" + (byte) (b >> 7 & 0x1) + (byte) (b >> 6 & 0x1) + (byte) (b >> 5 & 0x1) + (byte) (b >> 4 & 0x1)
				+ (byte) (b >> 3 & 0x1) + (byte) (b >> 2 & 0x1) + (byte) (b >> 1 & 0x1) + (byte) (b >> 0 & 0x1);
	}

	public static void printHexString(byte[] b) {
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			System.out.print(hex.toUpperCase());
		}
	}

	public static boolean ExistOtherChar(String str) {
		String numstr = "0123456789";
		int i = 0;
		for (i = 0; i < str.length(); i++) {

			if (numstr.indexOf(str.charAt(i)) == -1) {
				return true;
			}
		}
		return false;
	}

	public static boolean ExistChar(String str) {
		String regex = "[a-zA-Z]+$";
		return str.matches(regex);
	}

	public static boolean LegalVersion(String str) {
		String regex = "[0-9]+[.][0-9]+[.][0-9]+";
		return str.matches(regex);
	}

	public static byte[] shortToByteArray(short s) {
		byte[] targets = new byte[2];
		for (int i = 0; i < 2; i++) {
			int offset = (targets.length - 1 - i) * 8;
			targets[i] = (byte) (s >>> offset & 0xFF);
		}
		return targets;
	}

	public static String bytesToUnicodeString(byte[] bytes) {
		for (int i = 0; i < bytes.length; i += 2) {
			if (i + 1 == bytes.length) {
				bytes = Arrays.copyOf(bytes, bytes.length - 1);

				break;
			}
			byte temp = bytes[i];
			bytes[i] = bytes[i + 1];
			bytes[i + 1] = temp;
		}
		try {
			String byteStr = new String(bytes, "unicode");
			String result = byteStr.replaceAll("[\000-\037]", "");
			return result;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();

			return null;
		}
	}
}