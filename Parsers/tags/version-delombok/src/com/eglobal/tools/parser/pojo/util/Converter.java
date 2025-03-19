package com.eglobal.tools.parser.pojo.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class Converter {
	/***/
	private static final short[] ebcdicToAsciiTable = { 0, 1, 2, 3, 127, 9, 127, 127, 127, 127, 127, 11, 12, 13, 14, 15,
			16, 17, 18, 19, 127, 127, 8, 127, 24, 25, 127, 127, 127, 29, 127, 31, 127, 127, 28, 127, 127, 10, 23, 27,
			127, 127, 127, 127, 127, 5, 6, 7, 127, 127, 22, 127, 127, 30, 127, 4, 127, 127, 127, 127, 20, 21, 127, 26,
			32, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 46, 60, 40, 43, 127, 38, 127, 127, 127, 127, 127, 127,
			127, 127, 127, 33, 36, 42, 41, 59, 94, 45, 47, 127, 127, 127, 127, 127, 127, 127, 127, 124, 44, 37, 95, 62,
			63, 127, 127, 127, 127, 127, 127, 127, 127, 127, 96, 58, 35, 64, 39, 61, 34, 127, 97, 98, 99, 100, 101, 102,
			103, 104, 105, 127, 123, 127, 127, 127, 127, 127, 106, 107, 108, 109, 110, 111, 112, 113, 114, 127, 125,
			127, 127, 127, 127, 127, 126, 115, 116, 117, 118, 119, 120, 121, 122, 127, 127, 127, 91, 127, 127, 127, 127,
			127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 93, 127, 127, 127, 65, 66, 67, 68, 69, 70, 71, 72,
			73, 127, 127, 127, 127, 127, 127, 127, 74, 75, 76, 77, 78, 79, 80, 81, 82, 127, 127, 127, 127, 127, 127, 92,
			127, 83, 84, 85, 86, 87, 88, 89, 90, 127, 127, 127, 127, 127, 127, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57,
			127, 127, 127, 127, 127, 127 };

	private static final short[] asciiToEbcdicTable = { 0, 1, 2, 3, 55, 45, 46, 47, 22, 5, 37, 11, 12, 13, 14, 15, 16,
			17, 18, 19, 60, 61, 50, 38, 24, 25, 63, 39, 34, 29, 53, 31, 64, 90, 127, 123, 91, 108, 80, 125, 77, 93, 92,
			78, 107, 96, 75, 97, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 122, 94, 76, 126, 110, 111, 124, 193,
			194, 195, 196, 197, 198, 199, 200, 201, 209, 210, 211, 212, 213, 214, 215, 216, 217, 226, 227, 228, 229,
			230, 231, 232, 233, 173, 224, 189, 95, 109, 121, 129, 130, 131, 132, 133, 134, 135, 136, 137, 145, 146, 147,
			148, 149, 150, 151, 152, 153, 162, 163, 164, 165, 166, 167, 168, 169, 139, 106, 155, 161, 7 };

	public static String byteAsHexString(byte b) {
		return String.format("%02X", b);
	}

	public static String byteArrayAsHexString(byte byteArray[]) {
		StringBuilder byteString = new StringBuilder();
		for (int i = 0; i < byteArray.length; i++) {
			byteString.append(byteAsHexString(byteArray[i]));
		}
		return byteString.toString();
	}

	public static byte hexStringByteToByte(String stringByte) {
		return (byte) Integer.parseInt(stringByte, 16);
	}

	public static byte[] hexStringBytetoByteArray(String stringByteArray) {
		ByteBuffer buffer = ByteBuffer.allocate(stringByteArray.length() / 2);
		for (int i = 0; i < stringByteArray.length(); i += 2) {
			buffer.put(hexStringByteToByte(stringByteArray.substring(i, i + 2)));
		}
		return buffer.array();
	}

	public static String hexByteStringToAscii(String hexByteString) {
		StringBuilder asciiString = new StringBuilder();
		for (int i = 0; i < hexByteString.length(); i += 2) {
			asciiString.append((char) (hexStringByteToByte(hexByteString.substring(i, i + 2))));
		}
		return asciiString.toString();
	}

	public static String asciiAsHexByteString(String asciiString) {
		StringBuilder buildHexByteStringBuilder = new StringBuilder();
		for (int i = 0; i < asciiString.length(); i++) {
			byte b = (byte) asciiString.charAt(i);
			buildHexByteStringBuilder.append(byteAsHexString(b));
		}
		return buildHexByteStringBuilder.toString();
	}

	public static String ebcdicStringToAsciiString(String ebcdicString) {
		return new String(ebcdicToAscii(hexStringBytetoByteArray(ebcdicString)));
	}

	public static String asciiStringToEbcdicString(String asciiString) {
		return byteArrayAsHexString(asciiToEbcdic(asciiString.getBytes()));
	}

	public static byte[] ebcdicToAscii(byte[] ebcdicArray) {
		byte[] arrayToReturn = new byte[ebcdicArray.length];
		for (int i = 0; i < ebcdicArray.length; i++) {
			int arrayPointer = 0xFF & ebcdicArray[i];
			arrayToReturn[i] = (byte) ebcdicToAsciiTable[arrayPointer];
		}
		return arrayToReturn;
	}

	public static byte[] asciiToEbcdic(byte[] asciiArray) {
		byte[] arrayToReturn = new byte[asciiArray.length];
		for (int i = 0; i < asciiArray.length; i++) {
			int arrayPointer = asciiArray[i] % 128;
			arrayToReturn[i] = (byte) asciiToEbcdicTable[arrayPointer];
		}
		return arrayToReturn;
	}

	public static String bcdValue(byte[] bcdBytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bcdBytes) {
			sb.append(bcdValue(b));
		}
		return sb.toString();
	}

	public static String asciiValue(byte[] asciiBytes) {
		try {
			return new String(asciiBytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {

		}
		return null;
	}

	public static String bcdValue(byte value) {
		return String.format("%02d", 10 * (0x0f & (value >> 4)) + (0x0f & value));
	}
}
