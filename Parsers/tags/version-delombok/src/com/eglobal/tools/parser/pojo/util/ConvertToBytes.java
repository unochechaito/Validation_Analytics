package com.eglobal.tools.parser.pojo.util;

import java.nio.ByteBuffer;

/**
 * 
 * @author egldt1029
 *
 */
public class ConvertToBytes {
	public static byte[] intTo2Bytes(int num) {
		String hexString = String.format("%4s", Integer.toHexString(num)).replace(' ', '0');
		ByteBuffer bf = ByteBuffer.allocate(2);
		for (int i = 0; i < 4; i += 2) {
			bf.put((byte) Integer.parseInt(hexString.substring(i, i + 2), 16));
		}
		return bf.array();
	}
	
	public static byte[] intTo4Bytes(int num) {
		String hexString = String.format("%4s", Integer.toHexString(num)).replace(' ', '0');
		hexString = String.format("%-8s", hexString).replace(' ', '0');
		ByteBuffer bf = ByteBuffer.allocate(4);
		for (int i = 0; i < 8; i += 2) {
			bf.put((byte) Integer.parseInt(hexString.substring(i, i + 2), 16));
		}
		return bf.array();
	}
}

